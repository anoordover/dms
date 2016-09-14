package com.amplexor.ia.ingest;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * Created by admjzimmermann on 13-9-2016.
 */
public class IACredentials {
    private static String IA_USERNAME = "username";
    private static String IA_PASSWORD = "password";
    private static String IA_REFRESH_TOKEN = "refresh_token";
    private static String IA_LOGIN_GRANT = "grant_type";
    private static String IA_LOGIN_GRANT_REFRESH = "refresh_token";
    private static String IA_LOGIN_GRANT_PASSWORD = "password";

    private String mUsername;
    private String mPassword;
    private String mToken;
    private String mRefreshToken ="";
    private long mExpiry;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String aUsername) {
        mUsername = aUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(byte[] aPassword) {
        mPassword = Base64.getEncoder().encodeToString(aPassword);
    }

    public void setToken(String aToken) {
        mToken = aToken;
    }

    public void setRefreshToken(String aRefreshToken) {
        mRefreshToken = aRefreshToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setExpiry(long aExpiry) {
        mExpiry = aExpiry;
    }

    public String getToken() {
        return mToken;
    }

    public long getExpiry() {
        return mExpiry;
    }

    public boolean hasExpired() {
        return (mExpiry < System.currentTimeMillis());
    }

    public IACredentials() {

    }

    public String getLoginQuery() {
        StringBuilder oBuilder = new StringBuilder();
        try {
            oBuilder.append(URLEncoder.encode(IA_USERNAME, "UTF-8"));
            oBuilder.append("=");
            oBuilder.append(URLEncoder.encode(mUsername, "UTF-8"));
            oBuilder.append("&");
            oBuilder.append(URLEncoder.encode(IA_PASSWORD, "UTF-8"));
            oBuilder.append("=");
            String sDecodedPassword = new String(Base64.getDecoder().decode(mPassword.getBytes()));
            oBuilder.append(URLEncoder.encode(sDecodedPassword, "UTF-8"));
            sDecodedPassword = null;
            oBuilder.append("&");
            oBuilder.append(URLEncoder.encode(IA_LOGIN_GRANT, "UTF-8"));
            oBuilder.append("=");
            oBuilder.append(URLEncoder.encode(IA_LOGIN_GRANT_PASSWORD, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return oBuilder.toString();
    }

    public String getRefreshQuery() {
        StringBuilder oBuilder = new StringBuilder();
        try {
            oBuilder.append(URLEncoder.encode(IA_REFRESH_TOKEN, "UTF-8"));
            oBuilder.append("=");
            oBuilder.append(URLEncoder.encode(mRefreshToken, "UTF-8"));
            oBuilder.append("&");
            oBuilder.append(URLEncoder.encode(IA_LOGIN_GRANT, "UTF-8"));
            oBuilder.append("=");
            oBuilder.append(URLEncoder.encode(IA_LOGIN_GRANT_REFRESH, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return oBuilder.toString();
    }


}
