package ia.ingest;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.IAServerConfiguration;
import com.amplexor.ia.crypto.Crypto;
import com.amplexor.ia.ingest.ArchiveManager;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class ArchiveManagerTest {
    IACache objCache;
    IAServerConfiguration objConfiguration;

    @Before
    public void setup() {
        objConfiguration = mock(IAServerConfiguration.class);

        when(objConfiguration.getGatewayProtocol()).thenReturn("http");
        when(objConfiguration.getGatewayHost()).thenReturn("infoarchive-rp.front.ont.dms.func.cak-bz.local");
        when(objConfiguration.getGatewayPort()).thenReturn((short)8080);

        when(objConfiguration.getProtocol()).thenReturn("http");
        when(objConfiguration.getHost()).thenReturn("infoarchive-sp.back.ont.dms.func.cak-bz.local");
        when(objConfiguration.getPort()).thenReturn((short)8775);

        when(objConfiguration.getIngestTenant()).thenReturn("INFOARCHIVE");
        when(objConfiguration.getIngestUser()).thenReturn("sa-infoarchive-ont");
        when(objConfiguration.getEncryptedIngestPassword()).thenReturn("");

    }

    @Test
    public void ingestSip() throws Exception {

    }

    @Test
    public void login() throws Exception {
    }

}