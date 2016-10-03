package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * POJO for holding configuration pertaining to the WorkerManager
 * Created by admjzimmermann on 8-9-2016.
 */
public class WorkerConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("max_worker_threads")
    private int miMaxWorkerThreads;

    @XStreamAlias("worker_shutdown_threshold")
    private int miWorkerShutdownThreshold;

    @XStreamAlias("worker_startup_threshold")
    private int miWorkerStartupThreshold;

    @XStreamAlias("check_interval_milliseconds")
    private long mlCheckInterval;

    public int getMaxWorkerThreads() {
        return miMaxWorkerThreads;
    }

    public int getWorkerShutdownThreshold() {
        return miWorkerShutdownThreshold;
    }

    public int getWorkerStartupThreshold() {
        return miWorkerStartupThreshold;
    }

    public long getCheckInterval() {
        return mlCheckInterval;
    }
}
