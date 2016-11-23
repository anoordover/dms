package nl.hetcak.dms;

import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.crypto.Crypto;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 7-10-2016.
 */
public class ActiveMQManagerTest {
    PluggableObjectConfiguration objConfiguration;
    @Before
    public void setup() throws Exception {
        //Generate Keyfile
        String[] cArgs = new String[] {"-key", "testkey", "-keyfile", "data-file", "-data", "My0jKD3WWChHaVFrLwDNLxmaTl72f9PL"};
        Crypto.main(cArgs);

        objConfiguration = mock(PluggableObjectConfiguration.class);
        when(objConfiguration.getParameter("broker")).thenReturn("ssl://muleaq.ont.esb.func.cak-bz.local:10844"); //DEV:
        when(objConfiguration.getParameter("truststore")).thenReturn("../resources/truststore/truststore_ont1_mule.jks");
        when(objConfiguration.getParameter("truststore_password")).thenReturn(Base64.getEncoder().encodeToString(Crypto.encrypt("My0jKD3WWChHaVFrLwDNLxmaTl72f9PL".getBytes(), "testkeytestkeyte".getBytes())));
        when(objConfiguration.getParameter("result_format")).thenReturn("<urn:Item ResultCode=\"%s\" DocumentId=\"%s\", ResultDescription=\"%s\"/>");
        when(objConfiguration.getParameter("results_element")).thenReturn("<urn:Items xmlns:urn=\"urn:hetcak:dms:uitingarchief:2016:08\">%s</urn:Items>");
        when(objConfiguration.getParameter("result_values")).thenReturn("{ERROR};{ID};{MESSAGE}");
        when(objConfiguration.getParameter("input_queue_name")).thenReturn("AanleverenArchiefUitingen");
        when(objConfiguration.getParameter("result_queue_name")).thenReturn("VerwerkArchiefUitingenStatus");
        when(objConfiguration.getParameter("queue_receive_timeout")).thenReturn("500");
    }

    @Test
    public void retrieveDocumentData() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(objConfiguration);
        amm.initialize();
        assertNotNull(amm.retrieveDocumentData());
        amm.shutdown();
    }

    @Test
    public void initialize() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(objConfiguration);
        assertTrue(amm.initialize());
        amm.shutdown();
    }

    @Test
    public void postResult() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(objConfiguration);
        List<IADocumentReference> docrefs = new ArrayList<>();
        IADocumentReference idf = new IADocumentReference("1002224232", "sips");
        idf.setErrorCode(100);
        idf.setErrorMessage("testFailed");
        docrefs.add(idf);
        amm.initialize();
        assertTrue(amm.postResult(docrefs));
        amm.shutdown();
    }
}