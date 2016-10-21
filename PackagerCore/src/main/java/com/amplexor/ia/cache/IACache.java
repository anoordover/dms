package com.amplexor.ia.cache;

import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@link IACache} object contains a number of documents as well as a reference to a {@link IARetentionClass} object associated with the documents in this cache.
 * It also contains data pertaining to the time at which the cache was created and the SIP file that was generated for this cache.
 * Created by admjzimmermann on 6-9-2016.
 */
public class IACache {
    @XStreamAlias("id")
    private int miId;

    @XStreamAlias("closed")
    private boolean mbClosed;

    @XStreamAlias("created")
    private long mlCreated;

    @XStreamAlias("retention_class")
    private IARetentionClass mobjRetentionClass;

    @XStreamAlias("contents")
    private List<IADocumentReference> mcContents;

    @XStreamAlias("sip_file")
    private String msSipFile;

    @XStreamAlias("document_identifier")
    private String msDocumentIdentifier;

    @XStreamAlias("document_element_name")
    private String msDocumentElementName;

    @XStreamAlias("document_class")
    private String msDocumentClass;

    @XStreamAlias("target_application")
    private String msTargetApplication;

    public IACache(int iId, IARetentionClass objRetentionClass) {
        miId = iId;
        mobjRetentionClass = objRetentionClass;
        mbClosed = false;
        mlCreated = System.currentTimeMillis();
        mcContents = new ArrayList<>();
    }

    public void add(IADocumentReference objReference) {
        mcContents.add(objReference);
    }

    public boolean isClosed() {
        return mbClosed;
    }

    public void close() {
        mbClosed = true;
    }

    public long getCreated() {
        return mlCreated;
    }

    public int getSize() {
        return mcContents.size();
    }

    public int getId() {
        return miId;
    }

    public IARetentionClass getRetentionClass() {
        return mobjRetentionClass;
    }

    public List<IADocumentReference> getContents() {
        return Collections.unmodifiableList(mcContents);
    }

    public void setSipFile(String sSipFile) {
        msSipFile = sSipFile;
    }

    public Path getSipFile() {
        return Paths.get(msSipFile);
    }

    public String getDocumentIdentifier() {
        return msDocumentIdentifier;
    }

    public void setDocumentIdentifier(String sDocumentIdentifier) {
        msDocumentIdentifier = sDocumentIdentifier;
    }

    public void setTargetApplication(String sTargetApplication) {
        msTargetApplication = sTargetApplication;
    }

    public String getTargetApplication() {return msTargetApplication;}
}