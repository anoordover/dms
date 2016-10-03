package com.amplexor.ia.metadata;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.Set;

/**
 * Abstract class which should be subclassed by the Client implementation of the SIP Packager
 * This class will be used to store a documents metadata and content data
 * Created by admjzimmermann on 6-9-2016.
 */
public abstract class IADocument {
    @XStreamOmitField
    private String msDocumentId;

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

    public abstract byte[] loadContent(String sKey);

    public abstract void setContent(String sKey, byte[] cContent);

    public abstract void setError(String sError);

    public abstract String getError();

}
