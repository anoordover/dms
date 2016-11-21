package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.infoarchive.application.Application;
import nl.hetcak.dms.ia.web.infoarchive.search.Search;
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
public class SearchRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchRequest.class);
    private Configuration configuration;
    private Credentials credentials;
    
    public SearchRequest(Configuration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
    }
    
    public List<Search> requestSearch(Application application) throws RequestResponseException {
        LOGGER.debug("Requesting Search Interfaces.");
        String ia_response = executeRequest(application, null);
        LOGGER.debug("Returning list Search Interfaces.");
        return parseResult(ia_response);
    }
    
    public List<Search> requestSearchWithName(Application application, String name) throws RequestResponseException {
        LOGGER.debug("Requesting Search Interfaces.");
        String ia_response = executeRequest(application, name);
        LOGGER.debug("Returning list Search Interfaces.");
        return parseResult(ia_response);
    }
    
    private String executeRequest(Application application, String name) throws RequestResponseException {
        InfoArchiveRequestUtil requestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        StringBuilder urlBuilder = new StringBuilder("restapi/systemdata/applications/");
        urlBuilder.append(application.getId());
        urlBuilder.append("/searches");
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
        LOGGER.debug("Executing Search Request."+ url);
        HttpResponse response = requestUtil.executeGetRequest(url, null, requestHeader);
        try {
            return requestUtil.responseReader(response);
        }catch (IOException exc) {
            throw new RequestResponseException(exc, 9999, "Error reading response from InfoArchive.");
        }
    }
    
    private List<Search> parseResult(String response) {
        List<Search> searchList = new ArrayList<Search>();
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        if(jsonResponse.has("_embedded")) {
            JsonObject embedded = jsonResponse.getAsJsonObject("_embedded");
            JsonArray applications = embedded.getAsJsonArray("searches");
            for (int i = 0; i < applications.size(); i++) {
                JsonObject searchObject = applications.get(i).getAsJsonObject();
                Search search = parseSearch(searchObject);
                if(search != null) {
                    searchList.add(search);
                }
            }
        }
        return searchList;
    }
    
    private Search parseSearch(JsonObject tenantObject) {
        Search search = new Search();
        if(tenantObject.has("name")) {
            search.setName(tenantObject.get("name").getAsString());
        }
        if(tenantObject.has("version")) {
            search.setVersion(tenantObject.get("version").getAsInt());
        }
        if(tenantObject.has("_links")) {
            JsonObject links = tenantObject.getAsJsonObject("_links");
            if(links.has("self")) {
                JsonObject self = links.getAsJsonObject("self");
                String selfUrl = self.get("href").getAsString();
                search.setId(selfUrl.substring(selfUrl.lastIndexOf("/") + 1));
            }
        }
        if(search.isNotBlank()){
            return search;
        }
        return null;
    }
}
