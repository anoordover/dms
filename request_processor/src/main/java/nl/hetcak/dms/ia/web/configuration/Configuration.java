package nl.hetcak.dms.ia.web.configuration;

import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;
import nl.hetcak.dms.ia.web.comunication.Credentials;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * The setting needed to run this program.
 * If the settings are not met, expect failures.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface Configuration {
    //InfoArchive Configuration
    String getInfoArchiveServerAddress();
    int getInfoArchiveServerPort();
    Credentials getInfoArchiveCredentials();
    ServerConnectionInformation getInfoArchiveServerInformation();
    
    //Application Settings
    int getMaxResults();
    
    //InfoArchive Application Settings
    String getApplicationUUID();
    String getSearchCompositionUUID();
}
