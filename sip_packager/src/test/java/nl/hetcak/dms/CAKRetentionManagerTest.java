package nl.hetcak.dms;

import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
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
    @Test
    public void retrieveRetentionClassSuccess() throws Exception {
        //mobjConfiguration.getRetentionElementName()
        RetentionManagerConfiguration rmc = mock(RetentionManagerConfiguration.class);
        when(rmc.getRetentionElementName()).thenReturn("ArchiefDocumentsoort");

        //mobjConfiguration.getRetentionClasses()
        IARetentionClass iarc = mock(IARetentionClass.class);
        when(iarc.getName()).thenReturn("Startbrief");
        ArrayList<IARetentionClass> retentionClasses = new ArrayList<IARetentionClass>(Arrays.asList(iarc));
        when(rmc.getRetentionClasses()).thenReturn(retentionClasses);

        IADocument iad = mock(IADocument.class);

        CAKRetentionManager crm = new CAKRetentionManager(rmc);

        assertNotNull(crm.retrieveRetentionClass(iad));

    }

    @Test
    public void retrieveRetentionClassFailure() throws Exception {
        //mobjConfiguration.getRetentionElementName()
        RetentionManagerConfiguration rmc = mock(RetentionManagerConfiguration.class);
        when(rmc.getRetentionElementName()).thenReturn("ArchiefDocumentsoort");

        //mobjConfiguration.getRetentionClasses()
        IARetentionClass iarc = mock(IARetentionClass.class);
        when(iarc.getName()).thenReturn("Startbrief");
        ArrayList<IARetentionClass> retentionClasses = new ArrayList<IARetentionClass>(Arrays.asList(iarc));
        when(rmc.getRetentionClasses()).thenReturn(retentionClasses);

        IADocument iad = mock(IADocument.class);

        CAKRetentionManager crm = new CAKRetentionManager(rmc);

        IARetentionClass retentionClass = null;
        try {
            retentionClass = crm.retrieveRetentionClass(iad);
        }catch(Exception e){

        }
        assertNull(retentionClass);

    }

}