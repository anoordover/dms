package com.amplexor.ia.ingest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * Created by admjzimmermann on 13-9-2016.
 */
public class IACredentials {
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String IA_QUERY_USERNAME = "username";
    private static final String IA_QUERY_PASS = "password";
    private static final String IA_REFRESH_TOKEN = "refresh_token";
    private static final String IA_QUERY_LOGIN_GRANT = "grant_type";
    private static final String IA_LOGIN_GRANT_REFRESH = "refresh_token";
    private static final String IA_LOGIN_GRANT_PASS = "password";

    private String msUsername;
    private String msPassword;
    private String msToken;
    private String msRefreshToken;
    private long mlExpiry;

    public IACredentials() {
        msUsername = "";
        msPassword = null;
        msToken = "";
        msRefreshToken = "";
        mlExpiry = -1;
    }

    public String getUsername() {
        return msUsername;
    }

    public void setUsername(String sUsername) {
        msUsername = sUsername;
    }

    public String getPassword() {
        return msPassword;
    }

    public void setPassword(String sPassword) {
        msPassword = sPassword;
    }

    public void setToken(String sToken) {
        msToken = sToken;
    }

    public void setRefreshToken(String sRefreshToken) {
        msRefreshToken = sRefreshToken;
    }

    public String getRefreshToken() {
        return msRefreshToken;
    }

    public void setExpiry(long lExpiry) {
        mlExpiry = lExpiry;
    }

    public String getToken() {
        return msToken;
    }

    public long getExpiry() {
        return mlExpiry;
    }

    public boolean hasExpired() {
        return mlExpiry <= System.currentTimeMillis();
    }


    public String getLoginQuery() throws UnsupportedEncodingException {
        StringBuilder objBuilder = new StringBuilder();
        objBuilder.append(URLEncoder.encode(IA_QUERY_USERNAME, ENCODING_UTF8));
        objBuilder.append("=");
        objBuilder.append(URLEncoder.encode(msUsername, ENCODING_UTF8));
        objBuilder.append("&");
        objBuilder.append(URLEncoder.encode(IA_QUERY_PASS, ENCODING_UTF8));
        objBuilder.append("=");
        String sDecodedPassword = new String(Base64.getDecoder().decode(msPassword.getBytes()));
        objBuilder.append(URLEncoder.encode(sDecodedPassword, ENCODING_UTF8));
        objBuilder.append("&");
        objBuilder.append(URLEncoder.encode(IA_QUERY_LOGIN_GRANT, ENCODING_UTF8));
        objBuilder.append("=");
        objBuilder.append(URLEncoder.encode(IA_LOGIN_GRANT_PASS, ENCODING_UTF8));

        return objBuilder.toString();
    }

    public String getRefreshQuery() throws UnsupportedEncodingException {
        return URLEncoder.encode(IA_REFRESH_TOKEN, ENCODING_UTF8) +
                "=" +
                URLEncoder.encode(msRefreshToken, ENCODING_UTF8) +
                "&" +
                URLEncoder.encode(IA_QUERY_LOGIN_GRANT, ENCODING_UTF8) +
                "=" +
                URLEncoder.encode(IA_LOGIN_GRANT_REFRESH, ENCODING_UTF8);
    }


}
