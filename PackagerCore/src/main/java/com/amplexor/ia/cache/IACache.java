package com.amplexor.ia.cache;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.amplexor.ia.Logger.debug;

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

    @XStreamAlias("ingested")
    private boolean mbIngested;

    @XStreamAlias("created")
    private long mlCreated;

    @XStreamAlias("retention_class")
    private IARetentionClass mobjRetentionClass;

    @XStreamAlias("sip_file")
    private String msSipFile;

    @XStreamAlias("document_identifier")
    private String msDocumentIdentifier;

    @XStreamAlias("target_application")
    private String msTargetApplication;

    @XStreamOmitField
    private List<IADocumentReference> mcContents;

    public IACache(int iId, IARetentionClass objRetentionClass) {
        miId = iId;
        mobjRetentionClass = objRetentionClass;
        mbClosed = false;
        mlCreated = System.currentTimeMillis();
    }

    public void add(IADocumentReference objReference) {
        if(mcContents == null) {
            mcContents = new ArrayList<>();
        }
        mcContents.add(objReference);
    }

    public boolean isClosed() {
        return mbClosed;
    }

    public void close() {
        mbClosed = true;
        debug(this, "IACache-" + miId + " Closed");
    }

    public long getCreated() {
        return mlCreated;
    }

    public int getSize() {
        if(mcContents == null) {
            mcContents = new ArrayList<>();
        }
        return mcContents.size();
    }

    public int getId() {
        return miId;
    }

    public IARetentionClass getRetentionClass() {
        return mobjRetentionClass;
    }

    public List<IADocumentReference> getContents() {
        if(mcContents == null) {
            mcContents = new ArrayList<>();
        }
        return Collections.unmodifiableList(mcContents);
    }

    public void setSipFile(String sSipFile) {
        msSipFile = sSipFile;
    }

    public Path getSipFile() {
        if(msSipFile == null) return null;

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

    public boolean isIngested() {
        return mbIngested;
    }

    public void setIngested() {
        mbIngested = true;
    }
}
