package com.amplexor.ia.configuration;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Collections;
import java.util.List;

/**
 * POJO for holding configuration pertaining to the RetentionManager
 * Created by admjzimmermann on 7-9-2016.
 */
public class RetentionManagerConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("retention_classes")
    private List<IARetentionClass> mcRetentionClasses;

    @XStreamAlias("retention_element_name")
    private String msRetentionElementName;

    public List<IARetentionClass> getRetentionClasses() {
        return Collections.unmodifiableList(mcRetentionClasses);
    }

    public String getRetentionElementName() {
        return msRetentionElementName;
    }
}
