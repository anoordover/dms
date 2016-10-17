package nl.hetcak.dms.ia.web.comunication;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface Connection {
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
     * Sets the Credentials.
     * @param credentials the credentials.
     */
    void setCredentials(Credentials credentials);
        
}
