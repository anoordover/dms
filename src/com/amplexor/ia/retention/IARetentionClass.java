package com.amplexor.ia.retention;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public abstract class IARetentionClass {
    @XStreamAlias("name")
    private String name;

    public IARetentionClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
