package nl.hetcak.dms.ia;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import nl.hetcak.dms.configuration.ConfigurationManager;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by admjzimmermann on 1-9-2016.
 */
public class RESTApi {
    public static JSONObject getTenant(String aTenant, IACredentials aCredentials) {
        JSONObject pReturn = null;
        String pSpEL = String.format("[name=='" + aTenant + "']");
        try {
            HttpURLConnection pConnection = (HttpURLConnection) new URL(String.format("%s://%s:%s/systemdata/tenants?spel=?%s",
                    ConfigurationManager.getManager().getProperty("IA_SERVER_PROTOCOL"),
                    ConfigurationManager.getManager().getProperty("IA_SERVER_HOST"),
                    ConfigurationManager.getManager().getProperty("IA_SERVER_PORT"),
                    URLEncoder.encode(pSpEL, "UTF-8"))).openConnection();

            pConnection.setRequestMethod("GET");
            pConnection.setRequestProperty("Authorization", "Bearer " + aCredentials.getToken());
            pConnection.connect();
            try(BufferedReader pReader = new BufferedReader(new InputStreamReader(pConnection.getInputStream()))) {
                String pRead;
                while((pRead = pReader.readLine()) != null) {
                    System.out.println(pRead);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }

        return pReturn;
    }
}
