package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.worker.WorkerManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiver {
    private static final int V_MAJOR = 0;
    private static final int V_MINOR = 3;
    private static final int REVISION = 6;

    private static String configLocation = (System.getProperty("user.dir") + "/config/IAArchiver.xml").replace('/', File.separatorChar);

    private IAArchiver() { //Hide implicit public constructor

    }

    public static void main(String[] cArgs) {
        info(IAArchiver.class, "Starting SIP Packager " + V_MAJOR + "." + V_MINOR + "." + REVISION);
        parseArguments(cArgs);
        final WorkerManager objWorkerManager = WorkerManager.getWorkerManager();

        ConfigManager objConfigManager = loadConfiguration();
        if (objConfigManager != null) {
            configureLogging(objConfigManager);
            configureWorkerManager(objConfigManager);
            if (objWorkerManager != null) {
                Runtime.getRuntime().addShutdownHook(new IAArchiverShutdownHook());
                runLoop(objConfigManager);
            }
        }
    }

    private static void runLoop(ConfigManager objConfigManager) {
        while (WorkerManager.getWorkerManager().checkWorkers(objConfigManager.getConfiguration().getWorkerConfiguration())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    private static ConfigManager loadConfiguration() {
        ConfigManager objConfigManager = null;
        try {
            objConfigManager = new ConfigManager(configLocation);
            objConfigManager.loadConfiguration();
            ExceptionHelper.getExceptionHelper().setExceptionConfiguration(objConfigManager.getConfiguration().getExceptionConfiguration());
        } catch (IllegalArgumentException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INIT_INVALID_CONFIGURATION, ex);
        }

        return objConfigManager;
    }

    private static void configureLogging(ConfigManager objConfigManager) {
        info(IAArchiver.class, "Configuring Logging with " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        Properties objLog4JProperties = new Properties();
        try (FileInputStream objPropertiesStream = new FileInputStream(objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath())) {
            objLog4JProperties.load(objPropertiesStream);
            PropertyConfigurator.configure(objLog4JProperties);
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INIT_INVALID_LOG4J_PROPERTIES, ex);
        }
    }

    private static void configureWorkerManager(ConfigManager objConfigManager) {
        info(IAArchiver.class, "Logging configured using " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        info(IAArchiver.class, "Initializing Worker Manager");
        WorkerManager.getWorkerManager().initialize(objConfigManager.getConfiguration());
    }

    public static void parseArguments(String[] cArgs) {
        for (int i = 0; i < cArgs.length; ++i) {
            if ((i + 1) % 2 == 0 && i > 0 && "-config".equals(cArgs[i - 1])) {
                configLocation = cArgs[i];
            }
        }
    }
}
