package com.amplexor.ia;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.amplexor.ia.data.CAKXmlDocumentData;
import com.amplexor.ia.enums.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admjzimmermann on 4-10-2016.
 */
public class XmlDocument {
    @XStreamAlias("MetaData")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> mcMetadata;

    @XStreamAlias("PayloadPdf")
    private String msPayloadPdf;

    public XmlDocument() {
        mcMetadata = new HashMap<>();
    }

    public void setMetadata(String sKey, String sValue) {
        mcMetadata.put(sKey, sValue);
    }

    public void setPayloadPdf(String sPayloadPdf) {
        this.msPayloadPdf = sPayloadPdf;
    }

    public String getXml() {
        QNameMap objQNameMap = new QNameMap();
        objQNameMap.setDefaultNamespace("urn:hetcak:dms:uitingarchief:2016:08");
        XStream objXStream = new XStream(new StaxDriver(objQNameMap));
        objXStream.alias("ArchiefDocument", this.getClass());
        objXStream.processAnnotations(this.getClass());
        return objXStream.toXML(this);
    }
}