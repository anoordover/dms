package nl.hetcak.dms.ia.web.comunication;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveConnection implements Connection {
    private String serverAddress;
    private int serverPort;
    private Credentials credentials;
    
    /**
     * Sets the server address.
     *
     * @param serverAddress the server address.
     */
    @Override
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    /**
     * Sets the server port.
     *
     * @param serverPort the server port.
     */
    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    /**
     * Sets the Credentials.
     *
     * @param credentials the credentials.
     */
    @Override
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    
}
