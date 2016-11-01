package com.amplexor.ia.ingest;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.IAServerConfiguration;
import com.amplexor.ia.ingest.ArchiveManager;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class ArchiveManagerTest {
    ConfigManager mobjConfigManager;
    @Before
    public void setup() {
        System.out.println(System.getProperty("user.dir"));
        mobjConfigManager = new ConfigManager("IAArchiver.xml");
        mobjConfigManager.loadConfiguration();
    }

    @Test
    public void ingestSip() throws Exception {
        IACache ic = mock(IACache.class);
        //change null to sipfile
        when(ic.getSipFile()).thenReturn(null);
        when(ic.getTargetApplication()).thenReturn("CAK_Klantarchief");

        IAServerConfiguration isc = mock(IAServerConfiguration.class);
        when(isc.getIngestTenant()).thenReturn("INFOARCHIVE");
        when(isc.getIAApplicationName()).thenReturn("JUnit test");
        when(isc.getProtocol()).thenReturn("HTTP");
        when(isc.getHost()).thenReturn("cwno0427.cak-bz.local");
        when(isc.getPort()).thenReturn((short)8765);
        when(isc.getGatewayProtocol()).thenReturn("HTTP");
        when(isc.getGatewayHost()).thenReturn("cwno0427.cak-bz.local");
        when(isc.getGatewayPort()).thenReturn((short)8080);
        when(isc.getIngestUser()).thenReturn("admjzimmermann");
        when(isc.getEncryptedIngestPassword()).thenReturn("V3VyY2l0d2FnNDAt");

        ArchiveManager am = new ArchiveManager(isc);

        //assertTrue(am.ingestSip(ic));

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
        //assertTrue(am.authenticate());
    }

}