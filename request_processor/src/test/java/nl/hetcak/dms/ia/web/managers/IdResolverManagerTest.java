package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.infoarchive.application.Application;
import nl.hetcak.dms.ia.web.infoarchive.search.Search;
import nl.hetcak.dms.ia.web.infoarchive.searchComposition.SearchComposition;
import nl.hetcak.dms.ia.web.infoarchive.tenant.Tenant;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class IdResolverManagerTest {
    private static final String NAME_BASED_CONFIG = "test/config/nameBasedConfig.xml";
    
    @Test
    public void idManagerTest() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(NAME_BASED_CONFIG);
        configurationManager.setCustomConfigFile(file);
        configurationManager.loadConfiguration(true);
        
        IdResolverManager resolverManager = IdResolverManager.getInstance();
        Assert.assertTrue(StringUtils.isNotBlank(resolverManager.getApplicationID()));
        Assert.assertTrue(StringUtils.isNotBlank(resolverManager.getSearchCompositionID()));
    }
    
    @Test
    public void sendRequests() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(NAME_BASED_CONFIG);
        configurationManager.setCustomConfigFile(file);
        configurationManager.loadConfiguration(true);
        
        IdResolverManager resolverManager = IdResolverManager.getInstance();
        Tenant tenant =resolverManager.requestTenant();
        Application application = resolverManager.requestApplication(tenant);
        Search search = resolverManager.requestSearch(application);
        SearchComposition searchComposition = resolverManager.requestSearchComposition(search);
        
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(application);
        Assert.assertNotNull(search);
        Assert.assertNotNull(searchComposition);
        
    }
}
