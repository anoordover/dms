package nl.hetcak.dms;

import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by minkenbergs on 7-10-2016.
 */
public class ActiveMQManagerTest {
    @Test
    public void retrieveDocumentData() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(mock(PluggableObjectConfiguration.class));
        amm.initialize();
        assertNotNull(amm.retrieveDocumentData());
    }

    @Test
    public void initialize() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(mock(PluggableObjectConfiguration.class));
        assertTrue(amm.initialize());
    }

    @Test
    public void postResult() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(mock(PluggableObjectConfiguration.class));
        List<IADocumentReference> docrefs = new ArrayList<>();
        IADocumentReference idf = new IADocumentReference("1002224232", "sips");
        idf.setErrorCode(100);
        idf.setErrorMessage("testFailed");
        docrefs.add(idf);
        assertTrue(amm.postResult(docrefs));
    }

}