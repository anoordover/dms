package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.ConnectionConfigurationManager;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
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
    private final static String DEFAULT_CONTENT_TYPE_REQUEST = "application/x-www-form-urlencoded";
    
    private String executeRequest(String url, String contentType, Map<String,String> requestParameters, String requestBody, Credentials credentials) throws IOException{
        HttpResponse response = null;
        HttpClient httpClient = HttpClientBuilder.create().build();
        
        if(url != null) {
            HttpGet getRequest = new HttpGet(url);
    
            if(requestParameters != null) {
                for(String key : requestParameters.keySet()) {
                    getRequest.addHeader(key,requestParameters.get(key));
                }
            }
            response = httpClient.execute(getRequest);
        }
        
        if(response != null) {
            HttpEntity entity = response.getEntity();
            return IOUtils.toString(entity.getContent(), org.apache.commons.lang3.CharEncoding.UTF_8);
        }
        
        return "<data>error</data>";
    }
    
    
}
