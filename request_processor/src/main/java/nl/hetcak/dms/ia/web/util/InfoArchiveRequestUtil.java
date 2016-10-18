package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.configuration.Configuration;
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
    public final static String DEFAULT_CONTENT_TYPE_REQUEST = "application/x-www-form-urlencoded";
    
    private Configuration configuration;
    
    public InfoArchiveRequestUtil(Configuration configuration) {
        this.configuration = configuration;
    }
    
    public String responseReader(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return IOUtils.toString(entity.getContent(), org.apache.commons.lang3.CharEncoding.UTF_8);
    }
    
    public HttpResponse executeGetRequest(String url, String contentType, Map<String,String> requestParameters) throws IOException {
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
        
        return httpClient.execute(getRequest);
    }
    
    public HttpResponse executePostRequest(String url, String contentType, Map<String,String> requestParameters, String requestBody) throws IOException {
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
        
        return httpClient.execute(postRequest);
    }

    public String getServerUrl(String selector) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://");
        stringBuilder.append(configuration.getInfoArchiveServerAddress());
        stringBuilder.append(":");
        stringBuilder.append(configuration.getInfoArchiveServerPort());
        stringBuilder.append("/");
        stringBuilder.append(selector);
        return stringBuilder.toString();
    }
    
    
}
