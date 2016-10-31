package nl.hetcak.dms;

import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by minkenbergs on 7-10-2016.
 */
public class ActiveMQManagerTest {
    ConfigManager mobjConfigManager;

    @Before
    public void setup() {
        System.out.println(System.getProperty("user.dir"));
        mobjConfigManager = new ConfigManager("IAArchiver.xml");
        mobjConfigManager.loadConfiguration();
    }

    @Test
    public void retrieveDocumentData() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(mobjConfigManager.getConfiguration().getDocumentSource());
        amm.initialize();
        assertNotNull(amm.retrieveDocumentData());
    }

    @Test
    public void initialize() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(mobjConfigManager.getConfiguration().getDocumentSource());
        assertTrue(amm.initialize());
        amm.shutdown();
    }

    @Test
    public void postResult() throws Exception {
        ActiveMQManager amm = new ActiveMQManager(mobjConfigManager.getConfiguration().getDocumentSource());
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