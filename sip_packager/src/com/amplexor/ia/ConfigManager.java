package com.amplexor.ia;

import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.IOException;

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
        XStream xstream = new XStream(new StaxDriver());
        xstream.alias("configuration", SIPPackagerConfiguration.class);
        xstream.autodetectAnnotations(true);
        configuration = (SIPPackagerConfiguration)xstream.fromXML(new File(configPath));
    }
}
