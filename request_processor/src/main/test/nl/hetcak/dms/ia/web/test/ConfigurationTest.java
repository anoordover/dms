package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;
import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This test will test the {@link ConfigurationManager}.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConfigurationTest {
    private static final String WORKING_CONFIG = "test/config/working.xml";
    private static final String MISCONFIGURATION_CONFIG = "test/config/misconfig.xml";
    private ConfigurationManager configurationManager  = new ConfigurationManager();
    
    /**
     * Create a load failure.
     */
    @Test(expected = MissingConfigurationException.class, timeout = 1000)
    public void loadConfigurationFirstTime() throws  MissingConfigurationException, MisconfigurationException {
        //cleanup default config
        File file = new File("../conf/request_processor.xml");
        if(file.exists()) {
            file.delete();
        }
        
        Assert.assertNotNull(configurationManager);
        configurationManager.loadConfiguration(null);
    }
    
    /**
     * Create a misconfiguration.
     */
    @Test(expected = MisconfigurationException.class, timeout = 1000)
    public void missconfiguration() throws  MissingConfigurationException, MisconfigurationException {
        File misconfig = new File(MISCONFIGURATION_CONFIG);
        Assert.assertNotNull(configurationManager);
        configurationManager.loadConfiguration(misconfig);
    }
    
    /**
     * Load valid configuration
     */
    @Test(timeout = 1000)
    public void configuration() throws MissingConfigurationException, MisconfigurationException {
        File working_config = new File(WORKING_CONFIG);
        Assert.assertNotNull(configurationManager);
        configurationManager.loadConfiguration(working_config);
    }
    
    /**
     * ServerConnectionInformation setting tester
     */
    @Test(timeout = 1000)
    public void connectionSettings() throws MissingConfigurationException, MisconfigurationException {
        File working_config = new File(WORKING_CONFIG);
        Assert.assertNotNull(configurationManager);
        ConfigurationImpl config = configurationManager.loadConfiguration(working_config);
        ServerConnectionInformation connectionSettings = config.getInfoArchiveServerInformation();
        Assert.assertTrue(connectionSettings.getServerAddress() != null);
        Assert.assertTrue(connectionSettings.getServerPort() > 0);
        
    }
    
    /**
     * Test empty configuration file
     */
    @Test(timeout = 500)
    public void testConfiguration() {
        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.emptyConfiguration();
        Assert.assertNotNull(configuration.getServerAddress());
        Assert.assertNotNull(configuration.getServerPort());
        Assert.assertNotNull(configuration.getPassword());
        Assert.assertNotNull(configuration.getUsername());
        Assert.assertNotNull(configuration.getLogProps());
        Assert.assertNotNull(configuration.getSearchComponentUUID());
        Assert.assertNotNull(configuration.getApplicationUUID());
        Assert.assertNotNull(configuration.getMaxResults());
        Assert.assertNotNull(configuration.getInfoArchiveServerAddress());
        Assert.assertNotNull(configuration.getInfoArchiveServerPort());
        Assert.assertNotNull(configuration.getSecurityToken());
        Assert.assertFalse(configuration.hasBasicInformation());
        
        //fill in the basic information
        configuration.setServerAddress("0.0.0.0");
        configuration.setServerPort(1234);
        configuration.setUsername("test");
        configuration.setPassword("test");
        Assert.assertTrue(configuration.hasBasicInformation());
    }
    
    /**
     * Create Test configuration
     */
    @Test
    public void createEmptyDefaultConfig() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        File file = new File(WORKING_CONFIG);
        ConfigurationImpl oldConfiguration = configurationManager.loadConfiguration(file);
        ConfigurationImpl configuration = new ConfigurationImpl();
        configurationManager.createConfiguration(configuration);
        //override file
        configurationManager.createConfiguration(oldConfiguration);
        
    }
}
