package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.configuration.WorkerConfiguration;

import java.util.ArrayList;
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
    private long mlMillisecondsSinceLastCheck;

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
        long mlDiffMillisecondsSinceLastCheck = Math.abs(mlMillisecondsSinceLastCheck - System.currentTimeMillis());
        if (mlDiffMillisecondsSinceLastCheck > objConfiguration.getCheckInterval()) {
            int iTotalProcessed = getProcessedBytes();
            if (shouldStopWorker(iTotalProcessed, objConfiguration)) {
                stopWorker(false);
            } else if (shouldStartWorker(iTotalProcessed, objConfiguration)) {
                startWorker();
            }
            mlMillisecondsSinceLastCheck = System.currentTimeMillis();
        }
        updatePausedWorkerCaches(objConfiguration);

        return !mbStopFlag;
    }

    private void updatePausedWorkerCaches(WorkerConfiguration objConfiguration) {
        int iStartupCount = 0;
        for (int i = miCurrentWorker + 1; i < objConfiguration.getMaxWorkerThreads() - 1; ++i) {
            mcWorkers.get(i).update();
            if (mcWorkers.get(i).getClosedCacheCount() > 0) {
                mcWorkers.get(i).setIngestFlag();
                iStartupCount = i - miCurrentWorker;
            }
        }

        while (iStartupCount-- > 0) {
            startWorker();
        }
    }

    private boolean shouldStopWorker(int iProcessed, WorkerConfiguration objConfiguration) {
        return iProcessed < objConfiguration.getWorkerShutdownThreshold() && miCurrentWorker > 0 && !mcWorkers.get(miCurrentWorker).isIngesting() || mbStopFlag;

    }

    private boolean shouldStartWorker(int iProcessed, WorkerConfiguration objConfiguration) {
        if (mbStopFlag) {
            return false;
        }

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
            info(this, "Started Worker " + (miCurrentWorker + 1));
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
