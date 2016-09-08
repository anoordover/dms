package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class SIPPackagerConfiguration {
    @XStreamAlias("Archiver")
    private IAArchiverConfiguration archiverConfiguration;

    @XStreamAlias("WorkerManager")
    private WorkerConfiguration workerConfiguration;

    @XStreamAlias("Caching")
    private CacheConfiguration cacheConfiguration;

    @XStreamAlias("REST_API")
    private IAServerConfiguration serverConfiguration;

    @XStreamAlias("document_source")
    private PluggableObjectConfiguration documentSource;

    @XStreamAlias("retention_manager")
    private RetentionManagerConfiguration retentionManager;

    @XStreamAlias("message_parser")
    private PluggableObjectConfiguration messageParser;

    public IAArchiverConfiguration getArchiverConfiguration() {
        return archiverConfiguration;
    }

    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    public IAServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public PluggableObjectConfiguration getDocumentSource() {
        return documentSource;
    }

    public RetentionManagerConfiguration getRetentionManager() {
        return retentionManager;
    }

    public PluggableObjectConfiguration getMessageParser() {
        return messageParser;
    }

    public WorkerConfiguration getWorkerConfiguration() { return workerConfiguration; }
}
