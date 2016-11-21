package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.infoarchive.tenant.Tenant;
import nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class TenantRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantRequest.class);
    
    private Configuration configuration;
    private Credentials credentials;
    
    public TenantRequest(Configuration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
    }
    
    public List<Tenant> requestTenant() throws RequestResponseException {
        LOGGER.debug("Requesting Tenants.");
        String ia_response = executeRequest();
        LOGGER.debug("Returning list Tenants.");
        return parseResult(ia_response);
    }
    
    private String executeRequest() throws RequestResponseException {
        InfoArchiveRequestUtil requestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl("restapi/systemdata/tenants");
        LOGGER.debug("Executing Tenants Request.");
        HttpResponse response = requestUtil.executeGetRequest(url, null, requestHeader);
        try {
            return requestUtil.responseReader(response);
        }catch (IOException exc) {
            throw new RequestResponseException(exc, 9999, "Error reading response from InfoArchive.");
        }
    }
    
    private List<Tenant> parseResult(String response) {
        List<Tenant> tenantsList = new ArrayList<Tenant>();
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        if(jsonResponse.has("_embedded")) {
            JsonObject embedded = jsonResponse.getAsJsonObject("_embedded");
            JsonArray tenants = embedded.getAsJsonArray("tenants");
            for (int i = 0; i < tenants.size(); i++) {
                JsonObject tenantObject = tenants.get(i).getAsJsonObject();
                Tenant tenant = parseTenant(tenantObject);
                if(tenant != null) {
                    tenantsList.add(tenant);
                }
            }
        }
        return tenantsList;
    }
    
    private Tenant parseTenant(JsonObject tenantObject) {
        Tenant tenant = new Tenant();
        if(tenantObject.has("name")) {
            tenant.setName(tenantObject.get("name").getAsString());
        }
        if(tenantObject.has("version")) {
            tenant.setVersion(tenantObject.get("version").getAsInt());
        }
        if(tenantObject.has("_links")) {
            JsonObject links = tenantObject.getAsJsonObject("_links");
            if(links.has("self")) {
                JsonObject self = links.getAsJsonObject("self");
                String selfUrl = self.get("href").getAsString();
                tenant.setId(selfUrl.substring(selfUrl.lastIndexOf("/") + 1));
            }
        }
        if(tenant.isNotBlank()){
            return tenant;
        }
        return null;
    }
}
