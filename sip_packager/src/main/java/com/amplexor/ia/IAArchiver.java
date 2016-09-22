package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.WorkerConfiguration;
import com.amplexor.ia.worker.WorkerManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import static com.amplexor.ia.Logger.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiver {
    private static String configLocation = (System.getProperty("user.dir") + "/config/IAArchiver.xml").replace('/', File.separatorChar);

    private IAArchiver() {

    }

    public static void main(String[] cArgs) {
        parseArguments(cArgs);
        ConfigManager objConfigManager = new ConfigManager(configLocation);
        objConfigManager.loadConfiguration();

        info(IAArchiver.class, "Configuring Logging with " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        PropertyConfigurator.configure(objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        info(IAArchiver.class, "Logging configured using " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());

        info(IAArchiver.class, "Initializing Worker Manager");
        WorkerManager objWorkerManager = WorkerManager.getWorkerManager();
        objWorkerManager.initialize(objConfigManager.getConfiguration());
        objWorkerManager.start();
    }



    public static void parseArguments(String[] cArgs) {
        for (int i = 0; i < cArgs.length; ++i) {
            if ((i + 1) % 2 == 0 && i > 0 && "-config".equals(cArgs[i - 1])) {
                configLocation = cArgs[i];
            }
        }
    }
}
