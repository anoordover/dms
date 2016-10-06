package com.amplexor.ia.worker;

import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.parsing.MessageParser;
import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.configuration.*;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.ingest.ArchiveManager;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.RetentionManager;
import com.amplexor.ia.sip.SipManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
class IAArchiverWorkerThread implements Runnable {
    private SIPPackagerConfiguration mobjConfiguration;
    private int miId;

    private int miProcessedBytes;
    private boolean mbRunning;

    private DocumentSource mobjDocumentSource;
    private MessageParser mobjMessageParser;
    private RetentionManager mobjRetentionManager;
    private CacheManager mobjCacheManager;
    private ArchiveManager mobjArchiveManager;
    private SipManager mobjSipManager;


    public IAArchiverWorkerThread(SIPPackagerConfiguration objConfiguration) {
        this(objConfiguration, 1);
    }

    public IAArchiverWorkerThread(SIPPackagerConfiguration objConfiguration, int iId) {
        mobjConfiguration = objConfiguration;
        miId = iId;
    }


    @Override
    public void run() {
        info(this, "Initializing Worker " + miId);
        info(this, "Setting up ExceptionHelper");
        ExceptionHelper.getExceptionHelper().setExceptionConfiguration(mobjConfiguration.getExceptionConfiguration());
        try {
            if (loadClasses()) {
                mobjDocumentSource.initialize();
                Thread.currentThread().setName("IAWorker-" + miId);
                ExceptionHelper.getExceptionHelper().setDocumentSource(mobjDocumentSource);
                info(this, "Initializing Document Caches");
                mobjCacheManager.initializeCache();
                info(this, "Initializing Archive Manager");
                mobjArchiveManager = new ArchiveManager(mobjConfiguration.getServerConfiguration());
                info(this, "DONE. Starting main loop");
                mbRunning = true;

            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            WorkerManager.getWorkerManager().stop();
        }

        while (mbRunning) {
            if(Thread.currentThread().isInterrupted()) {
                info(this, "Worker-" + miId + " was Interruped");
                break;
            }

            List<IADocument> cDocuments = null;
            String sDocumentData = mobjDocumentSource.retrieveDocumentData();
            if (!"".equals(sDocumentData)) {
                cDocuments = mobjMessageParser.parse(sDocumentData);
            }

            if (cDocuments != null) {
                cDocuments.forEach(objDocument -> {
                    miProcessedBytes += objDocument.getSizeEstimate();
                    debug(this, "Retrieved document with id: " + objDocument.getDocumentId());
                    try {
                        mobjCacheManager.add(objDocument, mobjRetentionManager.retrieveRetentionClass(objDocument));
                        mobjCacheManager.saveCaches();
                    } catch (IllegalArgumentException ex) {
                        IADocumentReference objReference = new IADocumentReference(objDocument.getDocumentId(), null);
                        ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_UNKNOWN_RETENTION, objReference, ex);
                    }
                });
            }

            mobjCacheManager.update();
            mobjCacheManager.getClosedCaches().iterator().forEachRemaining(objCache -> {
                if (mobjSipManager.getSIPFile(objCache) && mobjArchiveManager.ingestSip(objCache)) {
                    info(this, "Successfully Ingested SIP " + objCache.getSipFile().toString());
                    mobjDocumentSource.postResult(objCache.getContents());
                    mobjCacheManager.cleanupCache(objCache);
                }
            });
        }

        info(this, "Saving Caches for worker " + miId);
        mobjCacheManager.saveCaches();
        mobjDocumentSource.shutdown();
        info(this, "Shutting down Worker " + miId);
    }

    public synchronized void stopWorker() {
        info(this, "Worker " + miId + " Received Stop command");
        mbRunning = false;
    }

    private boolean loadClasses() {
        try {
            info(this, "Initializing Cache Manager");
            Object objCacheManager = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getCacheConfiguration().getImplementingClass())
                    .getConstructor(CacheConfiguration.class)
                    .newInstance(mobjConfiguration.getCacheConfiguration());
            if (objCacheManager instanceof CacheManager) {
                info(this, "Successfully loaded Cache Manager " + mobjConfiguration.getCacheConfiguration().getImplementingClass());
                mobjCacheManager = CacheManager.class.cast(objCacheManager);
            }

            info(this, "Initializing Document Source");
            Object objDocumentSource = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getDocumentSource().getImplementingClass())
                    .getConstructor(PluggableObjectConfiguration.class)
                    .newInstance(mobjConfiguration.getDocumentSource());
            if (objDocumentSource instanceof DocumentSource) {
                info(this, "Successfully loaded Document Source: " + mobjConfiguration.getDocumentSource().getImplementingClass());
                mobjDocumentSource = DocumentSource.class.cast(objDocumentSource);
            }

            info(this, "Initializing Message Parser");
            Object objMessageParser = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getMessageParser().getImplementingClass())
                    .getConstructor(PluggableObjectConfiguration.class)
                    .newInstance(mobjConfiguration.getMessageParser());
            if (objMessageParser instanceof MessageParser) {
                info(this, "Successfully loaded Message Parser: " + mobjConfiguration.getMessageParser().getImplementingClass());
                mobjMessageParser = MessageParser.class.cast(objMessageParser);
            }

            info(this, "Initializing Retention Manager");
            Object objRetentionManager = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getRetentionManager().getImplementingClass())
                    .getConstructor(RetentionManagerConfiguration.class)
                    .newInstance(mobjConfiguration.getRetentionManager());
            if (objRetentionManager instanceof RetentionManager) {
                info(this, "Successfully loaded Retention Manager: " + mobjConfiguration.getMessageParser().getImplementingClass());
                mobjRetentionManager = RetentionManager.class.cast(objRetentionManager);
            }

            info(this, "Initializing SIP Manager");
            Object objSipManager = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getSipConfiguration().getImplementingClass())
                    .getConstructor(IASipConfiguration.class)
                    .newInstance(mobjConfiguration.getSipConfiguration());
            if (objSipManager instanceof SipManager) {
                info(this, "Successfully loaded SIP Manager: " + mobjConfiguration.getSipConfiguration().getImplementingClass());
                mobjSipManager = SipManager.class.cast(objSipManager);
            }

            return mobjDocumentSource != null && mobjMessageParser != null && mobjRetentionManager != null;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NullPointerException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            WorkerManager.getWorkerManager().stop();
        }
        return false;
    }

    public synchronized int getProcessedBytes() {
        return miProcessedBytes;
    }

    public synchronized void resetProcessedBytes() {
        miProcessedBytes = 0;
    }
}
