package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.*;
import nl.hetcak.dms.ia.web.requests.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);
    private static ConnectionManager sobjConnectionManager;
    private File mobjConfigurationFile;
    private Credentials mobjCredentials;
    private Configuration mobjConfiguration;
    
    private ConnectionManager() {
        LOGGER.debug("New connectionManager created.");
    }
    
    /**
     * Gets the current instance of {@link ConnectionManager}.
     *
     * @return the {@link ConnectionManager}
     */
    public static ConnectionManager getInstance() {
        LOGGER.info("Requesting ConnectionManager instance.");
        if (sobjConnectionManager == null) {
            LOGGER.info("Creating a new ConnectionManager instance.");
            sobjConnectionManager = new ConnectionManager();
        }
        LOGGER.info("Returning ConnectionManager instance.");
        return sobjConnectionManager;
    }
    
    /**
     * Set a new configuration file.
     *
     * @param configurationFile the new configuration file.
     */
    public void setConfigurationFile(File configurationFile) {
        LOGGER.info("Setting new Configuration file.");
        if (mobjConfiguration != null) {
            LOGGER.debug("Unload existing configuration and credentials.");
            mobjConfiguration = null;
            mobjCredentials = null;
        }
        this.mobjConfigurationFile = configurationFile;
    }
    
    /**
     * Gets the current active Credentials.
     *
     * @return Active credentials that are ready to be used.
     * @throws MissingConfigurationException    Can't find mobjConfiguration.
     * @throws MisconfigurationException        Missing basic mobjConfiguration settings.
     * @throws LoginFailureException            Wrong login information.
     * @throws ServerConnectionFailureException Can't connect to server.
     */
    public Credentials getActiveCredentials() throws RequestResponseException {
        LOGGER.info("Requesting Active Credentials.");
        
        if (mobjCredentials == null) {
            LOGGER.info("No Active Credentials found. Loading credentials from config.");
            mobjCredentials = getConfiguration().getInfoArchiveCredentials();
        }
        
        if (mobjCredentials.isSecurityTokenValid()) {
            LOGGER.info("Returning Active token.");
            return mobjCredentials;
        }
    
        return relogin();
    }
    
    private synchronized Credentials relogin() throws RequestResponseException {
        if (!mobjCredentials.isSecurityTokenValid()) {
            LOGGER.info("Security token is not valid. Starting LoginRequest.");
            LoginRequest loginRequest = new LoginRequest(getConfiguration());
        
            if (mobjCredentials.getSecurityToken() == null) {
                LOGGER.info("Requesting new security token. executing Login Request.");
                mobjCredentials = loginRequest.loginInfoArchive();
            } else if (mobjCredentials.getRecoveryToken() != null) {
                LOGGER.info("Refreshing security token. executing Login Request.");
                try {
                    mobjCredentials = loginRequest.refreshCredentialsInfoArchive(mobjCredentials);
                } catch (RequestResponseException rrExc) {
                    LOGGER.info("Refresh token expired.");
                    LOGGER.error("Refresh token error.", rrExc);
                    LOGGER.info("Retry Login Request.");
                    //retry-login
                    mobjCredentials.setRecoveryToken("");
                    mobjCredentials = loginRequest.loginInfoArchive();
                }
            }
        }
    
        LOGGER.info("Returning Active token.");
        return mobjCredentials;
    }
    
    /**
     * Gets the current {@link Configuration}.
     *
     * @return the {@link Configuration} object.
     * @throws MissingConfigurationException Can't find configuration
     * @throws MisconfigurationException     Missing basic configuration settings.
     */
    public Configuration getConfiguration() throws RequestResponseException {
        LOGGER.info("Getting current configuration.");
        if (mobjConfiguration == null) {
            LOGGER.info("Load config file.");
            if (mobjConfigurationFile != null) {
                mobjConfiguration = loadConfigurationFromFile(mobjConfigurationFile);
            } else {
                ConfigurationManager configurationManager = ConfigurationManager.getInstance();
                mobjConfiguration = configurationManager.loadConfiguration(false);
            }
        }
        LOGGER.info("Returning config file.");
        return mobjConfiguration;
    }
    
    private Configuration loadConfigurationFromFile(File file) throws RequestResponseException {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.setCustomConfigFile(file);
        return configurationManager.loadConfiguration(true);
    }
}
