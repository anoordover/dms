package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveRequestUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoArchiveRequestUtil.class);
    public final static String CONTENT_TYPE_XML = "application/xml";
    public final static String CONTENT_TYPE_JSON = "application/hal+json";
    private final static String CONTENT_TYPE_REQUEST = "Content-Type";
    private final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String DEFAULT_CONTENT_TYPE_REQUEST = "application/x-www-form-urlencoded";
    
    private ServerConnectionInformation serverConnectionInformation;
    
    public InfoArchiveRequestUtil(ServerConnectionInformation serverConnectionInformation) {
        this.serverConnectionInformation = serverConnectionInformation;
    }
    
    public String responseReader(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return IOUtils.toString(entity.getContent(), org.apache.commons.lang3.CharEncoding.UTF_8);
    }
    
    /**
     * Execute a GET request.
     * @param url Url to use.
     * @param contentType The body content type.
     * @param requestParameters The request header parameters.
     * @return the server response.
     * @throws ServerConnectionFailureException Server interaction failure.
     */
    public HttpResponse executeGetRequest(String url, String contentType, Map<String,String> requestParameters) throws ServerConnectionFailureException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
    
        //add headers
        if(requestParameters != null) {
            for(String key : requestParameters.keySet()) {
                getRequest.addHeader(key,requestParameters.get(key));
            }
        }
    
        //Add content-type
        if(contentType != null) {
            getRequest.addHeader(CONTENT_TYPE_REQUEST, contentType);
        }
        
        try {
            return httpClient.execute(getRequest);
        } catch (IOException ioExc) {
            //Server not found, failed to interact with the server.
            LOGGER.error("Failed to interact with server.", ioExc);
            throw new ServerConnectionFailureException("Failed to interact with server.", ioExc);
        }
    }
    
    /**
     * Execute a POST request.
     * @param url Url to use.
     * @param contentType The body content type.
     * @param requestParameters The request header parameters.
     * @param requestBody The request body.
     * @return the server response.
     * @throws IOException Server interaction failure.
     */
    public HttpResponse executePostRequest(String url, String contentType, Map<String,String> requestParameters, String requestBody) throws IOException, ServerConnectionFailureException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(url);
        
        //Add Body
        if(requestBody != null) {
            StringEntity stringEntity = new StringEntity(requestBody);
            postRequest.setEntity(stringEntity);
        }
    
        //Add Headers
        if(requestParameters != null) {
            for(String key : requestParameters.keySet()) {
                postRequest.addHeader(key,requestParameters.get(key));
            }
        }
        
        //Add content-type
        if(contentType != null) {
            postRequest.addHeader(CONTENT_TYPE_REQUEST, contentType);
        }
        
        try {
            return httpClient.execute(postRequest);
        } catch (IOException ioExc) {
            //Server not found, failed to interact with the server.
            LOGGER.error("Failed to interact with server.", ioExc);
            throw new ServerConnectionFailureException("Failed to interact with server.", ioExc);
        }
    }
    
    
    /**
     * Creates the url used for connections.
     * @param selector The action selector
     * @return a usable url to start a connection.
     */
    public String getServerUrl(String selector) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://");
        stringBuilder.append(serverConnectionInformation.getServerAddress());
        stringBuilder.append(":");
        stringBuilder.append(serverConnectionInformation.getServerPort());
        stringBuilder.append("/");
        stringBuilder.append(selector);
        return stringBuilder.toString();
    }
    
    /**
     * Creates the url used for connections.
     * @param selector The action selector.
     * @param uuid the uuid of the object.
     * @return a usable url to start a connection.
     */
    public String getServerUrl(String selector, String uuid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getServerUrl(selector));
        stringBuilder.append("/");
        stringBuilder.append(uuid);
        return stringBuilder.toString();
    }
    
    /**
     * Create a map with the credentials for InfoArchive.
     * @param credentials the logged in credentials object
     * @return Map with the credentials.
     */
    public Map<String, String> createCredentialsMap(Credentials credentials){
        Map<String, String> requestValuesMap = new HashMap<>();
        requestValuesMap.put(HEADER_AUTHORIZATION, "Bearer "+credentials.getSecurityToken());
        return requestValuesMap;
    }
    
}
