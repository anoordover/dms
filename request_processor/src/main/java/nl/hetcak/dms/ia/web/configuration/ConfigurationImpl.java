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
    private final static String CONF_SERVICE_USER = "ia_service_usr";
    private final static String CONF_SERVICE_USER_PASSWORD = "ia_service_usr_password";
    private final static String CONF_LOG_PROPS = "log4j_properties";
    private final static String CONF_MAX_FILESIZE = "max_allowed_filesize";
    private final static String CONF_MAX_RESULTS = "max_allowed_results";
    private final static String CONF_APPLICATION_UUID = "ia_application_uuid";
    private final static String CONF_SEARCHCOMPOSITION_UUID = "ia_searchcomposition_uuid";

    private String securityToken, serverAddress, username, password, logProps, applicationUUID, searchComponentUUID;
    private int serverPort, maxResults, maxFileSize;


    @XmlElement(name = CONF_SECURITY_TOKEN)
    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    @Override
    public String getInfoArchiveServerAddress() {
        return serverAddress;
    }

    @Override
    public int getInfoArchiveServerPort() {
        return serverPort;
    }

    @Override
    public Credentials getInfoArchiveCredentials() {
        Credentials credentials = new InfoArchiveCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);

        if (StringUtils.isNotBlank(securityToken)) {
            credentials.setSecurityToken(securityToken);
            credentials.setUseLoginToken(true);
        }
        return credentials;
    }

    @Override
    public ServerConnectionInformation getInfoArchiveServerInformation() {
        InfoArchiveServerConnectionInformation iaconnection = new InfoArchiveServerConnectionInformation();
        iaconnection.setServerAddress(serverAddress);
        iaconnection.setServerPort(serverPort);
        iaconnection.setMaxItems(maxResults);
        return iaconnection;
    }

    @XmlElement(name = CONF_MAX_RESULTS)
    @Override
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Get the file size limit.
     *
     * @return the maximum file size.
     */
    @XmlElement(name = CONF_MAX_FILESIZE)
    @Override
    public int getMaxFileSize() {
        return maxFileSize ;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void setMaxFileSize(int maxFileSize) { this.maxFileSize = maxFileSize; }

    @XmlElement(name = CONF_APPLICATION_UUID)
    @Override
    public String getApplicationUUID() {
        return applicationUUID;
    }

    public void setApplicationUUID(String applicationUUID) {
        this.applicationUUID = applicationUUID;
    }

    @Override
    public String getSearchCompositionUUID() {
        return searchComponentUUID;
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
                if (applicationUUID.length() == 0 || searchComponentUUID.length() == 0) {
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
        return logProps;
    }

    public void setLogProps(String logProps) {
        this.logProps = logProps;
    }

    @XmlElement(name = CONF_SERVER_ADDRESS)
    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @XmlElement(name = CONF_SERVICE_USER)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement(name = CONF_SERVICE_USER_PASSWORD)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement(name = CONF_SEARCHCOMPOSITION_UUID)
    public String getSearchComponentUUID() {
        return searchComponentUUID;
    }

    public void setSearchComponentUUID(String searchComponentUUID) {
        this.searchComponentUUID = searchComponentUUID;
    }

    @XmlElement(name = CONF_SERVER_PORT)
    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void emptyConfiguration() {
        securityToken = "";
        serverAddress = "localhost";
        username = "";
        password = "";
        logProps = "";
        serverPort = 8765;
        maxResults = 100;
        maxFileSize = 2500000;
        applicationUUID = "000000-000000-000000";
        searchComponentUUID = "000000-000000-000000";
    }
}
