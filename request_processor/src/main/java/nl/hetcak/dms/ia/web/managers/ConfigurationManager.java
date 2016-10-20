package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import java.io.File;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String DEFAULT_CONFIG_FILE_NAME = "config/request_processor.xml";
    private static final String DEFAULT_CONFIG_FOLDER_NAME = "config";
    
    public File getDefaultConfigurationFile() {
        return new File(DEFAULT_CONFIG_FILE_NAME);
    }
    
    private boolean checkConfigurationExist(File configFile) {
        if(configFile.exists()) {
            LOGGER.debug("Found configuration file, using path: "+configFile.getPath());
            return true;
        } else {
            LOGGER.error("Unable to locate configuration file.");
            LOGGER.debug("Failed to find file, using path: "+configFile.getPath());
        }
        return false;
    }
    
    public ConfigurationImpl loadConfiguration(File file) throws MissingConfigurationException, MisconfigurationException {
        if(file == null)
            file = new File(DEFAULT_CONFIG_FILE_NAME);
        
        if(checkConfigurationExist(file)) {
            try {
                JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return (ConfigurationImpl) unmarshaller.unmarshal(file);
            } catch (JAXBException jaxbExc) {
                LOGGER.error("Failed to load configuration.", jaxbExc);
                throw new MisconfigurationException("Failed to load configuration.", jaxbExc);
            }
        } else {
            LOGGER.error("Can't find configuration file at "+file.getPath());
            throw new MissingConfigurationException("Can't find configuration file at "+file.getPath());
        }
    }
    
    public ConfigurationImpl createConfiguration(ConfigurationImpl currentConfiguration) {
        File configFolder = new File(DEFAULT_CONFIG_FOLDER_NAME);
        if(!configFolder.exists()) {
            configFolder.mkdir();
        }
        
        File file = new File(DEFAULT_CONFIG_FILE_NAME);
        
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
}