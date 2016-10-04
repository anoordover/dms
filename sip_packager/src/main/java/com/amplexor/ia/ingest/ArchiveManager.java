package com.amplexor.ia.ingest;

import com.amplexor.ia.configuration.IAServerConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.util.MultipartUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ArchiveManager {
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final String HEADER_PRINT_FORMAT = "%s: %s";
    private static final String IA_OBJECT_LIST_IDENTIFIER = "_embedded";
    private static final String IA_ENDPOINT_TENANTS = "tenants";
    private static final String IA_ENDPOINT_APPLICATIONS = "applications";
    private static final String IA_ENDPOINT_INGEST = "ingest";

    private IAServerConfiguration mobjConfiguration;
    private final IACredentials mobjCredentials;

    public ArchiveManager(IAServerConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
        mobjCredentials = new IACredentials();
        mobjCredentials.setUsername(mobjConfiguration.getIngestUser());
        mobjCredentials.setPassword(mobjConfiguration.getEncryptedIngestPassword());
    }

    /**
     * Executes all the required steps for ingesting a SIP file (Retrieve tenant, get associated application, upload the SIP file and ingest the generated AIP)
     *
     * @param sSipFile The location of the SIP file on the local filesystem
     * @return whether or not ingesting the SIP file was successful.
     */
    public boolean ingestSip(String sSipFile) {
        info(this, "Ingesting SIP " + sSipFile);
        boolean bReturn = false;
        authenticate();


        info(this, "Sending SIP file" + sSipFile + " to IA");
        IAObject objAip = getAip(sSipFile);
        if (objAip != null && !"".equals(objAip.getUUID())) {
            try {
                info(this, "SIP file " + sSipFile + " was received by IA");
                debug(this, "Ingesting AIP with UUID: " + objAip.getUUID());
                IAObject objResult = IAObject.fromJSONObject(restCall(objAip.getLink(IA_ENDPOINT_INGEST), "PUT"));
                if (objResult != null && !"".equals(objResult.getUUID())) {
                    info(this, "Successfully ingested AIP with UUID: " + objAip.getUUID() + ", Result: " + objResult.getName());
                    bReturn = true;
                } else {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INGEST_INGESTION_ERROR, new Exception(String.format("Error ingesting object[Name: %s, UUID: %s]", objAip.getName(), objAip.getUUID())));
                }
            } catch (IllegalArgumentException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INGEST_INGESTION_ERROR, ex);
            } catch (ParseException | IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        } else {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INGEST_ERROR_UPLOADING_CONTENT, new Exception("InfoArchive returned a null AIP object"));
        }
        return bReturn;
    }

    /**
     * Helper method for executing all the prerequisite steps as well as uploading a SIP file to the InfoArchive REST API
     *
     * @param sSipFile The location of the SIP file on the local filesystem
     * @return a {@link IAObject} containing the AIP that was created for the SIP file
     */
    private IAObject getAip(String sSipFile) {
        try {
            IAObject objInfoArchiveApplication = null;
            debug(this, "Fetching tenant " + mobjConfiguration.getIngestTenant());
            IAObject objInfoArchiveTenant = extractObjectWithName(restCall(IA_ENDPOINT_TENANTS), mobjConfiguration.getIngestTenant());
            if (objInfoArchiveTenant.getName() != null) {
                info(this, "Found Tenant with UUID: " + objInfoArchiveTenant.getUUID());
            }
            if (objInfoArchiveTenant.getName() != null) {
                debug(this, "Fetching Application" + mobjConfiguration.getIAApplicationName());
                objInfoArchiveApplication = extractObjectWithName(restCall(objInfoArchiveTenant.getLink(IA_ENDPOINT_APPLICATIONS)), mobjConfiguration.getIAApplicationName());
            }

            if (objInfoArchiveApplication != null && !"".equals(objInfoArchiveApplication.getUUID())) {
                info(this, "Found application with UUID: " + objInfoArchiveApplication.getUUID());
                debug(this, "Uploading AIP");
                return IAObject.fromJSONObject(receive(objInfoArchiveApplication, sSipFile));
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_INGEST_ERROR_UPLOADING_CONTENT, ex);
        } catch (ParseException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return null;
    }

    /**
     * Attempts to authenticate with the InfoArchive Gateway, failing authentication will lead to the application exiting as there will be nowhere to send out SIP packages.
     */
    public boolean authenticate() {
        try {
            String sUrl = String.format("%s://%s:%d/login?%s",
                    mobjConfiguration.getGatewayProtocol(),
                    mobjConfiguration.getGatewayHost(),
                    mobjConfiguration.getGatewayPort(),
                    mobjCredentials.hasExpired() ? mobjCredentials.getLoginQuery() : mobjCredentials.getRefreshQuery());

            HttpURLConnection oConnection = (HttpURLConnection) new URL(sUrl).openConnection();
            oConnection.setRequestMethod("POST");
            oConnection.setRequestProperty("Cache-Control", "no-cache");
            oConnection.setRequestProperty("Content-Type", "x-www-urlencoded");
            oConnection.connect();

            InputStream objInputStream;
            if (oConnection.getResponseCode() == 200) {
                objInputStream = oConnection.getInputStream();
            } else {
                objInputStream = oConnection.getErrorStream();
            }

            return readLoginResponse(objInputStream);
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return false;
    }

    private boolean readLoginResponse(InputStream objInputStream) {
        try (BufferedReader oReader = new BufferedReader(new InputStreamReader(objInputStream))) {
            JSONObject oRespObject = (JSONObject) new JSONParser().parse(oReader);
            if (oRespObject != null) {
                String sAccessToken = (String) oRespObject.get("access_token");
                mobjCredentials.setToken(sAccessToken);
                String sRefreshToken = (String) oRespObject.get("refresh_token");
                mobjCredentials.setRefreshToken(sRefreshToken);
                long lExpiry = (long) oRespObject.get("expires_in");
                mobjCredentials.setExpiry(new Date().getTime() + (lExpiry * MILLISECONDS_PER_SECOND));
                return true;
            }
        } catch (ParseException | IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        return false;
    }


    /**
     * Send a request to the InfoArchive REST API using the HTTP GET method, target can be an endpoint or a fully qualified URL.
     *
     * @param sTarget The target to which the request should be sent (i.e. tenants)
     * @return a {@link JSONObject} containing the results returned by the InfoArchive REST API
     * @throws IOException    if an error occurred during the HTTP request
     * @throws ParseException if there was a problem parsing the {@link JSONObject} returned by the REST API
     */
    private JSONObject restCall(String sTarget) throws IOException, ParseException {
        return restCall(sTarget, "GET");
    }

    /**
     * Send a request to the InfoArchive REST API using the HTTP method provided in sMethod, target can be an endpoint or a fully qualified URL
     *
     * @param sTarget The target to which the request should be sent
     * @param sMethod The HTTP method to be used (GET, PUT, DELETE, POST)
     * @return a {@link JSONObject} containing the results returned by the InfoArchive REST API
     * @throws IOException    if an error occured during the HTTP request
     * @throws ParseException if there was a problem parsing the {@link JSONObject} returned by the REST API
     */
    private JSONObject restCall(String sTarget, String sMethod) throws IOException, ParseException {
        JSONObject objReturn;
        String sUrl;
        if (sTarget.startsWith("http://") || sTarget.startsWith("https://")) {
            sUrl = sTarget;
        } else {
            sUrl = String.format("%s://%s:%d/systemdata/%s", mobjConfiguration.getProtocol(), mobjConfiguration.getHost(), mobjConfiguration.getPort(), sTarget);
        }
        HttpURLConnection objConnection = (HttpURLConnection) new URL(sUrl).openConnection();
        objConnection.setRequestMethod(sMethod);
        objConnection.setRequestProperty("Authorization", "Bearer " + mobjCredentials.getToken());
        objConnection.connect();

        InputStream objInputSource;
        if (objConnection.getResponseCode() == 200) {
            objInputSource = objConnection.getInputStream();
        } else {
            objInputSource = objConnection.getErrorStream();
            printHeaders(objConnection);
        }

        try (BufferedReader objReader = new BufferedReader(new InputStreamReader(objInputSource))) {
            objReturn = (JSONObject) new JSONParser().parse(objReader);
        }

        checkErrors(objReturn);
        return objReturn;
    }

    /**
     * Sends the SIP file located at sFile to the application contained in {@link IAObject} objTarget
     *
     * @param objTarget a {@link JSONObject} describing the InfoArchive Application which should receive the SIP file
     * @param sFile     the location of the SIP file on the local filesystem
     * @return a {@link JSONObject} containing the AIP returned by the InfoArchive REST API
     * @throws IOException    if an error occurred during the HTTP request
     * @throws ParseException if there was a problem parsing the {@link JSONObject} returned by the REST API
     */
    private JSONObject receive(IAObject objTarget, String sFile) throws IOException, ParseException {
        JSONObject objReturn;
        String sUrl = String.format("%s://%s:%d/systemdata/applications/%s/aips", mobjConfiguration.getProtocol(), mobjConfiguration.getHost(), mobjConfiguration.getPort(), objTarget.getUUID());
        HttpURLConnection objConnection = (HttpURLConnection) new URL(sUrl).openConnection();
        objConnection.setUseCaches(false);
        objConnection.setDoOutput(true);
        objConnection.setDoInput(true);
        objConnection.setRequestProperty("Authorization", "Bearer " + mobjCredentials.getToken());

        String sBoundary = Long.toHexString(System.currentTimeMillis());
        MultipartUtility.startMultipart(sBoundary, objConnection);
        MultipartUtility.addFormField(sBoundary, objConnection, "format", "sip_zip");
        MultipartUtility.addFilePart(sBoundary, objConnection, sFile, "sip");
        MultipartUtility.finishMultipart(sBoundary, objConnection);

        InputStream objInputSource;
        if (objConnection.getResponseCode() >= 200 && objConnection.getResponseCode() < 300) {
            objInputSource = objConnection.getInputStream();
        } else {
            objInputSource = objConnection.getErrorStream();
            printHeaders(objConnection);
        }

        try (BufferedReader oReader = new BufferedReader(new InputStreamReader(objInputSource))) {
            objReturn = (JSONObject) new JSONParser().parse(oReader);
        }
        checkErrors(objReturn);
        return objReturn;
    }

    /**
     * Helper Method to check for and extract any errors from a {@link JSONObject} returned by the InfoArchive REST API
     *
     * @param objIAObject A {@link JSONObject} returned by the InfoArchive REST API
     */
    private void checkErrors(JSONObject objIAObject) {
        if (objIAObject.containsKey("_errors")) {
            StringBuilder objErrorBuilder = new StringBuilder();
            JSONArray objErrors = (JSONArray) objIAObject.get("_errors");
            objErrors.iterator().forEachRemaining((Object objErrorPart) -> {
                if (objErrorPart instanceof JSONObject && ((JSONObject) objErrorPart).containsKey("localizedMessage")) {
                    objErrorBuilder.append(((JSONObject) objErrorPart).get("localizedMessage"));
                }
            });
            throw new IllegalArgumentException(objErrorBuilder.toString());
        }
    }

    /**
     * Helper Method for extracting a {@link IAObject} from a {@link JSONObject} returned by the InfoArchive REST API
     *
     * @param objObject The {@link JSONObject} to parse
     * @param sName     The name of the object to extract
     * @return a {@link IAObject} extracted from the supplied {@link JSONObject}
     */
    private IAObject extractObjectWithName(JSONObject objObject, String sName) {
        IAObject objReturn = new IAObject();
        JSONObject objEmbedded = (JSONObject) objObject.get(IA_OBJECT_LIST_IDENTIFIER);
        JSONArray objObjects = null;
        if (objEmbedded != null) {
            objObjects = (JSONArray) objEmbedded.get(objEmbedded.keySet().iterator().next());
        }

        if (objObjects != null) {
            for (JSONObject objObject1 : (Iterable<JSONObject>) objObjects) {
                IAObject oObject = IAObject.fromJSONObject(objObject1);
                if (oObject.getName().equals(sName)) {
                    objReturn = oObject;
                    break;
                }
            }
        }
        return objReturn;
    }

    /**
     * Helper method for printing the headers of a {@link HttpURLConnection}
     *
     * @param objConnection the connection from which to print the headers
     */
    private static void printHeaders(HttpURLConnection objConnection) {
        for (String sHeaderName : objConnection.getHeaderFields().keySet()) {
            debug(ArchiveManager.class, String.format(HEADER_PRINT_FORMAT, sHeaderName, objConnection.getHeaderField(sHeaderName)));
        }
    }
}
