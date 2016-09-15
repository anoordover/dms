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
import com.amplexor.ia.sip.SipManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiverWorkerThread implements Runnable {
    private static Logger logger = Logger.getLogger("IAArchiverWorkerThread");
    private SIPPackagerConfiguration mobjConfiguration;
    private int miId;

    private int miWorkerLoad;
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

    public int getWorkerLoad() {
        return miWorkerLoad;
    }

    @Override
    public void run() {
        logger.info("Intializing Worker " + miId);
        if (loadClasses()) {
            mbRunning = true;
            Thread.currentThread().setName("IAWorker-" + miId);
            logger.info("Initializing Document Caches");
            mobjCacheManager = new CacheManager(mobjConfiguration.getCacheConfiguration());
            mobjCacheManager.initializeCache();
            mobjSipManager = new SipManager(mobjConfiguration.getSipConfiguration());
            mobjArchiveManager = new ArchiveManager(mobjConfiguration.getServerConfiguration());
            logger.info("DONE. Starting main loop");
        }

        while (mbRunning) {
            IADocument objDocument = mobjMessageParser.parse(mobjDocumentSource);
            if(objDocument != null) {
                logger.info("Retrieved document with id: " + objDocument.getDocumentId());
                IARetentionClass objRetentionClass = mobjRetentionManager.retrieveRetentionClass(objDocument);
                if (objRetentionClass != null) {
                    mobjCacheManager.add(objDocument, objRetentionClass);
                }
            }
            mobjCacheManager.update();
            mobjCacheManager.getClosedCaches().iterator().forEachRemaining(objCache -> {
                Path objSipPath = mobjSipManager.getSIPFile(objCache);
                mobjArchiveManager.ingestSip(objSipPath.toString());
            });
        }
        logger.info("Shutting down Worker " + miId);
    }

    public synchronized void stopWorker() {
        logger.info("Worker " + miId + " Received Stop command");
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
            logger.error(ex);
        }
        return false;
    }
}
