package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class SIPPackagerConfiguration {
    @XStreamAlias("archiver")
    private IAArchiverConfiguration moArchiverConfiguration;

    @XStreamAlias("worker_manager")
    private WorkerConfiguration moWorkerConfiguration;

    @XStreamAlias("caching")
    private CacheConfiguration moCacheConfiguration;

    @XStreamAlias("rest_api")
    private IAServerConfiguration moServerConfiguration;

    @XStreamAlias("sip")
    private IASipConfiguration moSipConfiguration;

    @XStreamAlias("document_source")
    private PluggableObjectConfiguration moDocumentSource;

    @XStreamAlias("retention_manager")
    private RetentionManagerConfiguration moRetentionManager;

    @XStreamAlias("message_parser")
    private PluggableObjectConfiguration moMessageParser;

    public IAArchiverConfiguration getArchiverConfiguration() {
        return moArchiverConfiguration;
    }

    public CacheConfiguration getCacheConfiguration() {
        return moCacheConfiguration;
    }

    public IAServerConfiguration getServerConfiguration() {
        return moServerConfiguration;
    }

    public PluggableObjectConfiguration getDocumentSource() {
        return moDocumentSource;
    }

    public RetentionManagerConfiguration getRetentionManager() {
        return moRetentionManager;
    }

    public PluggableObjectConfiguration getMessageParser() {
        return moMessageParser;
    }

    public WorkerConfiguration getWorkerConfiguration() {
        return moWorkerConfiguration;
    }

    public IASipConfiguration getSipConfiguration() {
        return moSipConfiguration;
    }
}