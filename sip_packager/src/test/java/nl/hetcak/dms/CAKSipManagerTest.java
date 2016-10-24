package nl.hetcak.dms;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class CAKSipManagerTest {

    @Test
    public void getCAKPackageInformation() throws Exception {
        IARetentionClass irc = mock(IARetentionClass.class);
        when(irc.getName()).thenReturn("Startbrief");

        IASipConfiguration isc = mock(IASipConfiguration.class);
        CAKSipManager csm = new CAKSipManager(isc);

        assertNotNull(csm.getCAKPackageInformation(irc, false));
    }

    @Test
    public void getSipFileIACache(){
        IACache ic = new IACache(1, new CAKRetentionClass("Test"));
        ic.setTargetApplication("CAK_Klantarchief");
        CAKDocument objDocument = new CAKDocument();
        objDocument.setPayload(Base64.getEncoder().encode("abcdef".getBytes()));
        ic.add(new IADocumentReference(objDocument, null));

        IASipConfiguration isc = mock(IASipConfiguration.class);
        when(isc.getParameter("fallback_application_name")).thenReturn("CAK_Tijdelijk_Klantarchief");
        when(isc.getParameter("document_class")).thenReturn("nl.hetcak.dms.CAKDocument");
        when(isc.getApplicationName()).thenReturn("CAK_Klantarchief");
        when(isc.getHoldingName()).thenReturn("CAK_Klantarchief");
        when(isc.getSchemaDeclaration()).thenReturn("urn:hetcak:dms:uitingarchief:2016:08");
        when(isc.getDocumentElementName()).thenReturn("ArchiefDocumenten");
        when(isc.getEntityName()).thenReturn("ArchiefDocument");
        when(isc.getSipOutputDirectory()).thenReturn("Sips");

        CAKSipManager csm = new CAKSipManager(isc);
        assertTrue(csm.getSIPFile(ic));
    }

    @Ignore
    public void getSipFileIAdocumentref(){
        //// TODO: 7-10-2016 opnemen met Joury
    }



}