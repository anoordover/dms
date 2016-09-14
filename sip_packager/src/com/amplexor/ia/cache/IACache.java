package com.amplexor.ia.cache;

import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IACache {
    @XStreamOmitField
    private String basePath;

    @XStreamAlias("id")
    private int id;

    @XStreamAlias("closed")
    private boolean closed;

    @XStreamAlias("created")
    private long created;

    @XStreamAlias("size")
    private int size;

    @XStreamAlias("retention_class")
    private IARetentionClass retentionClass;

    @XStreamAlias("contents")
    private List<IADocument> contents;

    public IACache(int id, IARetentionClass retentionClass) {
        this.id = id;
        this.retentionClass = retentionClass;
        this.closed = false;
        this.size = 0;
        this.created = System.currentTimeMillis();
        this.contents = new ArrayList<>();
    }

    public void add(IADocument document) {
        contents.add(document);
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        closed = true;
    }

    public long getCreated() {
        return created;
    }

    public int getSize() {
        return size;
    }

    public int getId() {
        return id;
    }

    public IARetentionClass getRetentionClass() {
        return retentionClass;
    }

    public List<IADocument> getContents() {
        return Collections.unmodifiableList(contents);
    }
}
