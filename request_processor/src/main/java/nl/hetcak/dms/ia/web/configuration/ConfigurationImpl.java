package nl.hetcak.dms.ia.web.configuration;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.comunication.InfoArchiveCredentials;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "config-request-processor")
public class ConfigurationImpl implements Configuration{
    private final static String CONF_SECURITY_TOKEN = "ia_token";
    private final static String CONF_SERVER_ADDRESS = "ia_server";
    private final static String CONF_SERVER_PORT = "ia_port";
    private final static String CONF_SERVICE_USER = "ia_service_usr";
    private final static String CONF_SERVICE_USER_PASSWORD = "ia_service_usr_password";
    private final static String CONF_LOG_PROPS = "log4j_properties";
    private final static String CONF_MAX_RESULTS = "maximaal_aantal_resultaten";
    private final static String CONF_APPLICATION_UUID = "ia_application_uuid";
    private final static String CONF_SEARCHCOMPOSITION_UUID = "ia_searchcomposition_uuid";
    
    private  String securityToken, serverAddress, username, password, logProps, applicationUUID, searchComponentUUID;
    private  int serverPort, maxResults;
    
    
    @XmlElement(name = CONF_SECURITY_TOKEN)
    public String getSecurityToken() {
        return securityToken;
    }
    
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
    
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setLogProps(String logProps) {
        this.logProps = logProps;
    }
    
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public void setApplicationUUID(String applicationUUID) {
        this.applicationUUID = applicationUUID;
    }
    
    public void setSearchComponentUUID(String searchComponentUUID) {
        this.searchComponentUUID = searchComponentUUID;
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
        
        if(StringUtils.isNotBlank(securityToken)) {
            credentials.setSecurityToken(securityToken);
            credentials.setUseLoginToken(true);
        }
        return credentials;
    }
    
    @XmlElement(name = CONF_MAX_RESULTS)
    public int getMaxResults() {
        return maxResults;
    }
    
    @XmlElement(name = CONF_APPLICATION_UUID)
    public String getApplicationUUID() {
        return applicationUUID;
    }
    
    @Override
    public String getSearchCompositionUUID() {
        return searchComponentUUID;
    }
    
    @XmlElement(name = CONF_LOG_PROPS)
    public String getLogProps() {
        return logProps;
    }
    
    @XmlElement(name = CONF_SERVER_ADDRESS)
    public String getServerAddress() {
        return serverAddress;
    }
    
    @XmlElement(name = CONF_SERVICE_USER)
    public String getUsername() {
        return username;
    }
    
    @XmlElement(name = CONF_SERVICE_USER_PASSWORD)
    public String getPassword() {
        return password;
    }
    
    @XmlElement(name = CONF_SEARCHCOMPOSITION_UUID)
    public String getSearchComponentUUID() {
        return searchComponentUUID;
    }
    
    @XmlElement(name = CONF_SERVER_PORT)
    public int getServerPort() {
        return serverPort;
    }
    
    public void emptyConfiguration(){
        securityToken = "";
        serverAddress = "localhost";
        username = "";
        password = "";
        logProps = "";
        serverPort = 8765;
        maxResults = 100;
        applicationUUID = "000000-000000-000000";
        searchComponentUUID = "000000-000000-000000";
    }
}
