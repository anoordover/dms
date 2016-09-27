package com.amplexor.ia.worker;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.amplexor.ia.Logger.info;
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
        for (int i = 0; i < objConfiguration.getWorkerConfiguration().getMaxWorkerThreads(); ++i) {
            mcWorkers.add(new IAArchiverWorkerThread(objConfiguration));
        }

        mobjManagerThread = new Thread(() -> {
            long lMillisecondsSinceLastCheck = 0;
            while (mbIsRunning) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                mlDiffMillisecondsSinceLastCheck = Math.abs(lMillisecondsSinceLastCheck - System.currentTimeMillis());
                if (mlDiffMillisecondsSinceLastCheck > objConfiguration.getWorkerConfiguration().getCheckInterval()) {
                    int iTotalProcessed = getProcessedMessages();
                    if (iTotalProcessed < objConfiguration.getWorkerConfiguration().getWorkerShutdownThreshold() && miCurrentWorker > -1) {
                        stopWorker();
                    } else if (iTotalProcessed > objConfiguration.getWorkerConfiguration().getWorkerStartupThreshold() && miCurrentWorker < objConfiguration.getWorkerConfiguration().getMaxWorkerThreads() || miCurrentWorker == -1) {
                        startWorker();
                    }
                    lMillisecondsSinceLastCheck = System.currentTimeMillis();
                }
                waitForNextCheck();
            }
        });
        miCurrentWorker = -1;
    }

    private int getProcessedMessages() {
        int iReturn = 0;
        for (Iterator<IAArchiverWorkerThread> objIter = mcWorkers.iterator(); objIter.hasNext(); ) {
            IAArchiverWorkerThread objWorker = objIter.next();
            iReturn += objWorker.getProcessedMessageCounter();
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
        while(miCurrentWorker > -1) {
            stopWorker();
        }
        mobjManagerThread.interrupt();
    }
}
