package com.amplexor.ia.worker;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiverWorkerThread implements Runnable {
    private static Logger logger = Logger.getLogger("IAArchiverWorkerThread");
    private SIPPackagerConfiguration configuration;
    private int id;

    private int workerLoad;
    private boolean running;

    private DocumentSource documentSource;
    private MessageParser messageParser;
    private RetentionManager retentionManager;
    private CacheManager cacheManager;

    public int getWorkerLoad() {
        return workerLoad;
    }

    public IAArchiverWorkerThread(SIPPackagerConfiguration configuration) {
        this(configuration, 1);
    }

    public IAArchiverWorkerThread(SIPPackagerConfiguration configuration, int id) {
        this.configuration = configuration;
        this.id = id;
    }

    @Override
    public void run() {
        logger.info("Intializing Worker " + id);
        if (loadClasses()) {
            running = true;
            Thread.currentThread().setName("IAWorker-" + id);
            logger.info("Initializing Document Caches");
            cacheManager = new CacheManager(configuration.getCacheConfiguration());
            cacheManager.initializeCache(configuration.getRetentionManager().getRetentionClasses());
            logger.info("DONE. Starting main loop");
        }

        while (running) {
            IADocument document = messageParser.parse(documentSource);
            logger.info("Retrieved document with id: " + document.getDocumentId());
            IARetentionClass retentionClass = retentionManager.retrieveRetentionClass(document);
            cacheManager.add(document, retentionClass);
        }
        logger.info("Shutting down Worker " + id);
    }

    public synchronized void stopWorker() {
        logger.info("Worker " + id + " Received Stop command");
        running = false;
    }

    private boolean loadClasses() {
        try {
            Object documentSourceInstance = Thread.currentThread().getContextClassLoader()
                    .loadClass(configuration.getDocumentSource().getImplementingClass())
                    .getConstructor(PluggableObjectConfiguration.class)
                    .newInstance(configuration.getDocumentSource());
            if (documentSourceInstance instanceof DocumentSource) {
                documentSource = DocumentSource.class.cast(documentSourceInstance);
            }

            Object messageParserInstance = Thread.currentThread().getContextClassLoader()
                    .loadClass(configuration.getMessageParser().getImplementingClass())
                    .getConstructor(PluggableObjectConfiguration.class)
                    .newInstance(configuration.getMessageParser());
            if (messageParserInstance instanceof MessageParser) {
                messageParser = MessageParser.class.cast(messageParserInstance);
            }

            Object retentionManagerInstance = Thread.currentThread().getContextClassLoader()
                    .loadClass(configuration.getRetentionManager().getImplementingClass())
                    .getConstructor(RetentionManagerConfiguration.class)
                    .newInstance(configuration.getRetentionManager());
            if (retentionManagerInstance instanceof RetentionManager) {
                retentionManager = RetentionManager.class.cast(retentionManagerInstance);
            }

            return ((documentSource != null) && (messageParser != null) && (retentionManager != null));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return false;
    }
}
