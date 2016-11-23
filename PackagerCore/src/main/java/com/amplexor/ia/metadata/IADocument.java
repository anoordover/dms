package com.amplexor.ia.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.Set;

/**
 * Abstract class which should be subclassed by the Client implementation of the SIP Packager
 * This class will be used to store a document's metadata and content data
 * Created by admjzimmermann on 6-9-2016.
 */
public abstract class IADocument {
    @XStreamOmitField
    private String msDocumentId;

    @XStreamAlias("error_text")
    private String msErrorText;

    @XStreamAlias("error_code")
    private int miErrorCode;

    @XStreamAlias("aiu_data")
    private String msAIU;

    public IADocument() {
        miErrorCode = 0;
    }

    public String getDocumentId() {
        return msDocumentId;
    }

    public void setDocumentId(String sDocumentId) {
        msDocumentId = sDocumentId;
    }

    public abstract Set<String> getMetadataKeys();

    public abstract String getMetadata(String sKey);

    public abstract void setMetadata(String sKey, String sValue);

    public abstract Set<String> getContentKeys();

    public abstract long getSizeEstimate();

    public abstract void loadContent(String sKey);

    public abstract byte[] getContent(String sKey);

    public abstract void setContent(String sKey, byte[] cContent);
 
    public abstract void setContentFile(String sKey, String sContentFile);

    public void setErrorText(String sError) {
        msErrorText = sError;
    }

    public String getErrorText() {
        return msErrorText;
    }

    public void setErrorCode(int iCode) {
        miErrorCode = iCode;
    }

    public int getErrorCode() {
        return miErrorCode;
    }

    public void setAIU(String sAIU) {
        msAIU = sAIU;
    }

    public String getAIU() {
        return msAIU;
    }
}
