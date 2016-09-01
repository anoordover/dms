package nl.hetcak.dms.ia;

import java.util.Date;

/**
 * Created by admjzimmermann on 1-9-2016.
 */
public class IACredentials {
    private String username;
    private String token;
    private long valid;

    public String getUsername(){
        return username;
    }

    public void setUsername(String aUsername) {
        username = aUsername;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String aToken) {
        token = aToken;
    }

    public boolean isValid() {
        return new Date().getTime() < valid;
    }

    public void refresh(long aExpires) {
        aExpires *= 1000; //Convert to seconds from milliseconds
        valid = new Date().getTime() + aExpires;
    }

    public IACredentials(String aUsername) {
        this(aUsername, "", 0);
    }

    public IACredentials(String aUsername, String aToken, long aValid) {
        username = aUsername;
        token = aToken;
        valid = aValid;
    }
}
