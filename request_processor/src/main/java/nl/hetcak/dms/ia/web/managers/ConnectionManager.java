package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.LoginFailureException;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.requests.LoginRequest;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConnectionManager {
    private static ConnectionManager connectionManager;
    private Credentials credentials;
    private Configuration configuration;
    
    private ConnectionManager() {
        
    }
    
    public static ConnectionManager getInstance() {
        if(connectionManager == null) {
            connectionManager = new ConnectionManager();
        }
        return connectionManager;
    }
    
    public Credentials getActiveCredentials()  throws MissingConfigurationException, MisconfigurationException,
        LoginFailureException, ServerConnectionFailureException {
        
        if(credentials == null) {
            credentials = getConfiguration().getInfoArchiveCredentials();
        }
        
        if(!credentials.isSecurityTokenValid()) {
            LoginRequest loginRequest = new LoginRequest(getConfiguration());
            
            if(credentials.getSecurityToken() == null) {
                credentials = loginRequest.loginInfoArchive();
            } else if(credentials.getRecoveryToken() != null) {
                credentials =  loginRequest.refreshCredentialsInfoArchive(credentials);
            }
        }
        
        return credentials;
    }
    
    public Configuration getConfiguration() throws MissingConfigurationException, MisconfigurationException {
        if(configuration == null) {
            configuration = loadConfiguration();
        }
        return configuration;
    }
    
    private Configuration loadConfiguration() throws MissingConfigurationException, MisconfigurationException {
        ConfigurationManager configurationManager = new ConfigurationManager();
        return configurationManager.loadConfiguration();
    }
}
