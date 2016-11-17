package nl.hetcak.dms.ia.web.comunication;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveServerConnectionInformation implements ServerConnectionInformation {
    private String msServerAddress;
    private int miServerPort;
    private int miMaxItems;
    private boolean useHttps;

    /**
     * Gets the server address.
     *
     * @return the server address.
     */
    @Override
    public String getServerAddress() {
        return msServerAddress;
    }

    /**
     * Sets the server address.
     *
     * @param serverAddress the server address.
     */
    @Override
    public void setServerAddress(String serverAddress) {
        this.msServerAddress = serverAddress;
    }

    /**
     * Gets the server port.
     *
     * @return the server port.
     */
    @Override
    public int getServerPort() {
        return miServerPort;
    }

    /**
     * Sets the server port.
     *
     * @param serverPort the server port.
     */
    @Override
    public void setServerPort(int serverPort) {
        this.miServerPort = serverPort;
    }

    /**
     * Gets the max items size.
     *
     * @return the max items
     */
    @Override
    public int getMaxItems() {
        return miMaxItems;
    }

    /**
     * Sets the max item count.
     *
     * @param maxItems total items
     */
    @Override
    public void setMaxItems(int maxItems) {
        this.miMaxItems = maxItems;
    }
    
    /**
     * True if the request should use https.
     *
     * @return True if the request should use https.
     */
    @Override
    public boolean isUsingHttps() {
        return useHttps;
    }
    
    /**
     * Sets if the request should start with https.
     *
     * @param https true if the request should use https.
     */
    @Override
    public void setHttps(boolean https) {
        this.useHttps = https;
    }
    
    
}
