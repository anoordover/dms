package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ConfigManager {

    private String msConfigPath;
    private SIPPackagerConfiguration mobjConfiguration;

    public ConfigManager(String sConfigPath) {
        msConfigPath = sConfigPath;
    }

    public SIPPackagerConfiguration getConfiguration() {
        return mobjConfiguration;
    }


    public void loadConfiguration() throws IllegalArgumentException {
        info(this, "Loading Configuration from " + msConfigPath);
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("IAArchiver", SIPPackagerConfiguration.class);
        objXStream.autodetectAnnotations(true);
        mobjConfiguration = (SIPPackagerConfiguration)objXStream.fromXML(new File(msConfigPath));
        if(mobjConfiguration != null) {
            info(this, "Configuration loaded");
        }
        else {
            throw new IllegalArgumentException("There was an error loading the configuration file, Exiting Application");
        }
    }
}
