package com.amplexor.ia.exception;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ExceptionConfiguration;
import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.metadata.IADocument;

import java.util.Arrays;
import java.util.List;

import static com.amplexor.ia.Logger.error;
import static com.amplexor.ia.Logger.fatal;

/**
 * Helper class for handling errors that occur within the SIP Packager, Note that this class is Thread Local,
 * meaning that every thread will have its own instance of the ExceptionHelper
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

    /*Private constructor to hide the implicit public one*/
    private ExceptionHelper() {
    }

    /**
     * Gets the instance of the {@link ExceptionHelper} for the current thread
     *
     * @return
     */
    public static ExceptionHelper getExceptionHelper() {
        return mobjLocalInstance.get();
    }

    /**
     * Sets the {@link DocumentSource} to be used for the "notify_source" handler
     *
     * @param objDocumentSource
     */
    public synchronized void setDocumentSource(DocumentSource objDocumentSource) {
        mobjDocumentSource = objDocumentSource;
    }

    /**
     * Set the {@link ExceptionConfiguration} to be used by this instance of the ExceptionHelper
     *
     * @param objExceptionConfiguration
     */
    public synchronized void setExceptionConfiguration(ExceptionConfiguration objExceptionConfiguration) {
        mobjExceptionConfiguration = objExceptionConfiguration;
    }

    /**
     * Handle the exception with code iCode
     *
     * @param iCode        The code for this error
     * @param objException The exception associated with this error code
     */
    public synchronized void handleException(int iCode, Exception objException) {
        if (mobjExceptionConfiguration != null) {
            AMPError objError = mobjExceptionConfiguration.getError(iCode);
            executeHandlers(objError, (IADocument) null, objException);
        } else {
            fatal(this, "No ExceptionConfiguration, Exiting");
            System.exit(ERROR_OTHER);
        }
    }

    /**
     * Handle the exception with code iCode and IADocument objDocument
     *
     * @param iCode        The code for this error
     * @param objDocument  The IADocument whose error should be set (For use by {@link DocumentSource}.postResult({@link IADocument})_
     * @param objException The exception associated with this error code
     */
    public synchronized void handleException(int iCode, IADocument objDocument, Exception objException) {
        if (mobjExceptionConfiguration != null) {
            AMPError objError = mobjExceptionConfiguration.getError(iCode);
            objDocument.setError(objError.getErrorText());
            executeHandlers(objError, objDocument, objException);
        } else {
            fatal(this, "No ExceptionConfiguration, Exiting");
            System.exit(ERROR_OTHER);
        }
    }

    /**
     * Handle the exception with code iCode and IADocument objDocument
     *
     * @param iCode        The code for this error
     * @param objCache     The IACache whose error should be set (For use by {@link DocumentSource}.postResult({@link IACache})_
     * @param objException The exception associated with this error code
     */
    public synchronized void handleException(int iCode, IACache objCache, Exception objException) {
        AMPError objError = mobjExceptionConfiguration.getError(iCode);
        for (IADocument objDocument : objCache.getContents()) {
            objDocument.setError(objError.getErrorText());
        }
        executeHandlers(objError, objCache, objException);
    }

    /**
     * Executes the handlers associated with {@link AMPError} objError
     *
     * @param objError     The error whose handlers should be invoked
     * @param objIAItem     The cache associated with the error
     * @param objException The exception associated with this error
     */
    private void executeHandlers(AMPError objError, Object objIAItem, Exception objException) {
        List<String> cHandlers = Arrays.asList(objError.getErrorHandling().split(";"));
        for (String sHandler : cHandlers) {
            if ("notify_source".equals(sHandler)) {
                if(mobjDocumentSource == null || objIAItem == null) {
                    error(ExceptionHelper.this, "DocumentSource or IADocument was not provided to error handler");
                    return;
                }

                if(objIAItem instanceof IACache) {
                    mobjDocumentSource.postResult((IACache)objIAItem);
                } else if (objIAItem instanceof IADocument) {
                    mobjDocumentSource.postResult((IADocument)objIAItem);
                }

            } else if ("log_error".equals(sHandler)) {
                error(ExceptionHelper.this, objError.getErrorText());
                error(ExceptionHelper.this, objException);
            } else if ("log_fatal".equals(sHandler)) {
                fatal(ExceptionHelper.this, objError.getErrorText());
                fatal(ExceptionHelper.this, objException);
                System.exit(objError.getErrorCode());
            } else {
                error(ExceptionHelper.this, "Invalid error handler " + sHandler);
                error(ExceptionHelper.this, objException);
            }
        }
    }
}
