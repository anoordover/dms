package com.amplexor.ia;

import com.amplexor.ia.exception.ExceptionHelper;

import java.util.Set;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 1-11-2016.
 */
public class IAArchiverShutdownHook extends Thread {
    void interruptThread(Thread objThread) {
        if (objThread != Thread.currentThread()
                && !objThread.isDaemon()
                && objThread.getName().startsWith("IAWorker")) {
            info(IAArchiver.class, "Interrupting Thread " + objThread.getName());
            objThread.interrupt();
        }
    }

    void joinThread(Thread objThread) {
        try {
            if (objThread != Thread.currentThread()
                    && !objThread.isDaemon()
                    && objThread.getName().startsWith("IAWorker")) {
                info(IAArchiver.class, "Joining Thread " + objThread.getName());
                objThread.join();
            }
        } catch (InterruptedException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        info(IAArchiver.class, "Starting shutdown sequence");
        Set<Thread> cRunningThreads = Thread.getAllStackTraces().keySet();
        info(IAArchiver.class, "Found " + cRunningThreads.size() + " Threads");
        for (Thread objThread : cRunningThreads) {
            interruptThread(objThread);
        }

        for (Thread objThread : cRunningThreads) {
            joinThread(objThread);
        }
    }
}
