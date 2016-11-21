package nl.hetcak.dms.ia.web.managers;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.exceptions.TooManyResultsException;
import nl.hetcak.dms.ia.web.infoarchive.application.Application;
import nl.hetcak.dms.ia.web.infoarchive.search.Search;
import nl.hetcak.dms.ia.web.infoarchive.searchComposition.SearchComposition;
import nl.hetcak.dms.ia.web.infoarchive.tenant.Tenant;
import nl.hetcak.dms.ia.web.requests.ApplicationRequest;
import nl.hetcak.dms.ia.web.requests.SearchCompositionRequest;
import nl.hetcak.dms.ia.web.requests.SearchRequest;
import nl.hetcak.dms.ia.web.requests.TenantRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class IdResolverManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdResolverManager.class);
    private ConfigurationManager configurationManager;
    private ConnectionManager connectionManager;
    private Configuration configuration;
    
    private Tenant tenant;
    private Application application;
    private Search search;
    private SearchComposition searchComposition;
    
    private String applicationID;
    private String searchCompositionID;
    
    private static IdResolverManager idResolverManager;
    
    private IdResolverManager() throws RequestResponseException {
        LOGGER.info("Initiating ID resolver class.");
        configurationManager = ConfigurationManager.getInstance();
        configuration = configurationManager.getCurrentConfiguration();
        connectionManager = ConnectionManager.getInstance();
        
        if(useNameBasedConfiguration()) {
            LOGGER.info("Starting Name based resolver.");
            tenant = requestTenant();
            application = requestApplication(tenant);
            search = requestSearch(application);
            searchComposition = requestSearchComposition(search);
            
            applicationID = application.getId();
            searchCompositionID = searchComposition.getId();
        } else {
            LOGGER.info("No name based resolver needed. IDs are loaded.");
            applicationID = configuration.getApplicationUUID();
            searchCompositionID = configuration.getSearchCompositionUUID();
        }
    
        LOGGER.info("IDs loaded.");
    }
    
    private boolean useNameBasedConfiguration() {
        boolean uuidMode = false;
        
        if(StringUtils.isNotBlank(configuration.getApplicationName()) && StringUtils.isNotBlank(configuration.getSearchCompositionName())
            && StringUtils.isNotBlank(configuration.getSearchName())) {
            uuidMode = true;
        }
        
        return uuidMode;
    }
    
    protected Tenant requestTenant() throws RequestResponseException {
        LOGGER.info("Requesting a list of tenants from server.");
        TenantRequest tenantRequest = new TenantRequest(configuration, connectionManager.getActiveCredentials());
        List<Tenant> tenants = tenantRequest.requestTenant();
        if(tenants.size() == 1) {
            return tenants.get(0);
        }
        throw new TooManyResultsException(4001,"Too many tenants.");
    }
    
    protected Application requestApplication(Tenant tenant) throws RequestResponseException {
        LOGGER.info("Requesting a list of applications from server.");
        ApplicationRequest applicationRequest = new ApplicationRequest(configuration, connectionManager.getActiveCredentials());
        String applicationName = configurationManager.getCurrentConfiguration().getApplicationName();
        List<Application> applications = applicationRequest.requestApplicationsWithName(tenant, applicationName);
        if(applications.size() == 1) {
            return applications.get(0);
        } else {
            for(Application application : applications) {
                if(application.getName().contentEquals(applicationName)) {
                    return application;
                }
            }
        }
        LOGGER.error("Cannot find an Application with the name set in the config.");
        throw new MisconfigurationException("Application Name is invalid.");
    }
    
    protected Search requestSearch(Application application) throws RequestResponseException {
        LOGGER.info("Requesting a list of search interfaces from server.");
        SearchRequest searchRequest = new SearchRequest(configuration, connectionManager.getActiveCredentials());
        String searchName = configurationManager.getCurrentConfiguration().getSearchName();
        List<Search> searches = searchRequest.requestSearchWithName(application, searchName);
        if(searches.size() == 1) {
            return searches.get(0);
        } else {
            for(Search search : searches) {
                if(application.getName().contentEquals(searchName)) {
                    return search;
                }
            }
        }
        LOGGER.error("Cannot find an Search Interface with the name set in the config.");
        throw new MisconfigurationException("Search Name is invalid.");
        
    }
    
    protected SearchComposition requestSearchComposition(Search search) throws RequestResponseException {
        LOGGER.info("Requesting a list of search compositions from server.");
        SearchCompositionRequest searchCompositionRequest = new SearchCompositionRequest(configuration, connectionManager.getActiveCredentials());
        String searchCompositionName = configurationManager.getCurrentConfiguration().getSearchCompositionName();
        List<SearchComposition> searchCompositions = searchCompositionRequest.requestSearchWithName(search, searchCompositionName);
        if(searchCompositions.size() == 1) {
            return searchCompositions.get(0);
        } else {
            for(SearchComposition searchComposition : searchCompositions) {
                if(searchComposition.getName().contentEquals(searchCompositionName)) {
                    return searchComposition;
                }
            }
        }
        LOGGER.error("Cannot find an Search Composition with the name set in the config.");
        throw new MisconfigurationException("SearchComposition Name is invalid.");
    }
    
    public synchronized static IdResolverManager getInstance() throws RequestResponseException {
        if(idResolverManager == null) {
            idResolverManager = new IdResolverManager();
        }
        return idResolverManager;
    }
    
    public String getApplicationID() {
        return applicationID;
    }
    
    public String getSearchCompositionID() {
        return searchCompositionID;
    }
}
