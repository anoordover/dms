package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.infoarchive.application.Application;
import nl.hetcak.dms.ia.web.infoarchive.tenant.Tenant;
import nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ApplicationRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRequest.class);
    private Configuration configuration;
    private Credentials credentials;
    
    public ApplicationRequest(Configuration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
    }
    
    public List<Application> requestApplications(Tenant tenant) throws RequestResponseException {
        LOGGER.debug("Requesting Application.");
        String ia_response = executeRequest(tenant, null);
        LOGGER.debug("Returning list Application.");
        return parseResult(ia_response);
    }
    
    public List<Application> requestApplicationsWithName(Tenant tenant, String name) throws RequestResponseException {
        LOGGER.debug("Requesting Application.");
        String ia_response = executeRequest(tenant, name);
        LOGGER.debug("Returning list Application.");
        return parseResult(ia_response);
    }
    
    
    private String executeRequest(Tenant tenant, String name) throws RequestResponseException {
        InfoArchiveRequestUtil requestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        StringBuilder urlBuilder = new StringBuilder("restapi/systemdata/tenants/");
        urlBuilder.append(tenant.getId());
        urlBuilder.append("/applications");
        try {
            if(StringUtils.isNotBlank(name)) {
                urlBuilder.append("?spel=?[name=='");
                urlBuilder.append(URLEncoder.encode(name, "UTF-8"));
                urlBuilder.append("']");
            }
        } catch (UnsupportedEncodingException unsEncExc){
            throw new RequestResponseException(unsEncExc, 9999, "Encoding failed.");
        }
    
        String url = requestUtil.getServerUrl(urlBuilder.toString());
        LOGGER.debug("Executing Application Request."+ url);
        HttpResponse response = requestUtil.executeGetRequest(url, null, requestHeader);
        try {
            return requestUtil.responseReader(response);
        }catch (IOException exc) {
            throw new RequestResponseException(exc, 9999, "Error reading response from InfoArchive.");
        }
    }
    
    private List<Application> parseResult(String response) {
        List<Application> tenantsList = new ArrayList<Application>();
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        if(jsonResponse.has("_embedded")) {
            JsonObject embedded = jsonResponse.getAsJsonObject("_embedded");
            JsonArray applications = embedded.getAsJsonArray("applications");
            for (int i = 0; i < applications.size(); i++) {
                JsonObject applicationObject = applications.get(i).getAsJsonObject();
                Application application = parseApplication(applicationObject);
                if(application != null) {
                    tenantsList.add(application);
                }
            }
        }
        return tenantsList;
    }
    
    private Application parseApplication(JsonObject tenantObject) {
        Application application = new Application();
        if(tenantObject.has("name")) {
            application.setName(tenantObject.get("name").getAsString());
        }
        if(tenantObject.has("version")) {
            application.setVersion(tenantObject.get("version").getAsInt());
        }
        if(tenantObject.has("_links")) {
            JsonObject links = tenantObject.getAsJsonObject("_links");
            if(links.has("self")) {
                JsonObject self = links.getAsJsonObject("self");
                String selfUrl = self.get("href").getAsString();
                application.setId(selfUrl.substring(selfUrl.lastIndexOf("/") + 1));
            }
        }
        if(application.isNotBlank()){
            return application;
        }
        return null;
    }
}
