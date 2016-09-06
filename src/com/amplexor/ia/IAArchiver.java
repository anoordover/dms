package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiver {
    private static String configLocation = (System.getProperty("user.dir") + "/config/IAArchiver.xml").replace('/', File.separatorChar);
    private static String cacheSave = (System.getProperty("user.dir") + "/cache/cache.xml").replace('/', File.separatorChar);

    public static void main(String[] args) {
        System.out.println("USER.DIR: " + System.getProperty("user.dir"));
        parseArguments(args);
        ConfigManager config = new ConfigManager(configLocation);
        config.loadConfiguration();
        ExecutorService poolExecutor = Executors.newFixedThreadPool(config.getConfiguration().getArchiverConfiguration().getWorkerThreads());

        poolExecutor.submit(new IAArchiverWorkerThread(config.getConfiguration()));

    }

    public static void parseArguments(String[] args) {
        for(int i = 0; i < args.length; ++i) {
            if((i + 1) % 2 == 0 && i > 0) { //Expect Arguments in key-value pairs and map them
                switch(args[i - 1]) {
                    case "-config":
                        configLocation = args[i];
                        break;
                    case "-cachesave":
                        cacheSave = args[i];
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
