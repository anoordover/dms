package nl.hetcak.dms;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamConverters;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKDocument extends IADocument {
    @XStreamAlias("MetaData")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> metadata;

    @XStreamAlias("PayloadPdf")
    private String payload;

    public CAKDocument() {
        metadata = new HashMap<String, String>();
    }

    @Override
    public Set<String> getMetadataKeys() {
        return metadata.keySet();
    }

    @Override
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    @Override
    public void setMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public Set<String> getContentKeys() {
        Set<String> rval = new HashSet<String>();
        rval.add("PayloadPdf");
        return rval;
    }

    @Override
    public byte[] loadContent(String key) {
        if(key.equals("PayloadPdf")) {
            return Base64.getDecoder().decode(payload);
        }

        return null;
    }
}
