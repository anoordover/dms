package nl.hetcak.dms.ia.web.comunication;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface ServerConnectionInformation {
    /**
     * Gets the server address.
     *
     * @return the server address.
     */
    String getServerAddress();

    /**
     * Sets the server address.
     *
     * @param serverAddress the server address.
     */
    void setServerAddress(String serverAddress);

    /**
     * Gets the server port.
     *
     * @return the server port.
     */
    int getServerPort();

    /**
     * Sets the server port.
     *
     * @param serverPort the server port.
     */
    void setServerPort(int serverPort);

    /**
     * Gets the max items size.
     *
     * @return the max items
     */
    int getMaxItems();

    /**
     * Sets the max item count.
     *
     * @param maxItems total items
     */
    void setMaxItems(int maxItems);
    
    /**
     * True if the request should use https.
     * @return True if the request should use https.
     */
    boolean isUsingHttps();
    
    /**
     * Sets if the request should start with https.
     * @param https true if the request should use https.
     */
    void setHttps(boolean https);

}
