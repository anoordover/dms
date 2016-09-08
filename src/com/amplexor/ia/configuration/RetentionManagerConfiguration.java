package com.amplexor.ia.configuration;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.Collections;
import java.util.List;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class RetentionManagerConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("retention_classes")
    private List<IARetentionClass> retentionClasses;

    public List<IARetentionClass> getRetentionClasses() {
        return Collections.unmodifiableList(retentionClasses);
    }
}
