package com.amplexor.ia.configuration;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.Map;

/**
 * Created by admjzimmermann on 18-10-2016.
 */
public class MessageParserConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("document_id_element_name")
    private String msDocumentIdElement;

    @XStreamAlias("AIU_mapping")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> mcAIUMapping;

    @XStreamAlias("AIU_element_name")
    private String msAIUElementName;

    @XStreamAlias("AIU_namespace")
    private String msAIUNamespace;

    public Map<String, String> getAIUMapping() {
        return mcAIUMapping;
    }

    public String getAIUElementName() {
        return msAIUElementName;
    }

    public String getAIUNamespace() {
        return msAIUNamespace;
    }

    public String getDocumentIdElement() {
        return msDocumentIdElement;
    }
}
