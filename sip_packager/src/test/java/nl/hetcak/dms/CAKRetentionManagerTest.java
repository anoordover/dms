package nl.hetcak.dms;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class CAKRetentionManagerTest {
    ConfigManager mobjConfigManager;

    @Before
    public void setup() {
        System.out.println(System.getProperty("user.dir"));
        mobjConfigManager = new ConfigManager("IAArchiver.xml");
        mobjConfigManager.loadConfiguration();
    }

    @Test
    public void retrieveRetentionClassSuccess() throws Exception {
        CAKDocument objDocument = new CAKDocument();
        objDocument.setDocumentId("1100011000");
        objDocument.setMetadata("ArchiefDocumentId", "1100011000");
        objDocument.setMetadata("ArchiefDocumenttitel", "B01");


        CAKRetentionManager crm = new CAKRetentionManager(mobjConfigManager.getConfiguration().getRetentionManager());
        assertNotNull(crm.retrieveRetentionClass(objDocument));

    }

    @Test
    public void retrieveRetentionClassFailure() throws Exception {
        IADocument iad = mock(IADocument.class);
        when(iad.getMetadata("ArchiefDocumenttitel")).thenReturn("Z121");

        CAKRetentionManager crm = new CAKRetentionManager(mobjConfigManager.getConfiguration().getRetentionManager());
        IARetentionClass retentionClass = null;
        try {
            retentionClass = crm.retrieveRetentionClass(iad);
        }catch(Exception e){

        }
        assertNull(retentionClass);

    }

}