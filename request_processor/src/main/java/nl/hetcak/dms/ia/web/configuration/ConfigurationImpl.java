package nl.hetcak.dms.ia.web.configuration;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.comunication.InfoArchiveCredentials;
import nl.hetcak.dms.ia.web.comunication.InfoArchiveServerConnectionInformation;
import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "config-request-processor")
public class ConfigurationImpl implements Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationImpl.class);
    private final static String CONF_SECURITY_TOKEN = "ia_token";
    private final static String CONF_SERVER_ADDRESS = "ia_server";
    private final static String CONF_SERVER_PORT = "ia_port";
    private final static String CONF_SERVER_HTTPS = "ia_server_https";
    private final static String CONF_SERVICE_USER = "ia_service_usr";
    private final static String CONF_SERVICE_USER_PASSWORD = "ia_service_usr_password";
    private final static String CONF_LOG_PROPS = "log4j_properties";
    private final static String CONF_MAX_FILESIZE = "max_allowed_filesize";
    private final static String CONF_MAX_RESULTS = "max_allowed_results";
    private final static String CONF_APPLICATION_UUID = "ia_application_uuid";
    private final static String CONF_SEARCHCOMPOSITION_UUID = "ia_searchcomposition_uuid";

    private String msSecurityToken, msServerAddress, msUsername, msPassword, msLogProps, msApplicationUUID, msSearchComponentUUID;
    private int miServerPort, miMaxResults, miMaxFileSize;
    private byte[] mbaDecryptionKey;
    private boolean mbUseHttps = false;


    @XmlElement(name = CONF_SECURITY_TOKEN)
    public String getSecurityToken() {
        return msSecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.msSecurityToken = securityToken;
    }

    @Override
    public String getInfoArchiveServerAddress() {
        return msServerAddress;
    }

    @Override
    public int getInfoArchiveServerPort() {
        return miServerPort;
    }

    @Override
    public Credentials getInfoArchiveCredentials() {
        Credentials credentials = new InfoArchiveCredentials();
        credentials.setUsername(msUsername);
        credentials.setPassword(msPassword);

        if (StringUtils.isNotBlank(msSecurityToken)) {
            credentials.setSecurityToken(msSecurityToken);
            credentials.setUseLoginToken(true);
        }
        return credentials;
    }

    @Override
    public ServerConnectionInformation getInfoArchiveServerInformation() {
        InfoArchiveServerConnectionInformation iaconnection = new InfoArchiveServerConnectionInformation();
        iaconnection.setServerAddress(msServerAddress);
        iaconnection.setServerPort(miServerPort);
        iaconnection.setMaxItems(miMaxResults);
        iaconnection.setHttps(mbUseHttps);
        return iaconnection;
    }

    @XmlElement(name = CONF_MAX_RESULTS)
    @Override
    public int getMaxResults() {
        return miMaxResults;
    }

    /**
     * Get the file size limit.
     *
     * @return the maximum file size.
     */
    @XmlElement(name = CONF_MAX_FILESIZE)
    @Override
    public int getMaxFileSize() {
        return miMaxFileSize;
    }

    public void setMaxResults(int maxResults) {
        this.miMaxResults = maxResults;
    }

    public void setMaxFileSize(int maxFileSize) { this.miMaxFileSize = maxFileSize; }

    @XmlElement(name = CONF_APPLICATION_UUID)
    @Override
    public String getApplicationUUID() {
        return msApplicationUUID;
    }

    public void setApplicationUUID(String applicationUUID) {
        this.msApplicationUUID = applicationUUID;
    }

    @Override
    public String getSearchCompositionUUID() {
        return msSearchComponentUUID;
    }

    @Override
    public boolean hasBasicInformation() {
        boolean result = checkCredentials();
        if (result)
            result = checkServerInformation();
        return result;
    }

    private boolean checkCredentials() {
        boolean result = true;
        Credentials credentials = getInfoArchiveCredentials();
        try {
            if (credentials.useTokenOnlyConfiguration()) {
                if (credentials.getSecurityToken().length() == 0)
                    LOGGER.warn("Security Token not set in Token only mode.");
                result = false;
            } else if (credentials.getUsername().length() == 0 || credentials.getPassword() == null) {
                LOGGER.warn("User credentials have no value.");
                result = false;
            }
        } catch (NullPointerException nullExc) {
            LOGGER.error("Configuration Error in the user credentials.", nullExc);
            result = false;
        }
        return result;
    }

    private boolean checkServerInformation() {
        boolean result = true;
        ServerConnectionInformation serverConnectionInformation = getInfoArchiveServerInformation();
        if (serverConnectionInformation == null) {
            return false;
        } else {
            try {
                if (serverConnectionInformation.getServerAddress().length() == 0 || serverConnectionInformation.getServerPort() == 0) {
                    LOGGER.warn("Server information have no value.");
                    result = false;
                }
                if (msApplicationUUID.length() == 0 || msSearchComponentUUID.length() == 0) {
                    LOGGER.warn("Server uuid information have no value.");
                    result = false;
                }
            } catch (NullPointerException nullExc) {
                LOGGER.error("Configuration Error in the server connection settings.", nullExc);
                result = false;
            }
        }
        return result;
    }

    @XmlElement(name = CONF_LOG_PROPS)
    public String getLogProps() {
        return msLogProps;
    }

    public void setLogProps(String logProps) {
        this.msLogProps = logProps;
    }

    @XmlElement(name = CONF_SERVER_ADDRESS)
    public String getServerAddress() {
        return msServerAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.msServerAddress = serverAddress;
    }

    @XmlElement(name = CONF_SERVICE_USER)
    public String getUsername() {
        return msUsername;
    }

    public void setUsername(String username) {
        this.msUsername = username;
    }

    @XmlElement(name = CONF_SERVICE_USER_PASSWORD)
    public String getPassword() {
        return msPassword;
    }

    public void setPassword(String password) {
        this.msPassword = password;
    }

    @XmlElement(name = CONF_SEARCHCOMPOSITION_UUID)
    public String getSearchComponentUUID() {
        return msSearchComponentUUID;
    }

    public void setSearchComponentUUID(String searchComponentUUID) {
        this.msSearchComponentUUID = searchComponentUUID;
    }

    @XmlElement(name = CONF_SERVER_PORT)
    public int getServerPort() {
        return miServerPort;
    }

    public void setServerPort(int serverPort) {
        this.miServerPort = serverPort;
    }

    //do not store this value!
    @XmlTransient
    @Override
    public byte[] getDecryptionKey() {
        return mbaDecryptionKey;
    }
    
    @XmlElement(name = CONF_SERVER_HTTPS)
    public boolean getUsingHttps() {
        return mbUseHttps;
    }
    
    public void setUsingHttps(boolean useHttps) {
        this.mbUseHttps = useHttps;
    }
    
    public void setDecryptionKey(byte[] decryptionKey) {
        this.mbaDecryptionKey = decryptionKey;
    }

    public void emptyConfiguration() {
        msSecurityToken = "";
        msServerAddress = "localhost";
        mbUseHttps = false;
        msUsername = "";
        msPassword = "";
        msLogProps = "";
        miServerPort = 8765;
        miMaxResults = 100;
        miMaxFileSize = 2500000;
        msApplicationUUID = "000000-000000-000000";
        msSearchComponentUUID = "000000-000000-000000";
    }
}
