package nl.hetcak.dms.ia.web.comunication;

import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveCredentials implements Credentials {
    private String msUsername;
    private String msPassword;
    private String msSecurityToken;
    private String msRecoveryToken;
    private Calendar mobjInvalidationTime;
    private boolean mbUseLoginToken = false;

    /**
     * Sets the time when the security token is expired.
     *
     * @param time The time when the security token is expired
     */
    @Override
    public void setSecurityTokenInvalidationTime(Calendar time) {
        this.mobjInvalidationTime = time;
    }

    /**
     * Gets the username
     *
     * @return The username
     */
    @Override
    public String getUsername() {
        return msUsername;
    }

    /**
     * Sets the username.
     *
     * @param username The Username
     */
    @Override
    public void setUsername(String username) {
        this.msUsername = username;
    }

    /**
     * Gets the decrypted msPassword.
     *
     * @return the msPassword
     */
    @Override
    public String getPassword() {
        return msPassword;
    }

    /**
     * Sets the msPassword.
     *
     * @param password The Password.
     */
    @Override
    public void setPassword(String password) {
        this.msPassword = password;
    }

    /**
     * Gets the security token.
     *
     * @return the security token.
     */
    @Override
    public String getSecurityToken() {
        return msSecurityToken;
    }

    /**
     * Sets the security token.
     *
     * @param securityToken The Security Token
     */
    @Override
    public void setSecurityToken(String securityToken) {
        this.msSecurityToken = securityToken;
    }

    /**
     * Gets the recovery token.
     *
     * @return the recovery token.
     */
    @Override
    public String getRecoveryToken() {
        return msRecoveryToken;
    }

    /**
     * Sets the recovery token.
     *
     * @param recoveryToken The Recovery Token
     */
    @Override
    public void setRecoveryToken(String recoveryToken) {
        this.msRecoveryToken = recoveryToken;
    }

    /**
     * Checks if the security token is valid.
     *
     * @return true, if the security token is valid.
     */
    @Override
    public boolean isSecurityTokenValid() {
        if (mobjInvalidationTime == null) {
            return false;
        }
        return mobjInvalidationTime.after(Calendar.getInstance());
    }

    /**
     * Allows the User to set a token and ignore the username and msPassword setting.
     *
     * @return true if the username and msPassword value needs to be ignored.
     */
    @Override
    public boolean useTokenOnlyConfiguration() {
        return mbUseLoginToken;
    }

    /**
     * Allows the User to set a token and ignore the username and msPassword setting.
     *
     * @param useLoginToken true if the username and msPassword value needs to be ignored.
     */
    @Override
    public void setUseLoginToken(Boolean useLoginToken) {
        this.mbUseLoginToken = useLoginToken;
    }
}
