package com.amplexor.ia.configuration;

import com.amplexor.ia.exception.ExceptionHelper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;

import static com.amplexor.ia.Logger.*;

/**
 * The {@link ConfigManager} is responsible for loading and parsing a configuration file containing the configuration for this instance of the SIP Packager
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


    /**
     * Loads the configuration contained in the XML file that was passed to this instance of the ConfigManager
     * @throws IllegalArgumentException If there was a problem while loading of the configuration file
     */
    public void loadConfiguration() throws IllegalArgumentException {
        info(this, "Loading Configuration from " + msConfigPath);
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("IAArchiver", SIPPackagerConfiguration.class);
        objXStream.autodetectAnnotations(true);
        try {
            File mobjConfigFile = new File(msConfigPath);
            if(mobjConfigFile.exists()) {
                mobjConfiguration = (SIPPackagerConfiguration) objXStream.fromXML(mobjConfigFile);
                if (mobjConfiguration != null) {
                    info(this, "Configuration loaded");
                } else {
                    throw new IllegalArgumentException("There was an error loading the configuration file, Exiting Application");
                }
            } else {
                throw new IllegalArgumentException("The file " + msConfigPath + " doesn't exist!");
            }
        } catch (ClassCastException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INIT_INVALID_CONFIGURATION, ex);
        }
    }
}
