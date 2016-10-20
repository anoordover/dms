package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.LoginFailureException;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.LoginRequest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionTest {
    private static final String WORKING_CONFIG = "test/config/working.xml";
    private static final String MISCONFIGURATION_CONFIG = "test/config/misconfig.xml";
    private ConfigurationManager configurationManager  = new ConfigurationManager();
    
    /**
     * Try to create ConnectionManager with bad configuration file
     */
    @Test(expected = MisconfigurationException.class,timeout = 1000)
    public void createConnectionManagerWithBadConfig() throws MissingConfigurationException, MisconfigurationException {
        File misconfig = new File(MISCONFIGURATION_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(misconfig);
        Configuration configuration = connectionManager.getConfiguration();
        assertNotNull(connectionManager);
    }
    
    /**
     * Try to create ConnectionManager with working configuration file.
     */
    @Test(timeout = 1000)
    public void createConnectionManagerWithWorkingConfig() throws MissingConfigurationException, MisconfigurationException {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Configuration configuration = connectionManager.getConfiguration();
        assertNotNull(connectionManager);
        assertNotNull(configuration.getApplicationUUID());
    }
    
    //external connection
    @Test
    public void getActiveCredentials() throws MissingConfigurationException, MisconfigurationException, LoginFailureException, ServerConnectionFailureException {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
    }
    
    //external ServerConnectionInformation
    @Test
    public void getActiveCredentialsWithRefreshToken() throws MissingConfigurationException, MisconfigurationException, LoginFailureException, ServerConnectionFailureException {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertTrue(credentials.isSecurityTokenValid());
        assertTrue(credentials.getSecurityToken() != null);
        assertTrue(credentials.getRecoveryToken() != null);
        credentials.setSecurityTokenInvalidationTime(Calendar.getInstance());
        LoginRequest loginRequest = new LoginRequest(connectionManager.getConfiguration());
        assertFalse(credentials.isSecurityTokenValid());
        credentials = loginRequest.refreshCredentialsInfoArchive(credentials);
        assertTrue(credentials.isSecurityTokenValid());
    }
    
}
