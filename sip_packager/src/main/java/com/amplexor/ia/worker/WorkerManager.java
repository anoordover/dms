package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.amplexor.ia.Logger.warn;

/**
 * Created by admjzimmermann on 8-9-2016.
 */

public class WorkerManager {
    private static WorkerManager mobjWorkerManager;

    private Thread mobjManagerThread;
    private List<Thread> mcThreads;
    private List<IAArchiverWorkerThread> mcWorkers;
    private int miCurrentWorker;

    private long mlDiffMillisecondsSinceLastCheck;
    private boolean mbIsRunning;

    private WorkerManager() {
        mcWorkers = new ArrayList<>();
    }

    public static WorkerManager getWorkerManager() {
        if (mobjWorkerManager == null) {
            mobjWorkerManager = new WorkerManager();
        }

        return mobjWorkerManager;
    }

    public void initialize(SIPPackagerConfiguration objConfiguration) {
        miCurrentWorker = -1;
        mcThreads = new ArrayList<>();
        for (int i = 0; i < objConfiguration.getWorkerConfiguration().getMaxWorkerThreads(); ++i) {
            mcWorkers.add(new IAArchiverWorkerThread(objConfiguration, i + 1));
            startWorker();
        }

        mobjManagerThread = new Thread(() -> {
            long lMillisecondsSinceLastCheck = 0;
            while (mbIsRunning) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                mlDiffMillisecondsSinceLastCheck = Math.abs(lMillisecondsSinceLastCheck - System.currentTimeMillis());
                if (mlDiffMillisecondsSinceLastCheck > objConfiguration.getWorkerConfiguration().getCheckInterval()) {
                    int iTotalProcessed = getProcessedBytes();
                    if (iTotalProcessed < objConfiguration.getWorkerConfiguration().getWorkerShutdownThreshold() && miCurrentWorker > 0) {
                        stopWorker();
                    } else if (iTotalProcessed > objConfiguration.getWorkerConfiguration().getWorkerStartupThreshold() && miCurrentWorker < (objConfiguration.getWorkerConfiguration().getMaxWorkerThreads() - 1) || miCurrentWorker == -1) {
                        startWorker();
                    }
                    lMillisecondsSinceLastCheck = System.currentTimeMillis();
                }
                waitForNextCheck();
            }
        });
    }

    private int getProcessedBytes() {
        int iReturn = 0;
        for (Iterator<IAArchiverWorkerThread> objIter = mcWorkers.iterator(); objIter.hasNext(); ) {
            IAArchiverWorkerThread objWorker = objIter.next();
            iReturn += objWorker.getProcessedBytes();
            objWorker.resetProcessedBytes();
        }
        return iReturn;
    }

    private void waitForNextCheck() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            warn(this, "WorkerManager Thread was Interrupted");
            mbIsRunning = false;
            Thread.currentThread().interrupt();
        }
    }

    private void startWorker() {
        if (miCurrentWorker > -2) { //Start from index 0
            mcThreads.add(new Thread(mcWorkers.get(++miCurrentWorker)));
            mcThreads.get(miCurrentWorker).start();
        }
    }

    private void stopWorker() {
        if (miCurrentWorker > -1) {
            mcThreads.get(miCurrentWorker).interrupt();
            mcThreads.remove(miCurrentWorker--);
        }
    }

    public synchronized void start() {
        if (!mbIsRunning) {
            mobjManagerThread.start();
            mbIsRunning = true;
        }
    }

    public synchronized void stop() {
        while (miCurrentWorker > -1) {
            stopWorker();
        }
        mbIsRunning = false;
        mobjManagerThread.interrupt();
    }
}
