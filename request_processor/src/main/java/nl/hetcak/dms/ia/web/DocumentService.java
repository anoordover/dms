package nl.hetcak.dms.ia.web;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.configuration.ConfigurationImpl;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.RequestRecord;
import nl.hetcak.dms.ia.web.restfull.consumes.ListDocumentRequest;
import nl.hetcak.dms.ia.web.restfull.produces.ListDocumentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by admjzimmermann on 13-10-2016.
 *
 * @author Joury.Zimmermann@AMPLEXOR.com
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@Path("/rest")
public class DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
    
    @POST
    @Path("/listDocuments")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response listDocuments(String sBody) throws Exception{
        StringBuilder input = new StringBuilder();
        input.append("<request>");
        input.append(sBody);
        input.append("</request>");
    
        ListDocumentRequest request = ListDocumentRequest.unmarshalRequest(input.toString());
        if(request.getArchivePersonNumber() != null) {
            //disable content grabbing with empty strings.
            if(request.getArchivePersonNumber().length() > 0) {
                ConnectionManager connectionManager = ConnectionManager.getInstance();
    
                RequestRecord requestRecord = new RequestRecord(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
                ListDocumentResponse response = new ListDocumentResponse(requestRecord.requestListDocuments(request.getArchivePersonNumber()));
                return Response.ok(response.getAsXML()).build();
            }
        }
        LOGGER.warn("Content grabbing attempt detected. Returning 406 - unaccepted http error.");
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("<error><code>406</code><description>Unacceptable request content detected.</description></error>").build();
    }

    @GET
    @Path("/document")
    @Produces("application/pdf")
    public Response getDocument() {
        return Response.ok().build();
    }
    
    /**
     * Basic response
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response defaultResponse() {
        return Response.ok("<html><head><title>DMS</title></head><body><h1>DMS Request Processor</h1><p>System running</p></body></html>").build();
    }
    
    @GET
    @Path("/checkConfig")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading config file...\n");
        ConfigurationManager configurationManager = new ConfigurationManager();
        try {
            Configuration configuration = configurationManager.loadConfiguration();
            stringBuilder.append("[OK] Config file found and loaded.\n");
        } catch (MissingConfigurationException missingcexc) {
            stringBuilder.append("[ERROR] Config file not found.\n");
            
            //todo: remove create command
            ConfigurationImpl config = new ConfigurationImpl();
            config.emptyConfiguration();
            configurationManager.createConfiguration(config);
        } catch (MisconfigurationException mcexc) {
            stringBuilder.append("Config file found.\n");
            stringBuilder.append("[ERROR] Config file is invalid.\n");
        }
        
        return Response.ok(stringBuilder.toString()).build();
    }
}
