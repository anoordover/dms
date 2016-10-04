package com.amplexor.ia.ingest;

import org.junit.Ignore;
import org.junit.Test;

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

    @Ignore
    public void getLoginQuery() throws Exception {

    }

    @Ignore
    public void getRefreshQuery() throws Exception {

    }

}