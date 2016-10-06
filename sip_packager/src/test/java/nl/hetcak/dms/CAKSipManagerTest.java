package nl.hetcak.dms;

import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import org.junit.Test;

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
    public void getPackageInformationWithIADocument() throws Exception {
        Set<String> keys = new HashSet<>();
        keys.add("ArchiefPersoonsnummer");
        IADocument iad = mock(IADocument.class);
        when(iad.getMetadataKeys()).thenReturn(keys);
        IARetentionClass irc = mock(IARetentionClass.class);
        when(irc.getName()).thenReturn("Startbrief");
        IASipConfiguration isc = mock(IASipConfiguration.class);
        CAKSipManager csm = new CAKSipManager(isc);

        //// TODO: 4-10-2016 change test assertion
//        assertNotNull(csm.getPackageInformation(iad, irc));


    }

    @Test
    public void getPackageInformationWithoutIADocument() throws Exception {
        IADocument iad = mock(IADocument.class);
        IARetentionClass irc = mock(IARetentionClass.class);
        when(irc.getName()).thenReturn("Startbrief");

        IASipConfiguration isc = mock(IASipConfiguration.class);
        CAKSipManager csm = new CAKSipManager(isc);

        //// TODO: 4-10-2016 change test assertion
//        assertNotNull(csm.getPackageInformation(iad, irc));
    }

}