package nl.hetcak.dms.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by admjzimmermann on 1-9-2016.
 */
public final class ConfigurationManager {
    private static ConfigurationManager instance;
    private Properties configuration;

    public static ConfigurationManager getManager() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }

        return instance;
    }

    private ConfigurationManager() {
        configuration = new Properties();
    }

    public boolean loadFromFile(String aPath) {
        try {
            configuration.load(new FileInputStream(aPath));
            return true;
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return false;
    }

    public String getProperty(String aKey) {
        return configuration.getProperty(aKey);
    }

    public void setProperty(String aKey, String aValue) {
        configuration.setProperty(aKey, aValue);
    }

    public boolean save(String aPath) {
        try {
            configuration.store(new FileOutputStream(aPath), "");
            return true;
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return false;
    }
}
