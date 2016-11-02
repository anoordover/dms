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
    
    /**
     * Create a load failure.
     */
    @Test(expected = MissingConfigurationException.class, timeout = 1000)
    public void loadConfigurationFirstTime() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        //cleanup default config
        File file = new File("../conf/request_processor.xml");
        if(file.exists()) {
            file.delete();
        }
        
        Assert.assertNotNull(configurationManager);
        configurationManager.setCustomConfigFile(file);
        configurationManager.loadConfiguration(true);
    }
    
    /**
     * Create a misconfiguration.
     */
    @Test(expected = MisconfigurationException.class, timeout = 1250)
    public void missconfiguration() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File misconfig = new File(MISCONFIGURATION_CONFIG);
        Assert.assertNotNull(configurationManager);
        configurationManager.setCustomConfigFile(misconfig);
        configurationManager.loadConfiguration(true);
    }
    
    /**
     * Load valid configuration
     */
    @Test(timeout = 1000)
    public void configuration() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File working_config = new File(WORKING_CONFIG);
        Assert.assertNotNull(configurationManager);
        configurationManager.setCustomConfigFile(working_config);
        configurationManager.loadConfiguration(true);
    }
    
    /**
     * ServerConnectionInformation setting tester
     */
    @Test(timeout = 1000)
    public void connectionSettings() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File working_config = new File(WORKING_CONFIG);
        Assert.assertNotNull(configurationManager);
        configurationManager.setCustomConfigFile(working_config);
        ConfigurationImpl config = configurationManager.loadConfiguration(true);
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
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(WORKING_CONFIG);
        configurationManager.setCustomConfigFile(file);
        ConfigurationImpl oldConfiguration = configurationManager.loadConfiguration(true);
        ConfigurationImpl configuration = new ConfigurationImpl();
        configurationManager.createConfiguration(configuration);
        //override file
        configurationManager.createConfiguration(oldConfiguration);
        
    }


    /**
     * Create Test configuration
     */
    @Test
    public void encrypt() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        configurationManager.loadConfiguration(false);
        configurationManager.loadConfiguration(false);
        configurationManager.loadConfiguration(false);

    }
}
