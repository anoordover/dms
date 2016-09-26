package com.amplexor.ia.exception;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ExceptionConfiguration;
import com.amplexor.ia.metadata.IADocument;

import java.util.Arrays;
import java.util.List;

import static com.amplexor.ia.Logger.error;
import static com.amplexor.ia.Logger.fatal;

/**
 * Created by zimmermannj on 9/23/2016.
 */
public class ExceptionHelper {
    public static final int ERROR_INIT_INVALID_CONFIGURATION = 1001;
    public static final int ERROR_INIT_INVALID_LOG4J_PROPERTIES = 1002;
    public static final int ERROR_INIT_INSUFFICIENT_PRIVELEGES = 1003;

    public static final int ERROR_CACHE_INVALID_BASE_PATH = 2001;
    public static final int ERROR_CACHE_INSUFFICIENT_PRIVELEGES = 2002;
    public static final int ERROR_CACHE_DELETION_FAILURE = 2003;

    public static final int ERROR_SOURCE_UNABLE_TO_CONNECT = 3001;
    public static final int ERROR_SOURCE_AUTHENTICATION_FAILURE = 3002;
    public static final int ERROR_SOURCE_INVALID_TRUSTSTORE = 3003;
    public static final int ERROR_SOURCE_INVALID_PASSWORD = 3004;
    public static final int ERROR_SOURCE_INVALID_INPUT = 3005;
    public static final int ERROR_SOURCE_INVALID_FILE_TYPE = 3006;
    public static final int ERROR_SOURCE_INVALID_ENCODING = 3007;
    public static final int ERROR_SOURCE_UNKNOWN_RETENTION = 3008;
    public static final int ERROR_SOURCE_STORAGE_FAILED = 3009;

    public static final int ERROR_CACHE_INSUFFICIENT_DISK_SPACE = 4001;

    public static final int ERROR_SIP_INSUFFICIENT_DISK_SPACE = 5001;
    public static final int ERROR_SIP_INVALID_METADATA = 5002;
    public static final int ERROR_SIP_TRANSFORMATION_FAILED = 5003;

    public static final int ERROR_INGEST_INVALID_IA_CREDENTIALS = 6001;
    public static final int ERROR_INGEST_INSUFFICIENT_PERMISSIONS = 6002;
    public static final int ERROR_INGEST_ERROR_UPLOADING_CONTENT = 6003;
    public static final int ERROR_INGEST_INGESTION_ERROR = 6004;

    public static final int ERROR_CALLBACK_UNABLE_TO_CONNECT = 7001;
    public static final int ERROR_CALLBACK_AUTHENTICATION_FAILURE = 7002;

    public static final int ERROR_OTHER = -1;

    /* Each Worker(Thread) will have it's own ExceptionHelper */
    private static final ThreadLocal<ExceptionHelper> mobjLocalInstance = new ThreadLocal<ExceptionHelper>() {
        @Override
        protected ExceptionHelper initialValue() {
            return new ExceptionHelper();
        }
    };

    private ExceptionConfiguration mobjExceptionConfiguration;
    private DocumentSource mobjDocumentSource;

    private ExceptionHelper() {
    }

    public static ExceptionHelper getExceptionHelper() {
        return mobjLocalInstance.get();
    }

    public synchronized void setDocumentSource(DocumentSource objDocumentSource) {
        mobjDocumentSource = objDocumentSource;
    }

    public synchronized void setExceptionConfiguration(ExceptionConfiguration objExceptionConfiguration) {
        mobjExceptionConfiguration = objExceptionConfiguration;
    }

    public synchronized void handleException(int iCode, Exception objException) {
        AMPError objError = mobjExceptionConfiguration.getError(iCode);
        executeHandlers(objError, (IADocument)null, objException);
    }

    public synchronized void handleException(int iCode, IADocument objDocument, Exception objException) {
        AMPError objError = mobjExceptionConfiguration.getError(iCode);
        objDocument.setError(objError.getErrorText());
        executeHandlers(objError, objDocument, objException);
    }

    public synchronized void handleException(int iCode, IACache objCache, Exception objException) {
        AMPError objError = mobjExceptionConfiguration.getError(iCode);
        for (IADocument objDocument : objCache.getContents()) {
            objDocument.setError(objError.getErrorText());
        }
        executeHandlers(objError, objCache, objException);
    }

    private void executeHandlers(AMPError objError, IACache objCache, Exception objException) {
        List<String> cHandlers = Arrays.asList(objError.getErrorHandling().split(";"));
        cHandlers.forEach(sHandler -> {
            if ("notify_source".equals(sHandler)) {
                if (mobjDocumentSource != null && objCache != null) {
                    mobjDocumentSource.postResult(objCache);
                } else {
                    error(this, "DocumentSource or IADocument was not provided to error handler");
                }
            } else if ("log_error".equals(sHandler)) {
                error(this, objError.getErrorText());
                error(this, objException);
            } else if ("log_fatal".equals(sHandler)) {
                if (objError.getErrorCode() < 2000) {
                    //Assume Logger is not available
                    System.err.println(objError.getErrorText());
                    System.err.println(objException.getLocalizedMessage());
                    System.exit(objError.getErrorCode());
                } else {
                    fatal(this, objError.getErrorText());
                    fatal(this, objException);
                    System.exit(objError.getErrorCode());
                }
            } else {
                error(this, "Invalid error handler " + sHandler);
                error(this, objException);
            }
        });
    }

    private void executeHandlers(AMPError objError, IADocument objDocument, Exception objException) {
        List<String> cHandlers = Arrays.asList(objError.getErrorHandling().split(";"));
        cHandlers.forEach(sHandler -> {
            if ("notify_source".equals(sHandler)) {
                if (mobjDocumentSource != null && objDocument != null) {
                    mobjDocumentSource.postResult(objDocument);
                } else {
                    error(this, "DocumentSource or IADocument was not provided to error handler");
                }
            } else if ("log_error".equals(sHandler)) {
                error(this, objError.getErrorText());
                error(this, objException);
            } else if ("log_fatal".equals(sHandler)) {
                if (objError.getErrorCode() < 2000) {
                    //Assume Logger is not available
                    System.err.println(objError.getErrorText());
                    System.err.println(objException.getLocalizedMessage());
                    System.exit(objError.getErrorCode());
                } else {
                    fatal(this, objError.getErrorText());
                    fatal(this, objException);
                    System.exit(objError.getErrorCode());
                }
            } else {
                error(this, "Invalid error handler " + sHandler);
                error(this, objException);
            }
        });
    }
}
