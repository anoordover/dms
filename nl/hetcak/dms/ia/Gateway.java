package nl.hetcak.dms.ia;

import nl.hetcak.dms.configuration.ConfigurationManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * Created by admjzimmermann on 1-9-2016.
 */
public class Gateway {
    public static IACredentials login(String aUsername, String aPassword) {
        IACredentials pReturn = new IACredentials(aUsername);
        try {
            HttpURLConnection pConnection = (HttpURLConnection) new URL(getLoginURL(aUsername, aPassword)).openConnection();
            pConnection.setRequestMethod("POST");
            pConnection.setRequestProperty("Cache-Control", "no-cache");
            pConnection.setRequestProperty("Accept", "application/json");
            pConnection.setRequestProperty("Content-Type", "application/www-form-urlencoded");
            pConnection.connect();
            if(pConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder pResponseBuilder = new StringBuilder();
                try (BufferedReader pReader = new BufferedReader(new InputStreamReader(pConnection.getInputStream()))) {
                    String pRead;
                    while ((pRead = pReader.readLine()) != null) {
                        pResponseBuilder.append(pRead);
                    }
                }
                String pResponse = pResponseBuilder.toString();
                System.out.println(pResponse);
                JSONObject pAuthObject = (JSONObject) new JSONParser().parse(pResponse);
                pReturn.setToken((String) pAuthObject.get("access_token"));
                pReturn.refresh((long) pAuthObject.get("expires_in"));
            }
            else {
                System.err.println("Gateway response code: " + pConnection.getResponseCode());
            }
        } catch (IOException | ParseException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return pReturn;
    }

    private static String getLoginURL(String aUsername, String aPassword) {
        StringBuilder pURLBuilder = new StringBuilder();
        pURLBuilder.append(ConfigurationManager.getManager().getProperty("IA_GATEWAY_PROTOCOL"));
        pURLBuilder.append("://");
        pURLBuilder.append(ConfigurationManager.getManager().getProperty("IA_GATEWAY_HOST"));
        pURLBuilder.append(":");
        pURLBuilder.append(ConfigurationManager.getManager().getProperty("IA_GATEWAY_PORT"));
        pURLBuilder.append("/login");
        pURLBuilder.append("?");
        try {
            pURLBuilder.append(URLEncoder.encode("username", "UTF-8"));
            pURLBuilder.append("=");
            pURLBuilder.append(URLEncoder.encode(ConfigurationManager.getManager().getProperty("INGEST_USERNAME"), "UTF-8"));
            pURLBuilder.append("&");
            pURLBuilder.append(URLEncoder.encode("password", "UTF-8"));
            pURLBuilder.append("=");
            pURLBuilder.append(URLEncoder.encode(new String(Base64.getDecoder().decode(ConfigurationManager.getManager().getProperty("INGEST_PASSWORD"))), "UTF-8"));
            pURLBuilder.append("&");
            pURLBuilder.append(URLEncoder.encode("grant_type", "UTF-8"));
            pURLBuilder.append("=");
            pURLBuilder.append(URLEncoder.encode("password", "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return pURLBuilder.toString();
    }
}
