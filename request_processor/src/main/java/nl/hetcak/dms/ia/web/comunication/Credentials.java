package nl.hetcak.dms.ia.web.comunication;

import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface Credentials {
    /**
     * Sets the username.
     * @param username The Username
     */
    void setUsername(String username);
    
    /**
     * Sets the password.
     * @param password The Password.
     */
    void SetPassword(String password);
    
    /**
     * Sets the security token.
     * @param securityToken The Security Token
     */
    void setSecurityToken(String securityToken);
    
    /**
     * Sets the recovery token.
     * @param recoveryToken The Recovery Token
     */
    void setRecoveryToken(String recoveryToken);
    
    /**
     * Sets the time when the security token is expired.
     * @param time The time when the security token is expired
     */
    void setSecurityTokenInvalidationTime(Calendar time);
    
    /**
     * Gets the username
     * @return The username
     */
    String getUsername();
    
    /**
     * Gets the password
     * @return the password
     */
    String getPassword();
    
    /**
     * Gets the security token.
     * @return the security token.
     */
    String getSecurityToken();
    
    /**
     * Gets the recovery token.
     * @return the recovery token.
     */
    String getRecoveryToken();
    
    /**
     * Checks if the security token is valid.
     * @return true, if the security token is valid.
     */
    boolean isSecurityTokenValid();
    
    
}
