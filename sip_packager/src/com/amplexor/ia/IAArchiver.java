package com.amplexor.ia;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAArchiver {
    public static void main(String[] args) {
        String configLocation = (System.getProperty("user.dir") + "/config/IAArchiver.xml").replace('/', File.separatorChar);
        for(int i = 0; i < args.length; ++i) {
            if((i + 1) % 2 == 0 && i > 0) { //Expect Arguments in key-value pairs and map them
                switch(args[i - 1]) {
                    case "-config":
                        configLocation = args[i];
                        break;
                    default:
                        break;
                }
            }
        }
        System.out.println("Loading configuration: " + configLocation);
        ConfigManager config = new ConfigManager(configLocation);
        config.loadConfiguration();
        System.out.println(config.getConfiguration().getDocumentSourceClass());
    }
}
