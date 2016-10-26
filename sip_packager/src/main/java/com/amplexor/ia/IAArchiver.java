package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.worker.WorkerManager;
import org.apache.log4j.PropertyConfigurator;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import static com.amplexor.ia.Logger.error;
import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiver {
    private static String configLocation = (System.getProperty("user.dir") + "/config/IAArchiver.xml").replace('/', File.separatorChar);

    private IAArchiver() {

    }

    public static void main(String[] cArgs) {
        parseArguments(cArgs);
        ConfigManager objConfigManager = null;
        final WorkerManager objWorkerManager = WorkerManager.getWorkerManager();
        try {
            objConfigManager = new ConfigManager(configLocation);
            objConfigManager.loadConfiguration();
            ExceptionHelper.getExceptionHelper().setExceptionConfiguration(objConfigManager.getConfiguration().getExceptionConfiguration());
        } catch (IllegalArgumentException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INIT_INVALID_CONFIGURATION, ex);
        }

        if (objConfigManager != null) {
            info(IAArchiver.class, "Configuring Logging with " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
            Properties objLog4JProperties = new Properties();
            try (FileInputStream objPropertiesStream = new FileInputStream(objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath())) {
                objLog4JProperties.load(objPropertiesStream);
                PropertyConfigurator.configure(objLog4JProperties);
            } catch (Exception ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INIT_INVALID_LOG4J_PROPERTIES, ex);
            }
            info(IAArchiver.class, "Logging configured using " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());

            info(IAArchiver.class, "Initializing Worker Manager");
            objWorkerManager.initialize(objConfigManager.getConfiguration());
        }

        if (objWorkerManager != null) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    info(IAArchiver.class, "Starting shutdown sequence");
                    try {
                        Set<Thread> cRunningThreads = Thread.getAllStackTraces().keySet();
                        info(IAArchiver.class, "Found " + cRunningThreads.size() + " Threads");
                        for (Thread objThread : cRunningThreads) {
                            if (objThread != Thread.currentThread()
                                    && !objThread.isDaemon()
                                    && objThread.getName().startsWith("IAWorker")) {
                                info(IAArchiver.class, "Interrupting Thread " + objThread.getName());
                                objThread.interrupt();
                            }
                        }

                        for (Thread objThread : cRunningThreads) {
                            if (objThread != Thread.currentThread()
                                    && !objThread.isDaemon()
                                    && objThread.getName().startsWith("IAWorker")) {
                                info(IAArchiver.class, "Joining Thread " + objThread.getName());
                                objThread.join();
                            }
                        }
                    } catch (InterruptedException ex) {
                        error(IAArchiver.class, "Shutdown interrupted!");
                    }
                }
            });

            while (objWorkerManager.checkWorkers(objConfigManager.getConfiguration().getWorkerConfiguration())) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    public static void parseArguments(String[] cArgs) {
        for (int i = 0; i < cArgs.length; ++i) {
            if ((i + 1) % 2 == 0 && i > 0 && "-config".equals(cArgs[i - 1])) {
                configLocation = cArgs[i];
            }
        }
    }
}
