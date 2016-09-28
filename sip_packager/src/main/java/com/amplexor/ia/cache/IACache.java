package com.amplexor.ia.cache;

import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.amplexor.ia.Logger.*;

/**
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
    private List<IADocument> mcContents;

    @XStreamAlias("sip_file")
    private String msSipFile;

    public IACache(int iId, IARetentionClass objRetentionClass) {
        miId = iId;
        mobjRetentionClass = objRetentionClass;
        mbClosed = false;
        mlCreated = System.currentTimeMillis();
        mcContents = new ArrayList<>();
    }

    public void add(IADocument objDocument) {
        mcContents.add(objDocument);
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

    public List<IADocument> getContents() {
        return Collections.unmodifiableList(mcContents);
    }

    public void setSipFile(String sSipFile) {
        msSipFile = sSipFile;
    }

    public Path getSipFile() {
        return Paths.get(msSipFile);
    }
}
