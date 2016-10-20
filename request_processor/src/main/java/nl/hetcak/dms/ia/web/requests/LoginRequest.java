package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.LoginFailureException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil.DEFAULT_CONTENT_TYPE_REQUEST;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class LoginRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequest.class);
    private static final String LOGIN_USERNAME = "username";
    private static final String LOGIN_PASSWORD = "password";
    private static final String LOGIN_GRANT = "grant_type";
    private static final String LOGIN_GRANT_PASSWORD = "password";
    private static final String LOGIN_GRANT_REFRESH = "refresh_token";
    private static final String SELECTOR_LOGIN = "login";
    
    private Configuration configuration;
    private InfoArchiveRequestUtil infoArchiveRequestUtil;
    
    public LoginRequest(Configuration configuration) {
        this.configuration = configuration;
        this.infoArchiveRequestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
    }
    
    public Credentials loginInfoArchive() throws LoginFailureException, ServerConnectionFailureException {
        String serverUrl = infoArchiveRequestUtil.getServerUrl(SELECTOR_LOGIN);
        try {
            String bodyContent = prepareLoginBody(configuration.getInfoArchiveCredentials());
            HttpResponse httpResponse = infoArchiveRequestUtil.executePostRequest(serverUrl, DEFAULT_CONTENT_TYPE_REQUEST, null, bodyContent);
            String response =  infoArchiveRequestUtil.responseReader(httpResponse);
            return updateCredentails(configuration.getInfoArchiveCredentials(), response);
        } catch (UnsupportedEncodingException unsupEncoExc) {
            LOGGER.error("Could not load UTF-8 encoding.", unsupEncoExc);
            throw new LoginFailureException("Encoding UTF-8 is unsupported.", unsupEncoExc);
        } catch (IOException ioExc) {
            LOGGER.error("Parse error.", ioExc);
            throw new LoginFailureException(ioExc);
        }
    }
    
    public Credentials refreshCredentialsInfoArchive(Credentials loggedInCredentials) throws LoginFailureException, ServerConnectionFailureException {
        String serverUrl = infoArchiveRequestUtil.getServerUrl(SELECTOR_LOGIN);
        try {
            String bodyContent = prepareRefreshLoginBody(loggedInCredentials);
            HttpResponse httpResponse = infoArchiveRequestUtil.executePostRequest(serverUrl, DEFAULT_CONTENT_TYPE_REQUEST, null, bodyContent);
            String response =  infoArchiveRequestUtil.responseReader(httpResponse);
            return updateCredentails(configuration.getInfoArchiveCredentials(), response);
        } catch (UnsupportedEncodingException unsupEncoExc) {
            LOGGER.error("Could not load UTF-8 encoding.", unsupEncoExc);
            throw new LoginFailureException("Encoding UTF-8 is unsupported.", unsupEncoExc);
        } catch (IOException ioExc) {
            LOGGER.error("Parse error.", ioExc);
            throw new LoginFailureException(ioExc);
        }
    }
    
    private Credentials updateCredentails(Credentials credentials, String serverResponse) throws LoginFailureException {
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(serverResponse).getAsJsonObject();
        
        if(response.has("expires_in")) {
            int expireSeconds = response.get("expires_in").getAsInt();
            GregorianCalendar expire = (GregorianCalendar)GregorianCalendar.getInstance();
            expire.add(Calendar.SECOND,expireSeconds);
            credentials.setSecurityTokenInvalidationTime(expire);
        } else {
            throw new LoginFailureException("Failed to find expires time in response.");
        }
        
        if(response.has("access_token")) {
            credentials.setSecurityToken(response.get("access_token").getAsString());
        }  else {
            throw new LoginFailureException("Failed to find token in response.");
        }
    
        if(response.has("refresh_token")) {
            credentials.setRecoveryToken(response.get("refresh_token").getAsString());
        }  else {
            throw new LoginFailureException("Failed to find token in response.");
        }
        return credentials;
    }
    
    
    private String prepareLoginBody(Credentials credentials) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URLEncoder.encode(LOGIN_USERNAME, "UTF-8"));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(credentials.getUsername(), "UTF-8"));
        stringBuilder.append('&');
        stringBuilder.append(URLEncoder.encode(LOGIN_PASSWORD, "UTF-8"));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(credentials.getPassword(), "UTF-8"));
        stringBuilder.append('&');
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT, "UTF-8"));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT_PASSWORD, "UTF-8"));
        
        return stringBuilder.toString();
    }
    
    private String prepareRefreshLoginBody(Credentials credentials) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT_REFRESH, "UTF-8"));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(credentials.getRecoveryToken(), "UTF-8"));
        stringBuilder.append('&');
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT, "UTF-8"));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT_REFRESH, "UTF-8"));
        
        return stringBuilder.toString();
    }
}
