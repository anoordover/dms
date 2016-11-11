package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.exceptions.InfoArchiveResponseException;
import nl.hetcak.dms.ia.web.exceptions.LoginFailureException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import org.junit.Test;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ErrorResponseTest {
    private static final String ERROR_TIMEOUT_IA_RESPONSE = "{ \"_errors\" : [{\"error\" : \"SEARCH_TIMEOUT\", \"message\" : \"This is a test message for a search time-out.\"}]}";
    private static final String ERROR_UNKNOWN_IA_RESPONSE = "{ \"_errors\" : [{\"error\" : \"TOTAL_SERVER_FAILURE\", \"message\" : \"This is a test message for a unknown error.\"}]}";
    private static final String ERROR_LOGIN_IA_RESPONSE = "{ \"error\": \"invalid_grant\", \"error_description\": \"Bad credentials\"}";
    private static final String ERROR_SERVER_ERROR_IA_RESPONSE = "{ \"error\": \"server_error\", \"error_description\": \"Back-end not responding\"}";
    private static final String ERROR_TOKEN_IA_RESPONSE = "{ \"error\": \"invalid_token\", \"error_description\": \"Token expired.\"}";
    private static final String ERROR_UNKNOWN_ERROR_IA_RESPONSE = "{ \"error\": \"ia_error\", \"error_description\": \"Unknown error.\"}";
    
    @Test(expected = InfoArchiveResponseException.class)
    public void documentListTimeOutError() throws Exception {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        RecordRequest rr = new RecordRequest(connectionManager.getConfiguration(),
            connectionManager.getActiveCredentials());
        rr.parseDocumentList(ERROR_TIMEOUT_IA_RESPONSE, true, false);
    }
    
    @Test(expected = InfoArchiveResponseException.class)
    public void documentListUnknownError() throws Exception {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        RecordRequest rr = new RecordRequest(connectionManager.getConfiguration(),
            connectionManager.getActiveCredentials());
        rr.parseDocumentList(ERROR_UNKNOWN_IA_RESPONSE, true, false);
    }
    
    @Test(expected = LoginFailureException.class)
    public void invalidCredentialsError() throws Exception {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        LoginRequest loginRequest = new LoginRequest(connectionManager.getConfiguration());
        JsonParser parser = new JsonParser();
        JsonObject error = parser.parse(ERROR_LOGIN_IA_RESPONSE).getAsJsonObject();
        throw loginRequest.readErrorResponse(error);
    }
    
    @Test(expected = ServerConnectionFailureException.class)
    public void serverErrorDuringLogin() throws Exception {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        LoginRequest loginRequest = new LoginRequest(connectionManager.getConfiguration());
        JsonParser parser = new JsonParser();
        JsonObject error = parser.parse(ERROR_SERVER_ERROR_IA_RESPONSE).getAsJsonObject();
        throw loginRequest.readErrorResponse(error);
    }
    
    @Test(expected = LoginFailureException.class)
    public void loginErrorDuringLogin() throws Exception {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        LoginRequest loginRequest = new LoginRequest(connectionManager.getConfiguration());
        JsonParser parser = new JsonParser();
        JsonObject error = parser.parse(ERROR_TOKEN_IA_RESPONSE).getAsJsonObject();
        throw loginRequest.readErrorResponse(error);
    }
    
    @Test(expected = RequestResponseException.class)
    public void unknownErrorDuringLogin() throws Exception {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        LoginRequest loginRequest = new LoginRequest(connectionManager.getConfiguration());
        JsonParser parser = new JsonParser();
        JsonObject error = parser.parse(ERROR_UNKNOWN_ERROR_IA_RESPONSE).getAsJsonObject();
        throw loginRequest.readErrorResponse(error);
    }
}
