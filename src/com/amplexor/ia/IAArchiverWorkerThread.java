package com.amplexor.ia;

import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.retention.RetentionManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiverWorkerThread implements Runnable {
    private SIPPackagerConfiguration configuration;

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
        this.configuration = configuration;
    }

    @Override
    public void run() {
        if (loadClasses()) {
            running = true;
        }

        while (running) {
            try {
                documentSource.retrieveDocument();
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
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
                    .getConstructor(PluggableObjectConfiguration.class)
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
