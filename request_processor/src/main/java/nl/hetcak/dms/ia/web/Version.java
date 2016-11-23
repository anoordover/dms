package nl.hetcak.dms.ia.web;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * Version for logging.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class Version {
    public static final String PROGRAM_NAME = "Request-Processor";
    private static final int VERSION_MAJOR = 1;
    private static final int VERSION_MINOR = 1;
    private static final int VERSION_REVISION = 0;
    private static final String VERSION_NAME = "RELEASE";

    private Version() {
        //no constructor
    }
    
    
    /**
     * Gets the current version as a {@link String}
     * @return a {@link String} containing the version number.
     */
    public static String currentVersion() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(VERSION_MAJOR);
        stringBuilder.append('.');
        stringBuilder.append(VERSION_MINOR);
        stringBuilder.append('.');
        stringBuilder.append(VERSION_REVISION);
        stringBuilder.append('-');
        stringBuilder.append(VERSION_NAME);
        return stringBuilder.toString();
    }
}
