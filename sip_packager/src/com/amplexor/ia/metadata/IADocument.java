package com.amplexor.ia.metadata;

import java.util.Set;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public abstract class IADocument {
    private String msDocumentId;

    public String getDocumentId() {
        return msDocumentId;
    }

    public abstract Set<String> getMetadataKeys();
    public abstract String getMetadata(String sKey);
    public abstract void setMetadata(String sKey, String sValue);

    public abstract Set<String> getContentKeys();
    public abstract byte[] loadContent(String sKey);

 }
