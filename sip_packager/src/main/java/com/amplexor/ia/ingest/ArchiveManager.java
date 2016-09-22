package com.amplexor.ia.ingest;

import com.amplexor.ia.configuration.IAServerConfiguration;
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
import java.util.*;

import static com.amplexor.ia.Logger.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ArchiveManager {
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int REFRESH_THRESHOLD_SECONDS = 300;
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

    public boolean ingestSip(String sSipFile) {
        info(this, "Ingesting SIP " + sSipFile);
        boolean bReturn = false;
        try {
            if (mobjCredentials.hasExpired()) {
                debug(this, "Logging in to IAWA");
                login();
            } else if (System.currentTimeMillis() > (mobjCredentials.getExpiry() + (REFRESH_THRESHOLD_SECONDS * MILLISECONDS_PER_SECOND))) { //Refresh if within 5 minutes of expiry
                refresh();
                debug(this, "Refreshing Token");
            }

            info(this, "Sending SIP file" + sSipFile + " to IA");
            IAObject objAip = getAip(sSipFile);
            if (objAip != null && !"".equals(objAip.getUUID())) {
                info(this, "SIP file " + sSipFile + " was received by IA");
                debug(this, "Ingesting AIP with UUID: " + objAip.getUUID());
                IAObject objResult = IAObject.fromJSONObject(restCall(objAip.getLink(IA_ENDPOINT_INGEST), "PUT"));
                if (objResult != null && !"".equals(objResult.getUUID())) {
                    info(this, "Successfully ingested AIP with UUID: " + objAip.getUUID() + ", Result: " + objResult.getName());
                    bReturn = true;
                } else {
                    error(this, String.format("Error ingesting object[Name: %s, UUID: %s]", objAip.getName(), objAip.getUUID()));
                }
            } else {
                error(this, String.format("Error Creating AIP for SIP: %s", sSipFile));
            }
        } catch (IOException | ParseException ex) {
            error(this, ex);
            bReturn = false;
        }
        return bReturn;
    }

    private IAObject getAip(String sSipFile) throws IOException, ParseException {
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

        return null;
    }

    public void login() throws IOException {
        String sUrl = String.format("%s://%s:%d/login?%s", mobjConfiguration.getGatewayProtocol(), mobjConfiguration.getGatewayHost(), mobjConfiguration.getGatewayPort(), mobjCredentials.getLoginQuery());
        HttpURLConnection oConnection = (HttpURLConnection) new URL(sUrl).openConnection();
        oConnection.setRequestMethod("POST");
        oConnection.setRequestProperty("Cache-Control", "no-cache");
        oConnection.setRequestProperty("Content-Type", "x-www-urlencoded");
        oConnection.connect();
        if (oConnection.getResponseCode() == 200) {
            try (BufferedReader oReader = new BufferedReader(new InputStreamReader(oConnection.getInputStream()))) {
                JSONObject oRespObject = (JSONObject) new JSONParser().parse(oReader);
                if (oRespObject != null) {
                    String sAccessToken = (String) oRespObject.get("access_token");
                    mobjCredentials.setToken(sAccessToken);
                    String sRefreshToken = (String) oRespObject.get("refresh_token");
                    mobjCredentials.setRefreshToken(sRefreshToken);
                    long lExpiry = (long) oRespObject.get("expires_in");
                    mobjCredentials.setExpiry(new Date().getTime() + (lExpiry * MILLISECONDS_PER_SECOND));
                }
            } catch (ParseException ex) {
                error(this, ex);
            }
        }
    }

    public void refresh() throws IOException {
        String sUrl = String.format("%s://%s:%d/login?%s", mobjConfiguration.getGatewayProtocol(), mobjConfiguration.getGatewayHost(), mobjConfiguration.getGatewayPort(), mobjCredentials.getRefreshQuery());
        HttpURLConnection oConnection = (HttpURLConnection) new URL(sUrl).openConnection();
        oConnection.setRequestMethod("POST");
        oConnection.setRequestProperty("Cache-Control", "no-cache");
        oConnection.setRequestProperty("Content-Type", "x-www-urlencoded");
        oConnection.connect();
        if (oConnection.getResponseCode() == 200) {
            try (BufferedReader oReader = new BufferedReader(new InputStreamReader(oConnection.getInputStream()))) {
                JSONObject oRespObject = (JSONObject) new JSONParser().parse(oReader);
                if (oRespObject != null) {
                    String sAccessToken = (String) oRespObject.get("access_token");
                    mobjCredentials.setToken(sAccessToken);
                    String sRefreshToken = (String) oRespObject.get("refresh_token");
                    mobjCredentials.setRefreshToken(sRefreshToken);
                    long lExpiry = (long) oRespObject.get("expires_in");
                    mobjCredentials.setExpiry(new Date().getTime() + (lExpiry * MILLISECONDS_PER_SECOND));
                }
            } catch (ParseException ex) {
                error(this, ex);
            }
        } else {
            for (String key : oConnection.getHeaderFields().keySet()) {
                error(this, String.format("%s: %s%n", key, oConnection.getHeaderField(key)));
            }
        }
    }

    private JSONObject restCall(String sTarget) throws IOException, ParseException {
        return restCall(sTarget, "GET");
    }

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

        return objReturn;
    }

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

        if (objReturn.containsKey("_errors")) {
            StringBuilder objErrorBuilder = new StringBuilder();
            JSONArray objErrors = (JSONArray) objReturn.get("_errors");
            objErrors.iterator().forEachRemaining((Object objErrorPart) -> {
                if (objErrorPart instanceof JSONObject && ((JSONObject) objErrorPart).containsKey("localizedMessage")) {
                    objErrorBuilder.append(((JSONObject) objErrorPart).get("localizedMessage"));
                }
            });
            throw new IllegalArgumentException(objErrorBuilder.toString());
        }

        return objReturn;
    }

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

    private static void printHeaders(HttpURLConnection objConnection) {
        for (String sHeaderName : objConnection.getHeaderFields().keySet()) {
            debug(ArchiveManager.class, String.format(HEADER_PRINT_FORMAT, sHeaderName, objConnection.getHeaderField(sHeaderName)));
        }
    }
}
