package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class SIPPackagerConfiguration {
    @XStreamAlias("Archiver")
    private IAArchiverConfiguration archiverConfiguration;

    @XStreamAlias("Caching")
    private CacheConfiguration cacheConfiguration;

    @XStreamAlias("REST_API")
    private IAServerConfiguration serverConfiguration;

    @XStreamAlias("document_source")
    private String documentSourceClass;

    @XStreamAlias("retention_manager")
    private String retentionManagerClass;

    @XStreamAlias("message_parser")
    private String messageParserClass;

    public IAArchiverConfiguration getArchiverConfiguration() {
        return archiverConfiguration;
    }

    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    public IAServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public String getDocumentSourceClass() {
        return documentSourceClass;
    }

    public String getRetentionManagerClass() {
        return retentionManagerClass;
    }

    public String getMessageParserClass() {
        return messageParserClass;
    }

}
