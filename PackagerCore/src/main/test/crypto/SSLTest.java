package crypto;

import com.amplexor.ia.configuration.IAServerConfiguration;
import com.amplexor.ia.crypto.Crypto;
import com.amplexor.ia.ingest.ArchiveManager;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by admjzimmermann on 10-11-2016.
 */
public class SSLTest {
    @Test
    public void testConnection() throws Exception {
        IAServerConfiguration objConfiguration = mock(IAServerConfiguration.class);
        when(objConfiguration.getGatewayProtocol()).thenReturn("https");
        when(objConfiguration.getGatewayHost()).thenReturn("infoarchive-rp.front.tst.dms.func.cak-bz.local");
        when(objConfiguration.getGatewayPort()).thenReturn((short)10743);

        when(objConfiguration.getProtocol()).thenReturn("http");
        when(objConfiguration.getHost()).thenReturn("infoarchive-sp.back.ont.dms.func.cak-bz.local");
        when(objConfiguration.getPort()).thenReturn((short)8775);

        when(objConfiguration.getIngestTenant()).thenReturn("INFOARCHIVE");
        when(objConfiguration.getIngestUser()).thenReturn("sa-infoarchive-tst");
        when(objConfiguration.getEncryptedIngestPassword()).thenReturn("Ymx4VVMKf2w6Mix+FTUXDOHKi93i/G9KAVMFBltwRF4=");


        ArchiveManager objManager = new ArchiveManager(objConfiguration);
        objManager.authenticate();
    }
}
