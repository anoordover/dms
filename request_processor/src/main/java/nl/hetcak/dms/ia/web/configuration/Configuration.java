package nl.hetcak.dms.ia.web.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement
public class Configuration {
    private final static String CONF_SECURITY_TOKEN = "ia_token";
    private final static String CONF_SERVER_ADDRESS = "ia_server";
    private final static String CONF_SERVER_PORT = "ia_port";
    private final static String CONF_SERVICE_USER = "ia_service_usr";
    private final static String CONF_SERVICE_USER_PASSWORD = "ia_service_usr_password";
    private final static String CONF_LOG_PROPS = "log4j_properties";
    private final static String CONF_MAX_RESULTS = "maximaal_aantal_resultaten";
    private final static String CONF_APPLICATION_UUID = "ia_application_uuid";
    
    private String securityToken, serverAddress, username, password, logProps, applicationUUID;
    private int serverPort, maxResults;
    
    public String getSecurityToken() {
        return securityToken;
    }
    
    @XmlElement(name = CONF_SECURITY_TOKEN)
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
    
    public String getServerAddress() {
        return serverAddress;
    }
    
    @XmlElement(name = CONF_SERVER_ADDRESS)
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public String getUsername() {
        return username;
    }
    
    @XmlElement(name = CONF_SERVICE_USER)
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    @XmlElement(name = CONF_SERVICE_USER_PASSWORD)
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getLogProps() {
        return logProps;
    }
    
    @XmlElement(name = CONF_LOG_PROPS)
    public void setLogProps(String logProps) {
        this.logProps = logProps;
    }
    
    public int getServerPort() {
        return serverPort;
    }
    
    @XmlElement(name = CONF_SERVER_PORT)
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    public int getMaxResults() {
        return maxResults;
    }
    
    @XmlElement(name = CONF_MAX_RESULTS)
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public String getApplicationUUID() {
        return applicationUUID;
    }
    
    @XmlElement(name = CONF_APPLICATION_UUID)
    public void setApplicationUUID(String applicationUUID) {
        this.applicationUUID = applicationUUID;
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
    }
}
