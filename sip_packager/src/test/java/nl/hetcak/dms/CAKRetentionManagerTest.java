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
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class CAKRetentionManagerTest {
    RetentionManagerConfiguration objConfiguration;
    List<IARetentionClass> cRetetionClasses;
    CAKDocument objDocument;

    @Before
    public void setup() {
        cRetetionClasses = new ArrayList<>();
        CAKRetentionClass objRetentionClass = new CAKRetentionClass("R1", "10.2", "R1");
        objRetentionClass.addAssociatedTitle("B01");

        objConfiguration = mock(RetentionManagerConfiguration.class);
        when(objConfiguration.getRetentionClasses()).thenReturn(cRetetionClasses);
        objConfiguration.getRetentionClasses().add(objRetentionClass);
        when(objConfiguration.getRetentionElementName()).thenReturn("ArchiefDocumenttitel");

        objDocument = new CAKDocument();
        objDocument.setDocumentId("1000010000");
        objDocument.setMetadata("ArchiefDocumentId", "1000010000");
        objDocument.setMetadata("ArchiefDocumenttitel", "B01");
    }

    @Test
    public void retrieveRetentionClassSuccess() throws Exception {
        CAKRetentionManager crm = new CAKRetentionManager(objConfiguration);
        assertNotNull(crm.retrieveRetentionClass(objDocument));
    }

    @Test
    public void retrieveRetentionClassFailure() throws Exception {
        IADocument iad = mock(IADocument.class);
        when(iad.getMetadata("ArchiefDocumenttitel")).thenReturn("Z121");

        CAKRetentionManager crm = new CAKRetentionManager(objConfiguration);
        IARetentionClass retentionClass = null;
        try {
            retentionClass = crm.retrieveRetentionClass(iad);
        } catch (Exception e) {

        }
        assertNull(retentionClass);

    }

}