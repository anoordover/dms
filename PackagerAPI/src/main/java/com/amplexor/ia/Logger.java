package com.amplexor.ia;

import org.apache.log4j.Level;

/**
 * Created by admjzimmermann on 21-9-2016.
 */
public class Logger {
    private Logger() {
    }

    public static void info(Object objCaller, Object objMessage) {
        org.apache.log4j.Logger objLogger = org.apache.log4j.Logger.getLogger(objCaller.getClass());
        objLogger.log(Logger.class.getCanonicalName(), Level.INFO, objMessage, null);
    }

    public static void debug(Object objCaller, Object objMessage) {
        org.apache.log4j.Logger objLogger = org.apache.log4j.Logger.getLogger(objCaller.getClass());
        objLogger.log(Logger.class.getCanonicalName(), Level.DEBUG, objMessage, null);
    }

    public static void warn(Object objCaller, Object objMessage) {
        org.apache.log4j.Logger objLogger = org.apache.log4j.Logger.getLogger(objCaller.getClass());
        objLogger.log(Logger.class.getCanonicalName(), Level.WARN, objMessage, null);
    }

    public static void trace(Object objCaller, Object objMessage) {
        org.apache.log4j.Logger objLogger = org.apache.log4j.Logger.getLogger(objCaller.getClass());
        objLogger.log(Logger.class.getCanonicalName(), Level.TRACE, objMessage, null);
    }

    public static void error(Object objCaller, Object objMessage) {
        org.apache.log4j.Logger objLogger = org.apache.log4j.Logger.getLogger(objCaller.getClass());
        objLogger.log(Logger.class.getCanonicalName(), Level.ERROR, objMessage, null);
    }

    public static void fatal(Object objCaller, Object objMessage) {
        org.apache.log4j.Logger objLogger = org.apache.log4j.Logger.getLogger(objCaller.getClass());
        objLogger.log(Logger.class.getCanonicalName(), Level.FATAL, objMessage, null);
    }
}
