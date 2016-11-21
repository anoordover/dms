package nl.hetcak.dms.ia.web.requests;

import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.infoarchive.application.Application;
import nl.hetcak.dms.ia.web.infoarchive.search.Search;
import nl.hetcak.dms.ia.web.infoarchive.searchComposition.SearchComposition;
import nl.hetcak.dms.ia.web.infoarchive.tenant.Tenant;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class IdRequestsTest {
    private static final String NAME_BASED_CONFIG = "test/config/nameBasedConfig.xml";
    
    @Test
    public void tenantRequestTest() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(NAME_BASED_CONFIG);
        configurationManager.setCustomConfigFile(file);
        ConfigurationImpl configuration = configurationManager.loadConfiguration(true);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        
        TenantRequest tenantRequest = new TenantRequest(configuration, connectionManager.getActiveCredentials());
        List<Tenant> tenants = tenantRequest.requestTenant();
    
        Assert.assertTrue(tenants.size() > 0);
    }
    
    @Test
    public void applicationRequestTest() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(NAME_BASED_CONFIG);
        configurationManager.setCustomConfigFile(file);
        ConfigurationImpl configuration = configurationManager.loadConfiguration(true);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
    
        TenantRequest tenantRequest = new TenantRequest(configuration, connectionManager.getActiveCredentials());
        List<Tenant> tenants = tenantRequest.requestTenant();
    
        Assert.assertTrue(tenants.size() > 0);
        Tenant tenant = tenants.get(0);
        
        ApplicationRequest applicationRequest = new ApplicationRequest(configuration, connectionManager.getActiveCredentials());
        List<Application> applicationsFiltered = applicationRequest.requestApplicationsWithName(tenant, configuration.getApplicationName());
        List<Application> applications = applicationRequest.requestApplications(tenant);
        
        Assert.assertTrue(applicationsFiltered.size() > 0);
        Assert.assertTrue(applications.size() > 0);
    }
    
    @Test
    public void searchRequestTest() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(NAME_BASED_CONFIG);
        configurationManager.setCustomConfigFile(file);
        ConfigurationImpl configuration = configurationManager.loadConfiguration(true);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        
        TenantRequest tenantRequest = new TenantRequest(configuration, connectionManager.getActiveCredentials());
        List<Tenant> tenants = tenantRequest.requestTenant();
        
        Assert.assertTrue(tenants.size() > 0);
        Tenant tenant = tenants.get(0);
        
        ApplicationRequest applicationRequest = new ApplicationRequest(configuration, connectionManager.getActiveCredentials());
        List<Application> applicationsFiltered = applicationRequest.requestApplicationsWithName(tenant, configuration.getApplicationName());
        
        Assert.assertTrue(applicationsFiltered.size() > 0);
        Application application = applicationsFiltered.get(0);
        
        SearchRequest searchRequest = new SearchRequest(configuration, connectionManager.getActiveCredentials());
        List<Search> searchesFiltered = searchRequest.requestSearchWithName(application, configuration.getSearchName());
        List<Search> searches = searchRequest.requestSearch(application);
    
        Assert.assertTrue(searchesFiltered.size() > 0);
        Assert.assertTrue(searches.size() > 0);
    }
    
    @Test
    public void searchCompositionRequestTest() throws Exception {
        ConfigurationManager configurationManager  = ConfigurationManager.getInstance();
        File file = new File(NAME_BASED_CONFIG);
        configurationManager.setCustomConfigFile(file);
        ConfigurationImpl configuration = configurationManager.loadConfiguration(true);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        
        TenantRequest tenantRequest = new TenantRequest(configuration, connectionManager.getActiveCredentials());
        List<Tenant> tenants = tenantRequest.requestTenant();
        
        Assert.assertTrue(tenants.size() > 0);
        Tenant tenant = tenants.get(0);
        
        ApplicationRequest applicationRequest = new ApplicationRequest(configuration, connectionManager.getActiveCredentials());
        List<Application> applicationsFiltered = applicationRequest.requestApplicationsWithName(tenant, configuration.getApplicationName());
        
        Assert.assertTrue(applicationsFiltered.size() > 0);
        Application application = applicationsFiltered.get(0);
        
        SearchRequest searchRequest = new SearchRequest(configuration, connectionManager.getActiveCredentials());
        List<Search> searchesFiltered = searchRequest.requestSearchWithName(application, configuration.getSearchName());
        List<Search> searches = searchRequest.requestSearch(application);
        
        Assert.assertTrue(searchesFiltered.size() > 0);
        Search search = searchesFiltered.get(0);
    
        SearchCompositionRequest searchCompositionRequest = new SearchCompositionRequest(configuration, connectionManager.getActiveCredentials());
        List<SearchComposition> searchCompositionsFiltered = searchCompositionRequest.requestSearchWithName(search, configuration.getSearchCompositionName());
        List<SearchComposition> searchCompositions = searchCompositionRequest.requestSearch(search);
        
        Assert.assertTrue(searchCompositionsFiltered.size() > 0);
        Assert.assertTrue(searchCompositions.size() > 0);
    }
    
}
