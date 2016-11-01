package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.sip.AMPSipManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 7-10-2016.
 */
public class AMPSipManagerTest {
    ConfigManager mobjConfigManager;

    @Before
    public void setup() {
        System.out.println(System.getProperty("user.dir"));
        mobjConfigManager = new ConfigManager("IAArchiver.xml");
        mobjConfigManager.loadConfiguration();
    }

    @Test
    public void getSipFileIACache(){
        IACache ic = mock(IACache.class);
        //change null to sipfile
        when(ic.getSipFile()).thenReturn(null);
        when(ic.getTargetApplication()).thenReturn("CAK_Klantarchief");

        IASipConfiguration isc = mock(IASipConfiguration.class);
        when(isc.getParameter("fallback_application_name")).thenReturn("CAK_Tijdelijk_Klantarchief");
        when(isc.getApplicationName()).thenReturn("CAK_Klantarchief");
        when(isc.getSipOutputDirectory()).thenReturn("Sips");

        AMPSipManager csm = new AMPSipManager(isc);
//        assertTrue(csm.getSIPFile(ic));


    }

    @Ignore
    public void getSipFileIAdocumentref(){
        //// TODO: 7-10-2016 opnemen met Joury
    }

}