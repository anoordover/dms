package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.configuration.WorkerConfiguration;

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
                    if (shouldStopWorker(iTotalProcessed, objConfiguration.getWorkerConfiguration())) {
                        stopWorker();
                    } else if (shouldStartWorker(iTotalProcessed, objConfiguration.getWorkerConfiguration())) {
                        startWorker();
                    }
                    lMillisecondsSinceLastCheck = System.currentTimeMillis();
                }
                updatePausedWorkerCaches(objConfiguration.getWorkerConfiguration());
                waitForNextCheck();
            }
        });
    }

    private void updatePausedWorkerCaches(WorkerConfiguration objConfiguration) {
        for (int i = miCurrentWorker + 1; i < objConfiguration.getMaxWorkerThreads() - 1; ++i) {
            mcWorkers.get(i).update();
            if (mcWorkers.get(i).getClosedCacheCount() > 0) {
                mcWorkers.get(i).setIngestFlag();
                startWorker();
            }
        }
    }

    private boolean shouldStopWorker(int iProcessed, WorkerConfiguration objConfiguration) {
        if (iProcessed < objConfiguration.getWorkerShutdownThreshold() && miCurrentWorker > 0) {
            if (mcWorkers.get(miCurrentWorker).isIngesting()) {
                return false;
            }

            return true;
        }

        return false;
    }

    private boolean shouldStartWorker(int iProcessed, WorkerConfiguration objConfiguration) {
        if (iProcessed > objConfiguration.getWorkerStartupThreshold() && miCurrentWorker < (objConfiguration.getMaxWorkerThreads() - 1) || miCurrentWorker == -1) {
            return true;
        }

        //Start a new worker if the current one is ingesting
        if (miCurrentWorker > -1 && mcWorkers.get(miCurrentWorker).isIngesting() && miCurrentWorker < (objConfiguration.getMaxWorkerThreads() - 1)) {
            return true;
        }

        return false;
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
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            warn(this, "WorkerManager Thread was Interrupted");
            mbIsRunning = false;
            Thread.currentThread().interrupt();
        }
    }

    private void startWorker() {
        if (miCurrentWorker > -2) { //Start from index 0
            new Thread(mcWorkers.get(++miCurrentWorker)).start();
        }
    }

    private void stopWorker() {
        if (miCurrentWorker > -1) {
            mcWorkers.get(miCurrentWorker--).stopWorker();
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
