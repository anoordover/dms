package nl.hetcak.dms.configuration;

import java.util.Base64;

/**
 * Created by admjzimmermann on 1-9-2016.
 */
public class DefaultConfiguration {
    public static void setDefaultConfiguration(ConfigurationManager aManager) {
        //Assume first time run, Set default config
        //IA Ingest Server Properties
        aManager.setProperty("IA_AUTH_TYPE", "GATEWAY");
        aManager.setProperty("IA_GATEWAY_PROTOCOL", "http");
        aManager.setProperty("IA_GATEWAY_HOST", "localhost");
        aManager.setProperty("IA_GATEWAY_PORT", "8080");
        aManager.setProperty("IA_SERVER_PROTOCOL", "http");
        aManager.setProperty("IA_SERVER_HOST", "localhost");
        aManager.setProperty("IA_SERVER_PORT", "8765");
        aManager.setProperty("INGEST_USERNAME", "DMS_Ingest");
        aManager.setProperty("INGEST_PASSWORD", Base64.getEncoder().encodeToString("IngestAdmin".getBytes()));
        //ESB / Queue Properties
        aManager.setProperty("DOC_PROVIDER", "ActiveMQ");
        aManager.setProperty("DOC_PROVIDER_PROTOCOL", "tcp");
        aManager.setProperty("DOC_PROVIDER_HOST", "localhost");
        aManager.setProperty("DOC_PROVIDER_PORT", "61616");
    }
}
