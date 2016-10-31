package com.amplexor.ia.cache;

import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.*;

/**
 * Created by admjzimmermann on 5-10-2016.
 */
public class IADocumentReference {
    private String msDocumentId;
    private String msFile;
    private String msErrorMessage;
    private int miErrorCode;


    public IADocumentReference(String sDocumentId, String sFile) {
        msDocumentId = sDocumentId;
        msFile = sFile;
        msErrorMessage = null;
        miErrorCode = 0;
    }

    public IADocumentReference(IADocument objDocument, String sFile) {
        msDocumentId = objDocument.getDocumentId();
        msFile = sFile;
        msErrorMessage = objDocument.getErrorText();
        miErrorCode = objDocument.getErrorCode();
    }

    public String getDocumentId() {
        return msDocumentId;
    }

    public String getFile() {
        return msFile;
    }

    public String getErrorMessage() {
        return msErrorMessage;
    }

    public void setErrorMessage(String sErrorMessage) {
        msErrorMessage = sErrorMessage;
    }

    public int getErrorCode() {
        return miErrorCode;
    }

    public void setErrorCode(int iErrorCode) {
        miErrorCode = iErrorCode;
    }

    public IADocument getDocumentData(Class<?> objDocumentClass, String sAlias) {
        IADocument objDocument = null;
        if (msFile == null) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER,
                    new Exception("Document reference not associated with a file"));
        }
        try (InputStream objInput = new FileInputStream(new File(msFile))) {
            XStream objXStream = new XStream(new StaxDriver());
            objXStream.alias(sAlias, objDocumentClass);
            objXStream.processAnnotations(objDocumentClass);
            Object objDocumentData = objDocumentClass.cast(objXStream.fromXML(objInput));
            if (objDocumentData instanceof IADocument) {
                objDocument = (IADocument) objDocumentClass.cast(objDocumentData);
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return objDocument;
    }
}
