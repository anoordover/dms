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
    private static ConnectionManager connectionManager;
    private File configurationFile;
    private Credentials credentials;
    private Configuration configuration;

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
        if (connectionManager == null) {
            LOGGER.info("Creating a new ConnectionManager instance.");
            connectionManager = new ConnectionManager();
        }
        LOGGER.info("Returning ConnectionManager instance.");
        return connectionManager;
    }

    /**
     * Set a new configuration file.
     *
     * @param configurationFile the new configuration file.
     */
    public void setConfigurationFile(File configurationFile) {
        LOGGER.info("Setting new Configuration file.");
        if (configuration != null) {
            LOGGER.debug("Unload existing configuration and credentials.");
            configuration = null;
            credentials = null;
        }
        this.configurationFile = configurationFile;
    }

    /**
     * Gets the current active Credentials.
     *
     * @return Active credentials that are ready to be used.
     * @throws MissingConfigurationException    Can't find configuration.
     * @throws MisconfigurationException        Missing basic configuration settings.
     * @throws LoginFailureException            Wrong login information.
     * @throws ServerConnectionFailureException Can't connect to server.
     */
    public Credentials getActiveCredentials() throws RequestResponseException {
        LOGGER.info("Requesting Active Credentials.");

        if (credentials == null) {
            LOGGER.info("No Active Credentials found. Loading credentials from config.");
            credentials = getConfiguration().getInfoArchiveCredentials();
        }

        if (!credentials.isSecurityTokenValid()) {
            LOGGER.info("Security token is not valid. Starting LoginRequest.");
            LoginRequest loginRequest = new LoginRequest(getConfiguration());

            if (credentials.getSecurityToken() == null) {
                LOGGER.info("Requesting new security token. executing Login Request.");
                credentials = loginRequest.loginInfoArchive();
            } else if (credentials.getRecoveryToken() != null) {
                LOGGER.info("Refreshing security token. executing Login Request.");
                try {
                    credentials = loginRequest.refreshCredentialsInfoArchive(credentials);
                } catch (RequestResponseException rrExc) {
                    LOGGER.error("Refresh token error.", rrExc);
                    //retry-login
                    credentials.setRecoveryToken("");
                    credentials = loginRequest.loginInfoArchive();
                }
            }
        }
        LOGGER.info("Returning Active token.");
        return credentials;
    }

    /**
     * Gets the current configuration.
     *
     * @return the {@link Configuration} object.
     * @throws MissingConfigurationException Can't find configuration
     * @throws MisconfigurationException     Missing basic configuration settings.
     */
    public Configuration getConfiguration() throws RequestResponseException {
        LOGGER.info("Getting current configuration.");
        if (configuration == null) {
            LOGGER.info("Load config file.");
            if (configurationFile != null) {
                configuration = loadConfigurationFromFile(configurationFile);
            } else {
                ConfigurationManager configurationManager = ConfigurationManager.getInstance();
                configuration = configurationManager.loadConfiguration(false);
            }
        }
        LOGGER.info("Returning config file.");
        return configuration;
    }

    private Configuration loadConfigurationFromFile(File file) throws RequestResponseException {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.setCustomConfigFile(file);
        return configurationManager.loadConfiguration(true);
    }
}
