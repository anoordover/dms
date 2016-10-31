package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.configuration.WorkerConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 8-9-2016.
 */

public class WorkerManager {
    private static WorkerManager mobjWorkerManager = new WorkerManager();
    private List<IAArchiverWorkerThread> mcWorkers;
    private int miCurrentWorker;

    private boolean mbStopFlag;
    private int miExitCode;
    private long lMillisecondsSinceLastCheck;

    private WorkerManager() {
        mcWorkers = new ArrayList<>();
    }

    public static synchronized WorkerManager getWorkerManager() {
        return mobjWorkerManager;
    }

    public int getExitCode() {
        return miExitCode;
    }

    public void initialize(SIPPackagerConfiguration objConfiguration) {
        mbStopFlag = false;
        miCurrentWorker = -1;
        for (int i = 0; i < objConfiguration.getWorkerConfiguration().getMaxWorkerThreads(); ++i) {
            mcWorkers.add(new IAArchiverWorkerThread(objConfiguration, i + 1));
            startWorker();
        }
    }

    public boolean checkWorkers(WorkerConfiguration objConfiguration) {
        long mlDiffMillisecondsSinceLastCheck = Math.abs(lMillisecondsSinceLastCheck - System.currentTimeMillis());
        if (mlDiffMillisecondsSinceLastCheck > objConfiguration.getCheckInterval()) {
            int iTotalProcessed = getProcessedBytes();
            if (shouldStopWorker(iTotalProcessed, objConfiguration)) {
                stopWorker(false);
            } else if (shouldStartWorker(iTotalProcessed, objConfiguration)) {
                startWorker();
            }
            lMillisecondsSinceLastCheck = System.currentTimeMillis();
        }
        updatePausedWorkerCaches(objConfiguration);

        return !mbStopFlag;
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
        return iProcessed < objConfiguration.getWorkerShutdownThreshold() && miCurrentWorker > 0 && !mcWorkers.get(miCurrentWorker).isIngesting();

    }

    private boolean shouldStartWorker(int iProcessed, WorkerConfiguration objConfiguration) {
        if (iProcessed > objConfiguration.getWorkerStartupThreshold() && miCurrentWorker < (objConfiguration.getMaxWorkerThreads() - 1) || miCurrentWorker == -1) {
            return true;
        }

        //Start a new worker if the current one is ingesting
        return miCurrentWorker > -1 && mcWorkers.get(miCurrentWorker).isIngesting() && miCurrentWorker < (objConfiguration.getMaxWorkerThreads() - 1);

    }

    private int getProcessedBytes() {
        int iReturn = 0;
        for (IAArchiverWorkerThread objWorker : mcWorkers) {
            iReturn += objWorker.getProcessedBytes();
            objWorker.resetProcessedBytes();
        }
        return iReturn;
    }

    private void startWorker() {
        if (miCurrentWorker > -2) { //Start from index 0
            new Thread(mcWorkers.get(++miCurrentWorker)).start();
        }
    }

    private void stopWorker(boolean bShutdown) {
        if (miCurrentWorker > -1) {
            mcWorkers.get(miCurrentWorker--).stopWorker(bShutdown);
        }
    }

    public synchronized void signalStop(int iExitCode) {
        info(this, "Received stop signal, shutting down all workers");
        mbStopFlag = true;
        miExitCode = iExitCode;
    }
}
