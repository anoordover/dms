package com.amplexor.ia.retention;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * POJO for holding information pertaining to the retention classes available within the InfoArchive environment
 * Created by admjzimmermann on 6-9-2016.
 */
public abstract class IARetentionClass {
    @XStreamAlias("name")
    private String msName;

    @XStreamAlias("policy")
    private String msPolicy;

    public IARetentionClass(String sName) {
        this.msName = sName;
    }

    public String getName() {
        return msName;
    }

    public String getPolicy() {
        return msPolicy;
    }

}
