package com.amplexor.ia.worker;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.ingest.ArchiveManager;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;
import com.amplexor.ia.sip.AMPSipManager;

import static com.amplexor.ia.Logger.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiverWorkerThread implements Runnable {
    private SIPPackagerConfiguration mobjConfiguration;
    private int miId;

    private int miProcessedMessages;
    private boolean mbRunning;

    private DocumentSource mobjDocumentSource;
    private MessageParser mobjMessageParser;
    private RetentionManager mobjRetentionManager;
    private CacheManager mobjCacheManager;
    private ArchiveManager mobjArchiveManager;
    private AMPSipManager mobjSipManager;


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
                info(this, "Initializing Document Caches");
                mobjCacheManager = new CacheManager(mobjConfiguration.getCacheConfiguration());
                mobjCacheManager.initializeCache();
                info(this, "Initializing SIP Manager");
                mobjSipManager = new AMPSipManager(mobjConfiguration.getSipConfiguration());
                info(this, "Initializing Archive Manager");
                mobjArchiveManager = new ArchiveManager(mobjConfiguration.getServerConfiguration());
                info(this, "DONE. Starting main loop");
                mbRunning = true;
            } catch (IOException ex) {
                error(this, ex);
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
                    IARetentionClass objRetentionClass = mobjRetentionManager.retrieveRetentionClass(objDocument);
                    if(objRetentionClass != null) {
                        mobjCacheManager.add(objDocument, objRetentionClass);
                    }
                });
            }
            mobjCacheManager.update();
            mobjCacheManager.getClosedCaches().iterator().forEachRemaining(objCache -> {
                Path objSipPath = mobjSipManager.getSIPFile(objCache);
                if (mobjArchiveManager.ingestSip(objSipPath.toString())) {
                    info(this, "Succesfully Ingested SIP " + objSipPath.toString());
                } else {
                    error(this, "Error Ingesting SIP: " + objSipPath.toString());
                }
            });
        }

        info(this, "Shutting down Worker " + miId);
    }

    public synchronized void stopWorker() {
        info(this, "Worker " + miId + " Received Stop command");
        mbRunning = false;
    }

    private boolean loadClasses() {
        try {
            Object objDocumentSource = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getDocumentSource().getImplementingClass())
                    .getConstructor(PluggableObjectConfiguration.class)
                    .newInstance(mobjConfiguration.getDocumentSource());
            if (objDocumentSource instanceof DocumentSource) {
                mobjDocumentSource = DocumentSource.class.cast(objDocumentSource);
            }

            Object objMessageParser = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getMessageParser().getImplementingClass())
                    .getConstructor(PluggableObjectConfiguration.class)
                    .newInstance(mobjConfiguration.getMessageParser());
            if (objMessageParser instanceof MessageParser) {
                mobjMessageParser = MessageParser.class.cast(objMessageParser);
            }

            Object objRetentionManager = Thread.currentThread().getContextClassLoader()
                    .loadClass(mobjConfiguration.getRetentionManager().getImplementingClass())
                    .getConstructor(RetentionManagerConfiguration.class)
                    .newInstance(mobjConfiguration.getRetentionManager());
            if (objRetentionManager instanceof RetentionManager) {
                mobjRetentionManager = RetentionManager.class.cast(objRetentionManager);
            }

            return mobjDocumentSource != null && mobjMessageParser != null && mobjRetentionManager != null;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            error(this, ex);
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
