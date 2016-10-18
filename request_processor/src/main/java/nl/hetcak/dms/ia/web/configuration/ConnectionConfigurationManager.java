package nl.hetcak.dms.ia.web.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConnectionConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionConfigurationManager.class);
    private static final String DEFAULT_CONFIG_FILE_NAME = "config/request_processor.xml";
    
    private boolean checkConfigurationExist() {
        File configFile = new File(DEFAULT_CONFIG_FILE_NAME);
        if(configFile.exists()) {
            LOGGER.debug("Found configuration file, using path: "+configFile.getPath());
            return true;
        } else {
            LOGGER.error("Unable to locate configuration file.");
            LOGGER.debug("Failed to find file, using path: "+configFile.getPath());
        }
        return false;
    }
    
    private ConfigurationImpl loadConfiguration(File file) {
        if(file != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return (ConfigurationImpl) unmarshaller.unmarshal(file);
            } catch (JAXBException jaxbExc) {
                LOGGER.error("Failed to load configuration.", jaxbExc);
            }
        } else {
            LOGGER.warn("No configuration file has been provided.");
        }
        return null;
    }
    
    private ConfigurationImpl createConfiguration(ConfigurationImpl currentConfiguration, File file) {
        if(file != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.marshal(currentConfiguration, file);
            } catch (JAXBException jaxbExc) {
                LOGGER.error("Failed to create configuration.", jaxbExc);
            }
        } else {
            LOGGER.warn("No configuration file has been provided.");
        }
        return null;
    }
    
    private ConfigurationImpl createDefaultConfiguration() {
        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.emptyConfiguration();
        return configuration;
    }
    
}