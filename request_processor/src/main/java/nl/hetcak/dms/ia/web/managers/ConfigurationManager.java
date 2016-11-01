package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.exceptions.CryptoFailureException;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.util.CryptoUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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

    private static final String LOGGER_STRING_CANT_WRITE_ENCRYPTED_FILE = "Can't write encrypted files";
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

    private File loadUnencryptedConfigFile() {
        File file = new File(DEFAULT_CONFIG_FILE_NAME);
        if (checkConfigurationExist(file)) {
            LOGGER.info("Found unencrypted config file.");
            LOGGER.warn("It is highly recommended that the config is encrypted.");
            return file;
        }
        LOGGER.error("No config file found.");
        return null;
    }

    private File loadEncryptedConfigFile() {
        File file = new File(DEFAULT_CONFIG_FILE_ENCRYPTED_NAME);
        if (checkConfigurationExist(file)) {
            LOGGER.info("Loading encrypted config file.");
            encrypted = true;
            return file;
        }
        LOGGER.error("No config file found.");
        return null;
    }

    private File loadKeyFile() {
        File file = new File(DEFAULT_CONFIG_KEY_FILE_NAME);
        if (checkConfigurationExist(file)) {
            LOGGER.info("Loading key file.");
            return file;
        }
        LOGGER.error("No key file found.");
        return null;
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

    /**
     * Sets a custom loaded Config.
     *
     * @param customConfiguration the custom configuration
     */
    public void setLoadedConfiguration(ConfigurationImpl customConfiguration) {
        this.mobjLoadedConfiguration = customConfiguration;
        encrypted = false;
    }

    private File loadConfigFile() {
        File file = loadEncryptedConfigFile();

        if (file == null) {
            LOGGER.info("No encrypted config file found.");
            applyEncryption = true;
            file = loadUnencryptedConfigFile();
        }
        return file;
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
        try {
            byte[] buffer = IOUtils.toByteArray(new FileInputStream(file));
            if (encrypted)
                buffer = decryptConfig(buffer);
            InputStream configStream = new ByteArrayInputStream(buffer);

            mobjLoadedConfiguration = unmarshalConfigFile(configStream);
            if (!encrypted && applyEncryption) {
                LOGGER.warn("Config file is not encrypted. Applying encryption.");
                encryptCurrentConfiguration();
            }
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

    private byte[] decryptConfig(byte[] configBuffer) throws MissingConfigurationException, MisconfigurationException {
        LOGGER.info("Start decrypting file");
        try {
            File key = loadKeyFile();
            if (key != null) {
                byte[] keyBuffer = IOUtils.toByteArray(new FileInputStream(key));
                configBuffer = CryptoUtil.decrypt(configBuffer, keyBuffer);
                return configBuffer;
            }
            throw new MissingConfigurationException("No key found.");

        } catch (CryptoFailureException cryFailExc) {
            throw new MisconfigurationException("Something went wrong during decrypting.", cryFailExc);
        } catch (FileNotFoundException fnfExc) {
            throw new MissingConfigurationException("Decryption Key is missing.", fnfExc);
        } catch (IOException ioExc) {
            throw new MissingConfigurationException("Unable to find Key file..", ioExc);
        }
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


    private void encryptCurrentConfiguration() throws RequestResponseException {
        try {
            byte[] configData = marshalConfiguration(mobjLoadedConfiguration);
            byte[] keyData = CryptoUtil.createRandomKey();
            byte[] encryptedConfigData = CryptoUtil.encrypt(configData, keyData);
            File encryptedConfigFile = new File(DEFAULT_CONFIG_FILE_ENCRYPTED_NAME);
            File keyFile = new File(DEFAULT_CONFIG_KEY_FILE_NAME);
            FileUtils.writeByteArrayToFile(encryptedConfigFile, encryptedConfigData);
            FileUtils.writeByteArrayToFile(keyFile, keyData);
        } catch (IOException ioExc) {
            LOGGER.error(LOGGER_STRING_CANT_WRITE_ENCRYPTED_FILE, ioExc);
            throw new RequestResponseException(ioExc, -1, LOGGER_STRING_CANT_WRITE_ENCRYPTED_FILE);
        } catch (InvalidKeyException invKeyExc) {
            LOGGER.error(LOGGER_STRING_CANT_WRITE_ENCRYPTED_FILE, invKeyExc);
            throw new CryptoFailureException(invKeyExc);
        }
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