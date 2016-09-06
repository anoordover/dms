package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class PluggableObjectConfiguration {
    @XStreamAlias("implementing_class")
    private String implementingClass;

    @XStreamAlias("custom_parameters")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> customParameters;

    public String getImplementingClass() {
        return implementingClass;
    }

    public String getParameter(String key) {
        return customParameters.get(key);
    }
}
