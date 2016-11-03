package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.util.CryptoUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import java.io.*;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String DEFAULT_CONFIG_FILE_NAME = "../conf/request_processor.xml";
    private static final String DEFAULT_CONFIG_KEY_FILE_NAME = "../conf/request_processor.key";

    private static ConfigurationManager mobjConfigurationManager;
    private Boolean encrypted = false;
    private Boolean applyEncryption = false;
    private ConfigurationImpl mobjLoadedConfiguration;
    private File mobjCustomFile;

    private ConfigurationManager() {
        //use getInstance.
    }

    public static ConfigurationManager getInstance() {
        if (mobjConfigurationManager == null) {
            mobjConfigurationManager = new ConfigurationManager();
        }
        return mobjConfigurationManager;
    }

    private boolean checkConfigurationExist(File configFile) {
        if (configFile.exists()) {
            LOGGER.debug("Found configuration file, using path: " + configFile.getAbsolutePath());
            return true;
        } else {
            LOGGER.error("Unable to locate configuration file with path: " + configFile.getAbsolutePath());
            LOGGER.debug("Failed to find file, using path: " + configFile.getPath());
        }
        return false;
    }

    private byte[] getKey() throws RequestResponseException {
        File file = new File(DEFAULT_CONFIG_KEY_FILE_NAME);
        if (checkConfigurationExist(file)) {
            LOGGER.info("Loading key file.");
            try{
                FileInputStream inputStream = new FileInputStream(file);
                byte[] buffer = IOUtils.toByteArray(inputStream);
                inputStream.close();
                return buffer;
            } catch (FileNotFoundException fnfExc) {
                LOGGER.error("Key file not found.");
                throw new MissingConfigurationException("Unable to find Key file.");
            } catch (IOException ioExc) {
                LOGGER.error("Error reading key file.");
                throw new MissingConfigurationException("Unable to read Key file.");
            }
        }
        LOGGER.error("No key file found.");
        throw new MissingConfigurationException("Unable to find Key file.");
    }

    /**
     * Gets loaded configuration.
     *
     * @return Currently loaded configuration.
     * @throws MissingConfigurationException Can't find configuration file.
     * @throws MisconfigurationException     Problems during parsing config file.
     */
    public Configuration getCurrentConfiguration() throws RequestResponseException {
        if (mobjLoadedConfiguration == null) {
            mobjLoadedConfiguration = loadConfiguration(false);
        }
        LOGGER.info("Using config in memory.");
        return mobjLoadedConfiguration;
    }

    private File loadConfigFile() {
        File file = new File(DEFAULT_CONFIG_FILE_NAME);
        if (checkConfigurationExist(file)) {
            LOGGER.info("Found unencrypted config file.");
            LOGGER.warn("It is highly recommended that the config is encrypted.");
            return file;
        }
        LOGGER.error("No config file found.");
        return null;
    }

    public ConfigurationImpl loadConfiguration(boolean onlyLoadCustom) throws RequestResponseException {
        LOGGER.info("Loading Configuration.");
        File file = mobjCustomFile;
        applyEncryption = false;
        encrypted = false;
        if (file == null) {
            LOGGER.info("No custom config file specified.");
            if (!onlyLoadCustom) {
                file = loadConfigFile();
            }
        } else {
            LOGGER.info("Config file has been supplied. If this file is encrypted then reading the file will fail.");
        }

        if (file == null) {
            LOGGER.info("No unencrypted config file found.");
            LOGGER.error("Can't find configuration");
            throw new MissingConfigurationException("Can't find configuration");
        }

        LOGGER.info("Loading config:" + file.getAbsolutePath());
        return readConfig(file);
    }

    private ConfigurationImpl readConfig(File file) throws RequestResponseException {
        if(!file.exists()) {
            throw new MissingConfigurationException("Config does not exist.");
        }
        try (AutoCloseInputStream fileInputStream = new AutoCloseInputStream(new FileInputStream(file))){
            byte[] buffer = IOUtils.toByteArray(fileInputStream);
            InputStream configStream = new ByteArrayInputStream(buffer);
            mobjLoadedConfiguration = unmarshalConfigFile(configStream);
            mobjLoadedConfiguration.setDecryptionKey(getKey());
            if(StringUtils.isNotBlank(mobjLoadedConfiguration.getSecurityToken())) {
                //decrypt token after loading.
                LOGGER.warn("Using token only mode will result in connection problems if the token expires.");
                mobjLoadedConfiguration.setSecurityToken(CryptoUtil.decryptValue(mobjLoadedConfiguration.getSecurityToken(),mobjLoadedConfiguration));
            }
            configStream.close();
            fileInputStream.close();
            return mobjLoadedConfiguration;
        } catch (IOException ioExc) {
            throw new MissingConfigurationException("Can't load config.", ioExc);
        }
    }

    /**
     * Sets a custom file.
     * You still need to reload the config.
     *
     * @param customFile the custom file.
     */
    public void setCustomConfigFile(File customFile) {
        this.mobjCustomFile = customFile;
    }

    private ConfigurationImpl unmarshalConfigFile(InputStream inputStream) throws MisconfigurationException {
        LOGGER.info("Start Parsing Config");
        try {
            JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (ConfigurationImpl) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException jaxbExc) {
            LOGGER.error("Failed to load configuration.", jaxbExc);
            throw new MisconfigurationException("Failed to load configuration.", jaxbExc);
        }
    }

    private byte[] marshalConfiguration(ConfigurationImpl configuration) throws MissingConfigurationException {
        if (configuration == null) {
            configuration = mobjLoadedConfiguration;
        }
        if (configuration == null) {
            throw new MissingConfigurationException("No configuration found.");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            JAXBContext context = JAXBContext.newInstance(ConfigurationImpl.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(mobjLoadedConfiguration, outputStream);
        } catch (JAXBException jaxbExc) {
            LOGGER.error("Failed to create configuration.", jaxbExc);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ioExc) {
                LOGGER.error("Failed to write to buffer.", ioExc);
            }
        }
        return outputStream.toByteArray();
    }

    public void createConfiguration(ConfigurationImpl currentConfiguration) {
        LOGGER.info("Create a new configuration file on the drive.");
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