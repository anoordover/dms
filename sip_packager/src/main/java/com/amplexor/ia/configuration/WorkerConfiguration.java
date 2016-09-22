package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 8-9-2016.
 */
public class WorkerConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("max_worker_threads")
    private int miMaxWorkerThreads;

    public int getMaxWorkerThreads() {
        return miMaxWorkerThreads;
    }
}
