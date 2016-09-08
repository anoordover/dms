package com.amplexor.ia.cache;

import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IACache {
    @XStreamAlias("id")
    private int id;

    @XStreamAlias("closed")
    private boolean closed;

    @XStreamAlias("created")
    private long created;

    @XStreamAlias("size")
    private int size;

    public IACache(int id) {
        this.id = id;
        this.closed = false;
        this.size = 0;
        this.created = System.currentTimeMillis();
    }

    public void add(IADocument document) {
        size++;
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
}
