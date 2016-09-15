package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ConfigManager {
    private static Logger logger = Logger.getLogger("ConfigManager");

    private String msConfigPath;
    private SIPPackagerConfiguration mobjConfiguration;

    public ConfigManager(String sConfigPath) {
        msConfigPath = sConfigPath;
    }

    public SIPPackagerConfiguration getConfiguration() {
        return mobjConfiguration;
    }


    public void loadConfiguration() {
        logger.info("Loading Configuration from " + msConfigPath);
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("IAArchiver", SIPPackagerConfiguration.class);
        objXStream.autodetectAnnotations(true);
        mobjConfiguration = (SIPPackagerConfiguration)objXStream.fromXML(new File(msConfigPath));
    }
}
