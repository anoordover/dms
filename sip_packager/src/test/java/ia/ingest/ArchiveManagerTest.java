package ia.ingest;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.IAServerConfiguration;
import com.amplexor.ia.crypto.Crypto;
import com.amplexor.ia.ingest.ArchiveManager;
import com.amplexor.ia.metadata.IADocument;
import nl.hetcak.dms.CAKDocument;
import nl.hetcak.dms.CAKRetentionClass;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class ArchiveManagerTest {
    IACache objCache;
    IAServerConfiguration objConfiguration;

    @Before
    public void setup() throws Exception {
        //Generate Keyfile
        String[] cArgs = new String[] {"-key", "testkey", "-keyfile", "data-file", "-data", "8TExclergywW"};
        Crypto.main(cArgs);

        objConfiguration = mock(IAServerConfiguration.class);

        when(objConfiguration.getGatewayProtocol()).thenReturn("http");
        when(objConfiguration.getGatewayHost()).thenReturn("infoarchive-rp.front.ont.dms.func.cak-bz.local");
        when(objConfiguration.getGatewayPort()).thenReturn((short)8080);

        when(objConfiguration.getProtocol()).thenReturn("http");
        when(objConfiguration.getHost()).thenReturn("infoarchive-sp.back.ont.dms.func.cak-bz.local");
        when(objConfiguration.getPort()).thenReturn((short)8775);

        when(objConfiguration.getIngestTenant()).thenReturn("INFOARCHIVE");
        when(objConfiguration.getIngestUser()).thenReturn("sa-infoarchive-ont");
        when(objConfiguration.getEncryptedIngestPassword()).thenReturn(Base64.getEncoder().encodeToString(Crypto.encrypt("8TExclergywW".getBytes(), "testkeytestkeyte".getBytes())));

        objCache = new IACache(1, new CAKRetentionClass("R1", "10.2", "R1"));
        objCache.setTargetApplication("CAK_Tijdelijk_Klantarchief");
        objCache.setSipFile("target/sip/testsip.zip");

    }

    @Test
    public void ingestSip() throws Exception {
        ArchiveManager objArchiveManager = new ArchiveManager(objConfiguration);
        objArchiveManager.authenticate();
        objArchiveManager.ingestSip(objCache);
    }

    @Test
    public void authenticate() throws Exception {
        ArchiveManager objArchiveManager = new ArchiveManager(objConfiguration);
        objArchiveManager.authenticate();
    }

}