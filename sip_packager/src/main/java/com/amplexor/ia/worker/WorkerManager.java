package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.configuration.WorkerConfiguration;

import java.util.Iterator;
import java.util.List;

/**
 * Created by admjzimmermann on 8-9-2016.
 */

public class WorkerManager {
    private static WorkerManager mobjWorkerManager;

    private Thread mobjManagerThread;
    private WorkerConfiguration mobjConfiguration;
    private List<IAArchiverWorkerThread> mcWorkers;
    private int miCurrentWorker;

    private long mlMillisecondsSinceLastCheck;
    private long mlDiffMillisecondsSinceLastCheck;
    private int miCheckCounter;

    private WorkerManager() {
    }

    public static WorkerManager getWorkerManager() {
        if (mobjWorkerManager == null) {
            mobjWorkerManager = new WorkerManager();
        }

        return mobjWorkerManager;
    }

    public void initialize(SIPPackagerConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration.getWorkerConfiguration();
        for (int i = 0; i < mobjConfiguration.getMaxWorkerThreads(); ++i) {
            mcWorkers.add(new IAArchiverWorkerThread(objConfiguration));
        }
        miCurrentWorker = -1;
    }

    private void startWorker() {
        if (miCurrentWorker > -2) { //Start from index 0
            mcWorkers.get(++miCurrentWorker).run();
        }
    }

    private void stopWorker() {
        mcWorkers.get(miCurrentWorker--).stopWorker();
    }

    public void start() {
        mlMillisecondsSinceLastCheck = 0;
        miCheckCounter = 0;
        mobjManagerThread = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                if ((mlDiffMillisecondsSinceLastCheck = (mlMillisecondsSinceLastCheck - System.currentTimeMillis()) > mobjConfiguration.getCheckInterval())) {
                    int iTotalProcessed = 0;
                    for (Iterator<IAArchiverWorkerThread> objIter = mcWorkers.iterator(); objIter.hasNext(); ) {
                        IAArchiverWorkerThread objWorker = objIter.next();
                        iTotalProcessed += objWorker.getProcessedMessageCounter();
                    }

                    if (iTotalProcessed < mobjConfiguration.getWorkerShutdownThreshold()) {
                        stopWorker();
                    } else if (iTotalProcessed > mobjConfiguration.getWorkerStartupThreshold()) {
                        startWorker();
                    }
                }

                mlMillisecondsSinceLastCheck = System.currentTimeMillis();
                Thread.sleep(mobjConfiguration.getCheckInterval() - mlDiffMillisecondsSinceLastCheck);
            }
        });
        mobjManagerThread.start();
    }

    public void stop() {
        mcWorkers.forEach((objThread) -> objThread.stopWorker());
        mobjManagerThread.interrupt();
    }
}
