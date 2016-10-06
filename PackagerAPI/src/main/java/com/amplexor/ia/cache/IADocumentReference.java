package com.amplexor.ia.cache;

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
}
