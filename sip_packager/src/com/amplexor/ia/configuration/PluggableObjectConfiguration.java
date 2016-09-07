package com.amplexor.ia.configuration;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.Map;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class PluggableObjectConfiguration {
    @XStreamAlias("implementing_class")
    private String implementingClass;

    @XStreamAlias("custom_parameters")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, Object> customParameters;

    public String getImplementingClass() {
        return implementingClass;
    }

    public String getParameter(String key) {
        return (String)customParameters.get(key);
    }

    public Map<String, Object> getSubset(String key) {
        return (Map<String, Object>)customParameters.get(key);
    }
}
