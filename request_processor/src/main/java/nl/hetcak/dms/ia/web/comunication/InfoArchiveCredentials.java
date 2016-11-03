package nl.hetcak.dms.ia.web.comunication;

import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveCredentials implements Credentials {
    private String username;
    private String password;
    private String securityToken;
    private String recoveryToken;
    private Calendar invalidationTime;
    private boolean useLoginToken = false;

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
     * Sets the username.
     *
     * @param username The Username
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the decrypted password.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password The Password.
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
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
     * Sets the security token.
     *
     * @param securityToken The Security Token
     */
    @Override
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
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
     * Sets the recovery token.
     *
     * @param recoveryToken The Recovery Token
     */
    @Override
    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    /**
     * Checks if the security token is valid.
     *
     * @return true, if the security token is valid.
     */
    @Override
    public boolean isSecurityTokenValid() {
        if (invalidationTime == null) {
            return false;
        }
        return invalidationTime.after(Calendar.getInstance());
    }

    /**
     * Allows the User to set a token and ignore the username and password setting.
     *
     * @return true if the username and password value needs to be ignored.
     */
    @Override
    public boolean useTokenOnlyConfiguration() {
        return useLoginToken;
    }

    /**
     * Allows the User to set a token and ignore the username and password setting.
     *
     * @param useLoginToken true if the username and password value needs to be ignored.
     */
    @Override
    public void setUseLoginToken(Boolean useLoginToken) {
        this.useLoginToken = useLoginToken;
    }
}
