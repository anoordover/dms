package nl.hetcak.dms.ia.web;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.infoarchive.application.Application;
import nl.hetcak.dms.ia.web.infoarchive.search.Search;
import nl.hetcak.dms.ia.web.infoarchive.searchComposition.SearchComposition;
import nl.hetcak.dms.ia.web.infoarchive.tenant.Tenant;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.ApplicationRequest;
import nl.hetcak.dms.ia.web.requests.SearchCompositionRequest;
import nl.hetcak.dms.ia.web.requests.SearchRequest;
import nl.hetcak.dms.ia.web.requests.TenantRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@Path("/config")
public class ConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized Response checkConfig(@Context HttpServletRequest httpRequest) {
        LOGGER.info(Version.PROGRAM_NAME + " " + Version.currentVersion());
        Calendar calendar = Calendar.getInstance();
        LOGGER.info("Got Request from "+httpRequest.getRemoteAddr());
        LOGGER.info("Running log checker. ("+calendar.getTime().toString()+")");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading config file...\n");
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        try {
            Configuration configuration = configurationManager.loadConfiguration(false);
            if (!configuration.hasBasicInformation()) {
                throw new MisconfigurationException("Some elements of the configuration have not been configured.");
            }
            stringBuilder.append("[OK] Config file found and loaded.\n");
        } catch (RequestResponseException rrExc) {
            LOGGER.error("There is a problem with the config file.", rrExc);
            stringBuilder.append("[ERROR] Config file not found.\n");
        }
        LOGGER.info(stringBuilder.toString());
        return Response.ok(stringBuilder.toString()).build();
    }
    
    @GET
    @Path("/tenants")
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized Response tenants(@Context HttpServletRequest httpRequest) {
        LOGGER.info(Version.PROGRAM_NAME + " " + Version.currentVersion());
        Calendar calendar = Calendar.getInstance();
        LOGGER.info("Got Request from "+httpRequest.getRemoteAddr());
        LOGGER.info("Running log checker. ("+calendar.getTime().toString()+")");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Starting Tenant request\n");
        try {
            ConfigurationManager configurationManager = ConfigurationManager.getInstance();
            Configuration configuration = configurationManager.getCurrentConfiguration();
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            TenantRequest tenantRequest = new TenantRequest(configuration, connectionManager.getActiveCredentials());
            ApplicationRequest applicationRequest = new ApplicationRequest(configuration, connectionManager.getActiveCredentials());
            SearchRequest searchRequest = new SearchRequest(configuration, connectionManager.getActiveCredentials());
            SearchCompositionRequest searchCompositionRequest = new SearchCompositionRequest(configuration, connectionManager.getActiveCredentials());
            
            
            for(Tenant t : tenantRequest.requestTenant()) {
                stringBuilder.append(t.getName());
                stringBuilder.append(" [");
                stringBuilder.append(t.getId());
                stringBuilder.append("]\n");
                for(Application a : applicationRequest.requestApplications(t)){
    
                    stringBuilder.append("\t");
                    stringBuilder.append(a.getName());
                    stringBuilder.append(" [");
                    stringBuilder.append(a.getId());
                    stringBuilder.append("]\n");
                    for(Search s : searchRequest.requestSearch(a)){
        
                        stringBuilder.append("\t\t");
                        stringBuilder.append(s.getName());
                        stringBuilder.append(" [");
                        stringBuilder.append(s.getId());
                        stringBuilder.append("]\n");
                        for(SearchComposition sc : searchCompositionRequest.requestSearch(s)){
        
                            stringBuilder.append("\t\t\t");
                            stringBuilder.append(sc.getName());
                            stringBuilder.append(" [");
                            stringBuilder.append(sc.getId());
                            stringBuilder.append("]\n");
                        }
                    }
                }
            }
        }
        catch (RequestResponseException rrExc) {
            stringBuilder.append("[ERROR] "+rrExc.getMessage());
        }
        return Response.ok(stringBuilder.toString()).build();
    }
}
