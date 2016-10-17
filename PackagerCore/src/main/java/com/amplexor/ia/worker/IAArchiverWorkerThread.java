package com.amplexor.ia.worker;

import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.*;
import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.ingest.ArchiveManager;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.parsing.MessageParser;
import com.amplexor.ia.retention.RetentionManager;
import com.amplexor.ia.sip.SipManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.error;
import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
class IAArchiverWorkerThread implements Runnable {
    private SIPPackagerConfiguration mobjConfiguration;
    private int miId;
    private Thread mobjShutdownHook;

    private int miProcessedBytes;
    private boolean mbRunning;
    private boolean mbShutdownFlag;
    private boolean mbIngesting;
    private boolean mbIngestFlag;

    private DocumentSource mobjDocumentSource;
    private MessageParser mobjMessageParser;
    private RetentionManager mobjRetentionManager;
    private CacheManager mobjCacheManager;
    private ArchiveManager mobjArchiveManager;
    private SipManager mobjSipManager;

    public IAArchiverWorkerThread(SIPPackagerConfiguration objConfiguration, int iId) {
        mobjConfiguration = objConfiguration;
        miId = iId;
        mbRunning = true;
        mobjShutdownHook = new Thread() {
            @Override
            public void run() {
                info(this, "Waiting for Worker-" + miId + " to shutdown");
                while (isRunning()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                info(this, "Shutdown Hook for Worker-" + miId);
                if (mobjCacheManager != null) {
                    mobjCacheManager.saveCaches();
                }

            }
        };
        Runtime.getRuntime().addShutdownHook(mobjShutdownHook);
    }

    public synchronized boolean isRunning() {
        return mbRunning;
    }

    @Override
    public void run() {
        info(this, "Initializing Worker " + miId);
        info(this, "Setting up ExceptionHelper");
        ExceptionHelper.getExceptionHelper().setExceptionConfiguration(mobjConfiguration.getExceptionConfiguration());
        synchronized (this) {
            mbRunning = initialize();
        }

        while (isRunning()) {
            List<IADocument> cDocuments = null;
            if (!mbIngestFlag) {
                String sDocumentData = mobjDocumentSource.retrieveDocumentData();
                if (!"".equals(sDocumentData)) {
                    cDocuments = mobjMessageParser.parse(sDocumentData);
                }
            }

            if (cDocuments != null) {
                cDocuments.forEach(objDocument -> {
                    miProcessedBytes += objDocument.getSizeEstimate();
                    debug(this, "Retrieved document with id: " + objDocument.getDocumentId());
                    if (objDocument.getErrorCode() != 0) {
                        IADocumentReference objReference = new IADocumentReference(objDocument, null);
                        List<IADocumentReference> cTempReference = new ArrayList<>();
                        cTempReference.add(objReference);
                        mobjDocumentSource.postResult(cTempReference);
                        return;
                    } else {
                        try {
                            mobjCacheManager.add(objDocument, mobjRetentionManager.retrieveRetentionClass(objDocument));
                            mobjCacheManager.saveCaches();
                        } catch (IllegalArgumentException ex) {
                            IADocumentReference objReference = new IADocumentReference(objDocument.getDocumentId(), null);
                            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_UNKNOWN_RETENTION, objReference, ex);
                        }
                    }
                });
            }

            mobjCacheManager.update();
            ingest();
            if (mbIngestFlag) {
                mbIngestFlag = mbRunning = false;
            }

            mbRunning = !mbShutdownFlag;
        }
        mobjDocumentSource.shutdown();
        info(this, "Shutting down Worker " + miId);
    }

    public boolean initialize() {
        try {
            if (loadClasses()) {
                Thread.currentThread().setName("IAWorker-" + miId);

                info(this, "Initializing Document Source");
                if (!mobjDocumentSource.initialize()) {
                    return false;
                }

                info(this, "Initializing Document Caches");
                if (!mobjCacheManager.initializeCache()) {
                    return false;
                }

                info(this, "Initializing Archive Manager");
                mobjArchiveManager = new ArchiveManager(mobjConfiguration.getServerConfiguration());
                info(this, "DONE. Starting main loop");
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            WorkerManager.getWorkerManager().signalStop(ExceptionHelper.ERROR_OTHER);
        }

        return true;
    }

    public void ingest() {
        mbIngesting = true;
        mobjCacheManager.getClosedCaches().iterator().forEachRemaining(objCache -> {
            if (mobjSipManager.getSIPFile(objCache) && mobjArchiveManager.ingestSip(objCache)) {
                info(this, "Successfully Ingested SIP " + objCache.getSipFile().toString());
            } else {
                error(this, "Error in cache " + objCache.getId() + " , Saving to error cache");
                mobjCacheManager.createErrorCache(objCache);
            }
            mobjDocumentSource.postResult(objCache.getContents());
            mobjCacheManager.cleanupCache(objCache);
        });
        mbIngesting = false;
    }

    public synchronized void update() {
        if (mobjCacheManager != null) {
            mobjCacheManager.update();
        }
    }

    public synchronized int getClosedCacheCount() {
        if (mobjCacheManager != null) {
            return mobjCacheManager.getClosedCaches().size();
        }

        return -1;
    }

    public synchronized void setIngestFlag() {
        mbIngestFlag = true;
    }

    public synchronized boolean isIngesting() {
        return mbIngesting;
    }

    public synchronized void stopWorker() {
        info(this, "Worker " + miId + " Received Stop command");
        mbShutdownFlag = true;
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
            WorkerManager.getWorkerManager().signalStop(ExceptionHelper.ERROR_OTHER);
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
