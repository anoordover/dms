package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.configuration.WorkerConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admjzimmermann on 8-9-2016.
 */

//TODO: Apply logic, i.e. thresholds for starting / stopping workers etc.
public class WorkerManager {
    ExecutorService poolExecutor;
    boolean running;

    public WorkerManager(WorkerConfiguration configuration) {
        poolExecutor = Executors.newFixedThreadPool(configuration.getMaxWorkerThreads());
    }

    public void initialize() {
        //TODO: Initialization Logic here
    }

    public void start(SIPPackagerConfiguration configuration) {
        int needed = 1;
        running = true;

        while (running) {
            if (needed == 1) { //Check if we need another worker
                poolExecutor.submit(new IAArchiverWorkerThread(configuration));
                ++needed;
            }
        }

    }
}
