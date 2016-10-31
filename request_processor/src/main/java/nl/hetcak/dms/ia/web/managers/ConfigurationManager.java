package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.exceptions.CryptoFailureException;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.util.CryptoUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import java.io.*;
import java.security.InvalidKeyException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String DEFAULT_CONFIG_FILE_NAME = "../conf/request_processor.xml";
    private static final String DEFAULT_CONFIG_FILE_ENCRYPTED_NAME = "../conf/request_processor_enc.xml";
    private static final String DEFAULT_CONFIG_KEY_FILE_NAME = "../conf/request_processor.key";
    private static final String DEFAULT_CONFIG_FOLDER_NAME = "config";
    private Boolean encrypted = false;
    
    public File getDefaultConfigurationFile() {
        return new File(DEFAULT_CONFIG_FILE_NAME);
    }
    
    private boolean checkConfigurationExist(File configFile) {
        if(configFile.exists()) {
            LOGGER.debug("Found configuration file, using path: "+configFile.getAbsolutePath());
            return true;
        } else {
            LOGGER.error("Unable to locate configuration file with path: "+configFile.getAbsolutePath());
            LOGGER.debug("Failed to find file, using path: "+configFile.getPath());
        }
        return false;
    }
    
    private File loadDefaultConfigFiles() {
        File file = new File(DEFAULT_CONFIG_FILE_ENCRYPTED_NAME);
        if(!checkConfigurationExist(file)) {
            file = null;
        } else {
            LOGGER.info("Found encrypted config file.");
            encrypted = true;
        }
        file = new File(DEFAULT_CONFIG_FILE_NAME);
        if(!checkConfigurationExist(file)) {
            file = null;
        } else {
            LOGGER.info("Found unencrypted config file.");
            LOGGER.warn("It is highly recommended that the config is encrypted.");
        }
        return file;
    }
    
    public ConfigurationImpl loadConfiguration(File file) throws MissingConfigurationException, MisconfigurationException {
        LOGGER.info("Loading Configuration.");
        if(file == null) {
            file = loadDefaultConfigFiles();
        } else {
            LOGGER.info("Config file has already been loaded or this application is running in test modes.");
            LOGGER.warn("It is highly recommended that the config is encrypted.");
        }
        
        if(file != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                LOGGER.info("Configuration Loaded.");
                return (ConfigurationImpl) unmarshaller.unmarshal(file);
            } catch (JAXBException jaxbExc) {
                LOGGER.error("Failed to load configuration.", jaxbExc);
                throw new MisconfigurationException("Failed to load configuration.", jaxbExc);
            } catch (NullPointerException nullPointExc) {
                LOGGER.error("Failed to load configuration.", nullPointExc);
                throw new MisconfigurationException("Failed to load configuration.", nullPointExc);
            }
        } else {
            LOGGER.error("Can't find configuration");
            throw new MissingConfigurationException("Can't find configuration");
        }
    }
    
    private File encryptCurrentConfiguration(File currentConfig, File key) throws IOException, InvalidKeyException, CryptoFailureException {
        byte[] configData= IOUtils.toByteArray(new FileInputStream(currentConfig));
        byte[] keyData = IOUtils.toByteArray(new FileInputStream(key));
        byte[] encryptedData = CryptoUtil.encrypt(configData, keyData);
        File encryptedFile = new File(DEFAULT_CONFIG_FILE_ENCRYPTED_NAME);
        FileUtils.writeByteArrayToFile(encryptedFile, encryptedData);
        return  encryptedFile;
    }
    
    public void createConfiguration(ConfigurationImpl currentConfiguration) {
        LOGGER.info("Create a new configuration file on the drive.");
        File configFolder = new File(DEFAULT_CONFIG_FOLDER_NAME);
        if(!configFolder.exists()) {
            configFolder.mkdir();
        }
        
        File file = new File(DEFAULT_CONFIG_FILE_NAME);
        
        try {
            JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(currentConfiguration, file);
            LOGGER.info("Configuration file saved to drive.");
        } catch (JAXBException jaxbExc) {
            LOGGER.error("Failed to create configuration.", jaxbExc);
        }
    }
}