package com.amplexor.ia.ingest;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.Base64;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class IACredentialsTest {

    @Test
    public void setGetUsername() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setUsername("testuser");
        assertEquals(iactest.getUsername(), "testuser");
    }

    @Test
    public void setGetPassword() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setPassword("testpass");
        assertEquals(iactest.getPassword(), "testpass");
    }

    @Test
    public void setGetToken() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setToken("testtoken");
        assertEquals(iactest.getToken(), "testtoken");
    }

    @Test
    public void setGetRefreshToken() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setRefreshToken("testrefreshtoken");
        assertEquals(iactest.getRefreshToken(), "testrefreshtoken");
    }


    @Test
    public void setGetExpiry() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setExpiry(1000);
        assertEquals(iactest.getExpiry(), 1000);
    }

    @Test
    public void hasExpiredNotExpired() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setExpiry(System.currentTimeMillis()+10000);
        assertFalse(iactest.hasExpired());
    }

    @Test
    public void hasExpiredExpired() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setExpiry(System.currentTimeMillis()+1);
        Thread.sleep(5);
        assertTrue(iactest.hasExpired());
    }

    @Test
    public void getLoginQuery() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setUsername("testUser");
        iactest.setPassword("testPassword");
        StringBuilder objBuilder = new StringBuilder();
        objBuilder.append(URLEncoder.encode("username", "UTF-8"));
        objBuilder.append("=");
        objBuilder.append(URLEncoder.encode("testUser", "UTF-8"));
        objBuilder.append("&");
        objBuilder.append(URLEncoder.encode("password", "UTF-8"));
        objBuilder.append("=");
        String sDecodedPassword = new String(Base64.getDecoder().decode("testPassword".getBytes()));
        objBuilder.append(URLEncoder.encode(sDecodedPassword, "UTF-8"));
        objBuilder.append("&");
        objBuilder.append(URLEncoder.encode("grant_type", "UTF-8"));
        objBuilder.append("=");
        objBuilder.append(URLEncoder.encode("password", "UTF-8"));

        assertEquals(iactest.getLoginQuery(), objBuilder.toString());
    }

    @Test
    public void getRefreshQuery() throws Exception {
        IACredentials iactest = new IACredentials();
        iactest.setRefreshToken("testRefreshToken");
        StringBuilder objBuilder = new StringBuilder();
        objBuilder.append(URLEncoder.encode("refresh_token", "UTF-8"));
        objBuilder.append("=");
        objBuilder.append(URLEncoder.encode("testRefreshToken", "UTF-8"));
        objBuilder.append("&");
        objBuilder.append(URLEncoder.encode("grant_type", "UTF-8"));
        objBuilder.append("=");
        objBuilder.append(URLEncoder.encode("refresh_token", "UTF-8"));

        assertEquals(iactest.getRefreshQuery(), objBuilder.toString());
    }

}