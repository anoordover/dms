package com.amplexor.ia.exception;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.ExceptionConfiguration;
import com.amplexor.ia.metadata.IADocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.amplexor.ia.Logger.*;

/**
 * Created by zimmermannj on 9/23/2016.
 */
public class ExceptionHelper {
    /*
        Java's Classloader will ensure this class is loaded once and only once, relieving us of implementing a thread-safe ExceptionHelper::getExceptionHelper method
     */
    private static class Loader {
        static ExceptionHelper objInstance = new ExceptionHelper();
    }

    private ExceptionConfiguration mobjExceptionConfiguration;

    private ExceptionHelper() {
    }

    public static ExceptionHelper getExceptionHelper() {
        return Loader.objInstance;
    }

    public synchronized void setExceptionConfiguration(ExceptionConfiguration objExceptionConfiguration) {
        mobjExceptionConfiguration = objExceptionConfiguration;
    }

    public synchronized void handleException(int iCode) {
        AMPError objError = mobjExceptionConfiguration.getError(iCode);
        executeHandlers(objError, null, null);
    }

    public synchronized void handleException(int iCode, IADocument objDocument, DocumentSource objDocumentSource) {
        AMPError objError = mobjExceptionConfiguration.getError(iCode);
        objDocument.setError(objError.getErrorText());
        executeHandlers(objError, objDocument, objDocumentSource);

    }

    private void executeHandlers(AMPError objError, IADocument objDocument, DocumentSource objDocumentSource) {
        List<String> cHandlers =  Arrays.asList(objError.getErrorHandling().split(";"));
        cHandlers.forEach(sHandler -> {
            switch (sHandler) {
                case "notify_source":
                    if (objDocumentSource != null && objDocument != null) {
                        objDocumentSource.postResult(objDocument);
                    }
                    else {
                        error(this, "DocumentSource or IADocument was not provided to error handler");
                    }
                    break;
                case "log_error":
                    error(this, objError.getErrorText());
                    break;
                case "log_fatal":
                    fatal(this, objError.getErrorText());
                    System.exit(objError.getErrorCode());
                default:
                    error(this, "Invalid error handler " + sHandler);
                    break;
            }
        });
    }
}
