package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.WorkerConfiguration;
import com.amplexor.ia.worker.WorkerManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiver {
    private static String configLocation = (System.getProperty("user.dir") + "/config/IAArchiver.xml").replace('/', File.separatorChar);
    private static Logger logger = Logger.getLogger("IAArchiver");

    public static void main(String[] args) {
        parseArguments(args);
        ConfigManager config = new ConfigManager(configLocation);
        config.loadConfiguration();

        System.out.println("Configuring Logging with " + config.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        PropertyConfigurator.configure(config.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        logger.info("Logging configured using " + config.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());

        WorkerManager workerManager = getWorkerManager(config.getConfiguration().getWorkerConfiguration());
        workerManager.initialize();
        workerManager.start(config.getConfiguration());
    }

    public static WorkerManager getWorkerManager(WorkerConfiguration configuration) {
        WorkerManager rval = null;
        if (configuration.getImplementingClass() == null) {
            rval = new WorkerManager(configuration);
        } else {
            if (configuration.getImplementingClass().equals("")) {
                rval = new WorkerManager(configuration);
            } else {
                try {
                    Object workerManager = ClassLoader.getSystemClassLoader().loadClass(configuration.getImplementingClass())
                            .getConstructor(WorkerConfiguration.class)
                            .newInstance(configuration);
                    if (workerManager instanceof WorkerManager) {
                        rval = (WorkerManager) workerManager;
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {

                }
            }
        }
        return rval;
    }

    public static void parseArguments(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if ((i + 1) % 2 == 0 && i > 0) { //Expect Arguments in key-value pairs and map them
                switch (args[i - 1]) {
                    case "-config":
                        configLocation = args[i];
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
