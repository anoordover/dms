package com.amplexor.ia.configuration;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.Map;

/**
 * POJO for holding configuration pertaining to "Pluggable" classes, these classes will be loaded during runtime provided they are available on the JVMs classpath
 * Created by admjzimmermann on 6-9-2016.
 */
public class PluggableObjectConfiguration {
    @XStreamAlias("implementing_class")
    private String msImplementingClass;

    @XStreamAlias("custom_parameters")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, Object> mcCustomParameters;

    public String getImplementingClass() {
        return msImplementingClass;
    }

    public String getParameter(String sKey) {
        return (String) mcCustomParameters.get(sKey);
    }

    public Map<String, Object> getSubset(String sKey) {
        return (Map<String, Object>) mcCustomParameters.get(sKey);
    }
}
