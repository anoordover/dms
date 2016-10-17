package nl.hetcak.dms.ia.web.comunication;

import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveCredentials implements Credentials {
    private String username, password, securityToken, recoveryToken;
    private Calendar invalidationTime;
    
    /**
     * Sets the username.
     *
     * @param username The Username
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Sets the password.
     *
     * @param password The Password.
     */
    @Override
    public void SetPassword(String password) {
        this.password = password;
    }
    
    /**
     * Sets the security token.
     *
     * @param securityToken The Security Token
     */
    @Override
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
    
    /**
     * Sets the recovery token.
     *
     * @param recoveryToken The Recovery Token
     */
    @Override
    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }
    
    /**
     * Sets the time when the security token is expired.
     *
     * @param time The time when the security token is expired
     */
    @Override
    public void setSecurityTokenInvalidationTime(Calendar time) {
        this.invalidationTime = time;
    }
    
    /**
     * Gets the username
     *
     * @return The username
     */
    @Override
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the password
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }
    
    /**
     * Gets the security token.
     *
     * @return the security token.
     */
    @Override
    public String getSecurityToken() {
        return securityToken;
    }
    
    /**
     * Gets the recovery token.
     *
     * @return the recovery token.
     */
    @Override
    public String getRecoveryToken() {
        return recoveryToken;
    }
    
    /**
     * Checks if the security token is valid.
     *
     * @return true, if the security token is valid.
     */
    @Override
    public boolean isSecurityTokenValid() {
        return invalidationTime.before(Calendar.getInstance());
    }
}
