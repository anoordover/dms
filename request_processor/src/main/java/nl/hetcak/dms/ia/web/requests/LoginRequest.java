package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.LoginFailureException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.util.CryptoUtil;
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
    private static final String ENCODING_UTF8 = "UTF-8";

    private Configuration configuration;
    private InfoArchiveRequestUtil infoArchiveRequestUtil;

    public LoginRequest(Configuration configuration) {
        this.configuration = configuration;
        this.infoArchiveRequestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
    }

    /**
     * Try to login the InfoArchive server.
     *
     * @return active credentials.
     * @throws LoginFailureException            Failed to login.
     * @throws ServerConnectionFailureException Failed to connect.
     */
    public Credentials loginInfoArchive() throws RequestResponseException {
        LOGGER.info("Logging in to InfoArchive.");
        String serverUrl = infoArchiveRequestUtil.getServerUrl(SELECTOR_LOGIN);
        try {
            String bodyContent = prepareLoginBody(configuration.getInfoArchiveCredentials());
            LOGGER.debug("Logging in with credentials...");
            HttpResponse httpResponse = infoArchiveRequestUtil.executePostRequest(serverUrl, DEFAULT_CONTENT_TYPE_REQUEST, null, bodyContent);
            String response = infoArchiveRequestUtil.responseReader(httpResponse);
            LOGGER.debug(response);
            return updateCredentials(configuration.getInfoArchiveCredentials(), response);
        } catch (UnsupportedEncodingException unsupEncoExc) {
            LOGGER.error("Could not load UTF-8 encoding.", unsupEncoExc);
            throw new LoginFailureException("Encoding UTF-8 is unsupported.", unsupEncoExc);
        } catch (IOException ioExc) {
            LOGGER.error("Parse error.", ioExc);
            throw new LoginFailureException(ioExc);
        }
    }

    public Credentials refreshCredentialsInfoArchive(Credentials loggedInCredentials) throws RequestResponseException {
        LOGGER.info("Refreshing InfoArchive login token.");
        String serverUrl = infoArchiveRequestUtil.getServerUrl(SELECTOR_LOGIN);
        try {
            String bodyContent = prepareRefreshLoginBody(loggedInCredentials);
            LOGGER.debug(bodyContent);
            HttpResponse httpResponse = infoArchiveRequestUtil.executePostRequest(serverUrl, DEFAULT_CONTENT_TYPE_REQUEST, null, bodyContent);
            String response = infoArchiveRequestUtil.responseReader(httpResponse);
            LOGGER.debug(response);
            LOGGER.info("Returning credentials.");
            return updateCredentials(configuration.getInfoArchiveCredentials(), response);
        } catch (UnsupportedEncodingException unsupEncoExc) {
            LOGGER.error("Could not load UTF-8 encoding.", unsupEncoExc);
            throw new LoginFailureException("Encoding UTF-8 is unsupported.", unsupEncoExc);
        } catch (IOException ioExc) {
            LOGGER.error("Parse error.", ioExc);
            throw new LoginFailureException(ioExc);
        }
    }

    private Credentials updateCredentials(Credentials credentials, String serverResponse) throws RequestResponseException {
        LOGGER.info("Updating InfoArchive Credentials.");
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(serverResponse).getAsJsonObject();

        if(response.has("error")){
            throw readErrorResponse(response);
        }

        if (response.has("expires_in") && response.has("access_token") && response.has(LOGIN_GRANT_REFRESH)) {
            int expireSeconds = response.get("expires_in").getAsInt();
            GregorianCalendar expire = (GregorianCalendar) GregorianCalendar.getInstance();
            expire.add(Calendar.SECOND, expireSeconds);
            LOGGER.info("InfoArchive Credentials will expire:" + expire.getTime().toString());
            credentials.setSecurityTokenInvalidationTime(expire);
            LOGGER.info("Updating Tokens");
            credentials.setSecurityToken(response.get("access_token").getAsString());
            credentials.setRecoveryToken(response.get(LOGIN_GRANT_REFRESH).getAsString());
        } else {
            throw new LoginFailureException("Failed to find expires time or tokens in response.");
        }
        LOGGER.info("Returning updated credentials.");
        return credentials;
    }

    private RequestResponseException readErrorResponse(JsonObject response) {
        String errorTitle = response.get("error").getAsString();
        String errorDescription = response.get("error_description").getAsString();
        if(errorTitle.contentEquals("server_error")) {
            StringBuilder message = new StringBuilder("Server responded with a error: ");
            message.append(errorDescription);
            ServerConnectionFailureException scfExc = new ServerConnectionFailureException(message.toString());
            return scfExc;
        } else if(errorTitle.contentEquals("invalid_token")) {
            StringBuilder message = new StringBuilder("Token error: ");
            message.append(errorTitle);
            message.append(": ");
            message.append(errorDescription);
            return new LoginFailureException(message.toString());
        } else {
            StringBuilder message = new StringBuilder("Unexpected error: ");
            message.append(errorTitle);
            message.append(": ");
            message.append(errorDescription);
            RequestResponseException rrExc = new RequestResponseException(-1,message.toString());
            return rrExc;
        }
    }

    private String prepareLoginBody(Credentials credentials) throws RequestResponseException {
        LOGGER.info("Prepare login body.");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(URLEncoder.encode(LOGIN_USERNAME, ENCODING_UTF8));
            stringBuilder.append('=');
            stringBuilder.append(URLEncoder.encode(credentials.getUsername(), ENCODING_UTF8));
            stringBuilder.append('&');
            stringBuilder.append(URLEncoder.encode(LOGIN_PASSWORD, ENCODING_UTF8));
            stringBuilder.append('=');
            String password = CryptoUtil.decryptValue(credentials.getPassword(), configuration);
            stringBuilder.append(URLEncoder.encode(password, ENCODING_UTF8));
            stringBuilder.append('&');
            stringBuilder.append(URLEncoder.encode(LOGIN_GRANT, ENCODING_UTF8));
            stringBuilder.append('=');
            stringBuilder.append(URLEncoder.encode(LOGIN_GRANT_PASSWORD, ENCODING_UTF8));
        } catch (UnsupportedEncodingException unsEncExc) {
            throw new RequestResponseException(unsEncExc, -1, "UTF-8 is not supported.");
        }
        LOGGER.info("Returning login body.");
        return stringBuilder.toString();
    }

    private String prepareRefreshLoginBody(Credentials credentials) throws UnsupportedEncodingException {
        LOGGER.info("Prepare login refresh body.");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT_REFRESH, ENCODING_UTF8));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(credentials.getRecoveryToken(), ENCODING_UTF8));
        stringBuilder.append('&');
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT, ENCODING_UTF8));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(LOGIN_GRANT_REFRESH, ENCODING_UTF8));

        LOGGER.info("Returning login refresh body.");
        return stringBuilder.toString();
    }
}
