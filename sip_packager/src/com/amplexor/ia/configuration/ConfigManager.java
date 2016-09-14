package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ConfigManager {
    private String configPath;
    private SIPPackagerConfiguration configuration;

    public SIPPackagerConfiguration getConfiguration() {
        return configuration;
    }

    public ConfigManager(String configPath) {
        this.configPath = configPath;
    }

    public void loadConfiguration() {
        System.out.println("Loading Configuration from " + configPath);
        XStream xstream = new XStream(new StaxDriver());
        xstream.alias("IAArchiver", SIPPackagerConfiguration.class);
        xstream.autodetectAnnotations(true);
        configuration = (SIPPackagerConfiguration)xstream.fromXML(new File(configPath));
    }
}
