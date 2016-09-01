package nl.hetcak.dms; /**
 * Created by admjzimmermann on 1-9-2016.
 */

import nl.hetcak.dms.configuration.ConfigurationManager;
import nl.hetcak.dms.configuration.DefaultConfiguration;
import nl.hetcak.dms.ia.Gateway;
import nl.hetcak.dms.ia.IACredentials;
import nl.hetcak.dms.ia.RESTApi;

import java.io.File;
import java.util.Base64;

public class Program {

    public static void main(String[] args) {
        String pConfigPath = System.getProperty("user.dir") + "/config.properties".replace('/', File.separatorChar);
        ConfigurationManager pConfig = ConfigurationManager.getManager();
        if(!pConfig.loadFromFile(pConfigPath)) {
            DefaultConfiguration.setDefaultConfiguration(pConfig);
            pConfig.save(pConfigPath);
        }
        IACredentials creds = Gateway.login("admjzimmermann", "Wurcitwag40-");
        RESTApi.getTenant("INFOARCHIVE", creds);
    }
}
