package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.infoarchive.search.Search;
import nl.hetcak.dms.ia.web.infoarchive.searchComposition.SearchComposition;
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
public class SearchCompositionRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCompositionRequest.class);
    private Configuration configuration;
    private Credentials credentials;
    
    public SearchCompositionRequest(Configuration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
    }
    
    public List<SearchComposition> requestSearch(Search search) throws RequestResponseException {
        LOGGER.debug("Requesting search-compositions.");
        String ia_response = executeRequest(search, null);
        LOGGER.debug("Returning list search-compositions.");
        return parseResult(ia_response);
    }
    
    public List<SearchComposition> requestSearchWithName(Search search, String name) throws RequestResponseException {
        LOGGER.debug("Requesting search-compositions.");
        String ia_response = executeRequest(search, name);
        LOGGER.debug("Returning list search-compositions.");
        return parseResult(ia_response);
    }
    
    private String executeRequest(Search search, String name) throws RequestResponseException {
        InfoArchiveRequestUtil requestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        StringBuilder urlBuilder = new StringBuilder("restapi/systemdata/searches/");
        urlBuilder.append(search.getId());
        urlBuilder.append("/search-compositions");
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
        LOGGER.debug("Executing search-compositions Request. "+ url);
        HttpResponse response = requestUtil.executeGetRequest(url, null, requestHeader);
        try {
            return requestUtil.responseReader(response);
        }catch (IOException exc) {
            throw new RequestResponseException(exc, 9999, "Error reading response from InfoArchive.");
        }
    }
    
    private List<SearchComposition> parseResult(String response) {
        List<SearchComposition> searchCompositionList = new ArrayList<SearchComposition>();
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        if(jsonResponse.has("_embedded")) {
            JsonObject embedded = jsonResponse.getAsJsonObject("_embedded");
            JsonArray applications = embedded.getAsJsonArray("searchCompositions");
            for (int i = 0; i < applications.size(); i++) {
                JsonObject searchObject = applications.get(i).getAsJsonObject();
                SearchComposition searchComposition = parseSearchComposition(searchObject);
                if(searchComposition != null) {
                    searchCompositionList.add(searchComposition);
                }
            }
        }
        return searchCompositionList;
    }
    
    private SearchComposition parseSearchComposition(JsonObject tenantObject) {
        SearchComposition searchComposition = new SearchComposition();
        if(tenantObject.has("name")) {
            searchComposition.setName(tenantObject.get("name").getAsString());
        }
        if(tenantObject.has("version")) {
            searchComposition.setVersion(tenantObject.get("version").getAsInt());
        }
        if(tenantObject.has("searchName")) {
            searchComposition.setSearchName(tenantObject.get("searchName").getAsString());
        }
        if(tenantObject.has("_links")) {
            JsonObject links = tenantObject.getAsJsonObject("_links");
            if(links.has("self")) {
                JsonObject self = links.getAsJsonObject("self");
                String selfUrl = self.get("href").getAsString();
                searchComposition.setId(selfUrl.substring(selfUrl.lastIndexOf("/") + 1));
            }
        }
        if(searchComposition.isNotBlank()){
            return searchComposition;
        }
        return null;
    }
}
