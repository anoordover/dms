package com.amplexor.ia.worker;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.configuration.SIPPackagerConfiguration;
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

    private int miProcessedMessages;
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
        if (loadClasses()) {
            try {
                Thread.currentThread().setName("IAWorker-" + miId);
                info(this, "Setting up ExceptionHelper");
                ExceptionHelper.getExceptionHelper().setExceptionConfiguration(mobjConfiguration.getExceptionConfiguration());
                ExceptionHelper.getExceptionHelper().setDocumentSource(mobjDocumentSource);
                info(this, "Initializing Document Caches");
                mobjCacheManager = new CacheManager(mobjConfiguration.getCacheConfiguration());
                mobjCacheManager.initializeCache();
                info(this, "Initializing Archive Manager");
                mobjArchiveManager = new ArchiveManager(mobjConfiguration.getServerConfiguration());
                info(this, "DONE. Starting main loop");
                mbRunning = true;
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }

        while (mbRunning) {
            List<IADocument> cDocuments = null;
            String sDocumentData = mobjDocumentSource.retrieveDocumentData();
            if (!"".equals(sDocumentData)) {
                ++miProcessedMessages;
                cDocuments = mobjMessageParser.parse(sDocumentData);
            }
            if (cDocuments != null) {
                cDocuments.forEach(objDocument -> {
                    debug(this, "Retrieved document with id: " + objDocument.getDocumentId());
                    try {
                        mobjCacheManager.add(objDocument, mobjRetentionManager.retrieveRetentionClass(objDocument));
                    } catch (IllegalArgumentException ex) {
                        ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_UNKNOWN_RETENTION, objDocument, ex);
                    }
                });
            }
            mobjCacheManager.update();
            mobjCacheManager.getClosedCaches().iterator().forEachRemaining(objCache -> {
                if (mobjSipManager.getSIPFile(objCache) && mobjArchiveManager.ingestSip(objCache.getSipFile().toString())) {
                    info(this, "Succesfully Ingested SIP " + objCache.getSipFile().toString());
                }
            });
        }
        mobjCacheManager.saveCaches();
        info(this, "Shutting down Worker " + miId);
    }

    public synchronized void stopWorker() {
        info(this, "Worker " + miId + " Received Stop command");
        mbRunning = false;
    }

    private boolean loadClasses() {
        try {
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
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        return false;
    }

    public synchronized int getProcessedMessageCounter() {
        return miProcessedMessages;
    }

    public synchronized void resetProcessedMessageCounter() {
        miProcessedMessages = 0;
    }
}
