package nl.hetcak.dms.ia.web.comunication;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveServerConnectionInformation implements ServerConnectionInformation {
    private String serverAddress;
    private int serverPort;
    
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
     * Gets the server address.
     *
     * @return the server address.
     */
    @Override
    public String getServerAddress() {
        return serverAddress;
    }
    
    /**
     * Gets the server port.
     *
     * @return the server port.
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }
    
}
