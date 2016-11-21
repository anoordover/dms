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

    private Version() {
        //no constructor
    }

    public static String currentVersion() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(VERSION_MAJOR);
        stringBuilder.append('.');
        stringBuilder.append(VERSION_MINOR);
        stringBuilder.append('.');
        stringBuilder.append(VERSION_REVISION);
        return stringBuilder.toString();
    }
}
