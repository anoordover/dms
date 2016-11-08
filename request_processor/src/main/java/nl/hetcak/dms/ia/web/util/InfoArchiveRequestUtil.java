package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveRequestUtil {
    public final static String DOCUEMENT_REQUEST_ACTION = "restapi/systemdata/applications";
    public final static String DOCUEMENT_REQUEST_SELECTOR = "ci";
    public final static String DOCUEMENT_REQUEST_PARAMETER = "?cid=";
    public final static String CONTENT_TYPE_XML = "application/xml";
    public final static String CONTENT_TYPE_JSON = "application/hal+json";
    public final static String DEFAULT_CONTENT_TYPE_REQUEST = "application/x-www-form-urlencoded";
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoArchiveRequestUtil.class);
    private final static String CONTENT_TYPE_REQUEST = "Content-Type";
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String LOGGER_STRING_INTERACTION_WITH_SERVER_FAILED = "Failed to interact with server.";

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
     *
     * @param url               Url to use.
     * @param contentType       The body content type.
     * @param requestParameters The request header parameters.
     * @return the server response.
     * @throws ServerConnectionFailureException Server interaction failure.
     */
    public HttpResponse executeGetRequest(String url, String contentType, Map<String, String> requestParameters) throws ServerConnectionFailureException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);

        //add headers
        if (requestParameters != null) {
            for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
                getRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //Add content-type
        if (contentType != null) {
            getRequest.addHeader(CONTENT_TYPE_REQUEST, contentType);
        }

        try {
            return httpClient.execute(getRequest);
        } catch (IOException ioExc) {
            //Server not found, failed to interact with the server.
            LOGGER.error(LOGGER_STRING_INTERACTION_WITH_SERVER_FAILED, ioExc);
            throw new ServerConnectionFailureException(LOGGER_STRING_INTERACTION_WITH_SERVER_FAILED, ioExc);
        }
    }

    /**
     * Execute a POST request.
     *
     * @param url               Url to use.
     * @param contentType       The body content type.
     * @param requestParameters The request header parameters.
     * @param requestBody       The request body.
     * @return the server response.
     * @throws IOException Server interaction failure.
     */
    public HttpResponse executePostRequest(String url, String contentType, Map<String, String> requestParameters, String requestBody) throws RequestResponseException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(url);

        try {
            //Add Body
            if (requestBody != null) {
                StringEntity stringEntity = new StringEntity(requestBody);
                postRequest.setEntity(stringEntity);
            }
        }catch (UnsupportedEncodingException unSubEncExc) {
            throw new RequestResponseException(unSubEncExc, 1202,"Failed to load encoding.");
        }

        //Add Headers
        if (requestParameters != null) {
            for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
                postRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //Add content-type
        if (contentType != null) {
            postRequest.addHeader(CONTENT_TYPE_REQUEST, contentType);
        }

        try {
            return httpClient.execute(postRequest);
        } catch (IOException ioExc) {
            //Server not found, failed to interact with the server.
            LOGGER.error(LOGGER_STRING_INTERACTION_WITH_SERVER_FAILED, ioExc);
            throw new ServerConnectionFailureException(LOGGER_STRING_INTERACTION_WITH_SERVER_FAILED, ioExc);
        }
    }


    /**
     * Creates the url used for connections.
     *
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
     *
     * @param selector The action selector.
     * @param uuid     the uuid of the object.
     * @return a usable url to start a connection.
     */
    public String getServerUrl(String selector, String uuid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getServerUrl(selector));
        stringBuilder.append("/");
        stringBuilder.append(uuid);
        stringBuilder.append("?size=");
        stringBuilder.append(serverConnectionInformation.getMaxItems());
        return stringBuilder.toString();
    }

    /**
     * Creates a Server Content Request Url
     *
     * @param uuid The application uuid.
     * @param cid  The Content ID.
     * @return A usable url.
     */
    public String getServerContentUrl(String uuid, String cid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getServerUrl(DOCUEMENT_REQUEST_ACTION));
        stringBuilder.append("/");
        stringBuilder.append(uuid);
        stringBuilder.append("/");
        stringBuilder.append(DOCUEMENT_REQUEST_SELECTOR);
        stringBuilder.append(DOCUEMENT_REQUEST_PARAMETER);
        stringBuilder.append(cid);
        return stringBuilder.toString();
    }

    /**
     * Create a map with the credentials for InfoArchive.
     *
     * @param credentials the logged in credentials object
     * @return Map with the credentials.
     */
    public Map<String, String> createCredentialsMap(Credentials credentials) {
        Map<String, String> requestValuesMap = new HashMap<>();
        requestValuesMap.put(HEADER_AUTHORIZATION, "Bearer " + credentials.getSecurityToken());
        return requestValuesMap;
    }

}
