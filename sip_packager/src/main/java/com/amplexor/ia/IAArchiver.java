package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.worker.WorkerManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.Properties;

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
        WorkerManager objWorkerManager = null;
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
            objWorkerManager = WorkerManager.getWorkerManager();
            objWorkerManager.initialize(objConfigManager.getConfiguration());
            objWorkerManager.start();
        }

        if (objWorkerManager != null) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    WorkerManager.getWorkerManager().stop();
                }
            });
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
