package nl.hetcak.dms;

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
    RetentionManagerConfiguration mobjConfiguration;

    @Before
    public void setup() {
        ArrayList<IARetentionClass> cRetentionClasses = new ArrayList<>();
        cRetentionClasses.add(new CAKRetentionClass("Z01"));

        mobjConfiguration = mock(RetentionManagerConfiguration.class);
        when(mobjConfiguration.getParameter("retention_element_name")).thenReturn("ArchiefDocumenttitel");

    }

    @Test
    public void retrieveRetentionClassSuccess() throws Exception {
        RetentionManagerConfiguration rmc = mock(RetentionManagerConfiguration.class);
        when(rmc.getParameter("retention_element_name")).thenReturn("ArchiefDocumenttitel");
        IARetentionClass iarc = mock(IARetentionClass.class);
        when(iarc.getName()).thenReturn("Z01");
        ArrayList<IARetentionClass> retentionClasses = new ArrayList<IARetentionClass>(Arrays.asList(iarc));
        when(rmc.getRetentionClasses()).thenReturn(retentionClasses);

        CAKDocument objDocument = new CAKDocument();
        objDocument.setDocumentId("1100011000");
        objDocument.setMetadata("ArchiefDocumentid", "1100011000");
        objDocument.setMetadata("ArchiefDocumenttitel", "Z01");


        CAKRetentionManager crm = new CAKRetentionManager(rmc);
        assertNotNull(crm.retrieveRetentionClass(objDocument));

    }

    @Test
    public void retrieveRetentionClassFailure() throws Exception {
        //mobjConfiguration.getRetentionElementName()
        RetentionManagerConfiguration rmc = mock(RetentionManagerConfiguration.class);
        when(rmc.getRetentionElementName()).thenReturn("ArchiefDocumenttitel");
        //mobjConfiguration.getRetentionClasses()
        IARetentionClass iarc = mock(IARetentionClass.class);
        when(iarc.getName()).thenReturn("Z01");
        ArrayList<IARetentionClass> retentionClasses = new ArrayList<IARetentionClass>(Arrays.asList(iarc));
        when(rmc.getRetentionClasses()).thenReturn(retentionClasses);

        IADocument iad = mock(IADocument.class);
        when(iad.getMetadata("ArchiefDocumenttitel")).thenReturn("Z01");

        CAKRetentionManager crm = new CAKRetentionManager(rmc);

        IARetentionClass retentionClass = null;
        try {
            retentionClass = crm.retrieveRetentionClass(iad);
        }catch(Exception e){

        }
        assertNull(retentionClass);

    }

}