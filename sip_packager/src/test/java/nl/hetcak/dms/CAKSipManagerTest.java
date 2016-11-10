package nl.hetcak.dms;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import org.junit.Before;
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
    ConfigManager mobjConfigManager;

    @Before
    public void setup() {
        System.out.println(System.getProperty("user.dir"));
        mobjConfigManager = new ConfigManager("IAArchiver.xml");
        mobjConfigManager.loadConfiguration();
    }

    @Test
    public void getCAKPackageInformation() throws Exception {
        IARetentionClass irc = mock(IARetentionClass.class);
        when(irc.getName()).thenReturn("B01");

        CAKSipManager csm = new CAKSipManager(mobjConfigManager.getConfiguration().getSipConfiguration());

        assertNotNull(csm.getCAKPackageInformation(irc, false));
    }

    @Test
    public void getSipFileIACache() throws Exception {
        CAKCacheManager objCacheManager = new CAKCacheManager(mobjConfigManager.getConfiguration().getCacheConfiguration());
        objCacheManager.initializeCache();
        CAKDocument objDocument = new CAKDocument();
        objDocument.setDocumentId("1000100010");
        objDocument.setContent(CAKDocument.KEY_ATTACHMENT, Base64.getEncoder().encode("abcdef".getBytes()));
        objCacheManager.add(objDocument, new CAKRetentionClass("Z01"));
        objCacheManager.update();

        CAKSipManager csm = new CAKSipManager(mobjConfigManager.getConfiguration().getSipConfiguration());
        assertTrue(csm.getSIPFile(objCacheManager.getClosedCaches().get(0)));
    }

    @Ignore
    public void getSipFileIAdocumentref() {
        //// TODO: 7-10-2016 opnemen met Joury
    }


}