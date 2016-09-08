package com.amplexor.ia.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;
import java.util.Set;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public abstract class IADocument {
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public abstract Set<String> getMetadataKeys();
    public abstract String getMetadata(String key);
    public abstract void setMetadata(String key, String value);

    public abstract Set<String> getContentKeys();
    public abstract byte[] loadContent(String key);

 }
