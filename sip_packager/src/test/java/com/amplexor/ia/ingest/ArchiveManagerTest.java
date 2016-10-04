package com.amplexor.ia.ingest;

import com.amplexor.ia.configuration.IAServerConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class ArchiveManagerTest {

    @Ignore
    public void ingestSip() throws Exception {

    }

    @Test
    public void login() throws Exception {
        IAServerConfiguration iasc = mock(IAServerConfiguration.class);
        when(iasc.getIngestUser()).thenReturn("admjzimmermann");
        when(iasc.getEncryptedIngestPassword()).thenReturn("V3VyY2l0d2FnNDAt");
        when(iasc.getGatewayProtocol()).thenReturn("http");
        when(iasc.getGatewayHost()).thenReturn("cwno0427.cak-bz.local");
        when(iasc.getGatewayPort()).thenReturn((short)8080);
        ArchiveManager am = new ArchiveManager(iasc);
        am.login();

        //// TODO: 4-10-2016 add test assertion
        //test needs to be validated, when is am.login() successful
    }

}