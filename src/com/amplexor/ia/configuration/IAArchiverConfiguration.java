package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiverConfiguration {
    @XStreamAlias("worker_threads")
    private int noWorkerThreads;

    @XStreamAlias("log4j_properties_path")
    private String log4jPropertiesPath;
}
