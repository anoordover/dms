package nl.hetcak.dms.ia.web.comunication;

import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface Credentials {
    /**
     * Sets the time when the security token is expired.
     *
     * @param time The time when the security token is expired
     */
    void setSecurityTokenInvalidationTime(Calendar time);

    /**
     * Gets the username
     *
     * @return The username
     */
    String getUsername();

    /**
     * Sets the username.
     *
     * @param username The Username
     */
    void setUsername(String username);

    /**
     * Gets the password
     *
     * @return the password
     */
    String getPassword();

    /**
     * Sets the password.
     *
     * @param password The Password.
     */
    void setPassword(String password);

    /**
     * Gets the security token.
     *
     * @return the security token.
     */
    String getSecurityToken();

    /**
     * Sets the security token.
     *
     * @param securityToken The Security Token
     */
    void setSecurityToken(String securityToken);

    /**
     * Gets the recovery token.
     *
     * @return the recovery token.
     */
    String getRecoveryToken();

    /**
     * Sets the recovery token.
     *
     * @param recoveryToken The Recovery Token
     */
    void setRecoveryToken(String recoveryToken);

    /**
     * Checks if the security token is valid.
     *
     * @return true, if the security token is valid.
     */
    boolean isSecurityTokenValid();

    /**
     * Allows the User to set a token and ignore the username and password setting.
     *
     * @return true if the username and password value needs to be ignored.
     */
    boolean useTokenOnlyConfiguration();

    /**
     * Allows the User to set a token and ignore the username and password setting.
     *
     * @param useLoginToken true if the username and password value needs to be ignored.
     */
    void setUseLoginToken(Boolean useLoginToken);
}
