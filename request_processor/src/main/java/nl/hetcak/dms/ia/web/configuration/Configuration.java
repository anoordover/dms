package nl.hetcak.dms.ia.web.configuration;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 * <p>
 * The setting needed to run this program.
 * If the settings are not met, expect failures.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public interface Configuration {
    //InfoArchive Configuration

    /**
     * Gets the server address.
     *
     * @return the server address.
     * @deprecated see {@link Configuration#getInfoArchiveServerInformation()}.
     */
    @Deprecated
    String getInfoArchiveServerAddress();

    /**
     * Gets the server port.
     *
     * @return the server port.
     * @deprecated see {@link Configuration#getInfoArchiveServerInformation()}.
     */
    @Deprecated
    int getInfoArchiveServerPort();

    /**
     * Gets the InfoArchive Credentials.
     *
     * @return
     */
    Credentials getInfoArchiveCredentials();

    /**
     * Get the sever connection information.
     *
     * @return the server connection information.
     */
    ServerConnectionInformation getInfoArchiveServerInformation();

    //Application Settings

    /**
     * Get the result limit.
     *
     * @return the maximum results.
     */
    int getMaxResults();

    //InfoArchive Application Settings

    /**
     * The Application UUID that should be used during a InfoArchive request.
     *
     * @return the Application UUID
     */
    String getApplicationUUID();

    /**
     * The Search Composition UUID that should be used during a InfoArchive request.
     *
     * @return the Search Composition UUID
     */
    String getSearchCompositionUUID();

    /**
     * Checks if this class has the basic settings set.
     *
     * @return true, if this class has the basic settings set.
     */
    boolean hasBasicInformation();

}
