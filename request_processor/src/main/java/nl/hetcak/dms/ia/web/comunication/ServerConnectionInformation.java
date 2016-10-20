package nl.hetcak.dms.ia.web.comunication;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface ServerConnectionInformation {
    /**
     * Sets the server address.
     * @param serverAddress the server address.
     */
    void setServerAddress(String serverAddress);
    
    /**
     * Sets the server port.
     * @param serverPort the server port.
     */
    void setServerPort(int serverPort);
    
    /**
     * Gets the server address.
     * @return the server address.
     */
    String getServerAddress();
    
    /**
     * Gets the server port.
     * @return the server port.
     */
    int getServerPort();
    
    /**
     * Sets the max item count.
     * @param maxItems total items
     */
    void setMaxItems(int maxItems);
    
    /**
     * Gets the max items size.
     * @return the max items
     */
    int getMaxItems();
    
}
