package nl.hetcak.dms;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamConverters;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKDocument implements IADocument {
    @XStreamAlias("MetaData")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> metadata;

    @XStreamAlias("PayloadPdf")
    private String payload;

    public CAKDocument(String location) {
        metadata = new HashMap<String, String>();
    }

    public String getMetadata(String key) {
        return metadata.get(key);
    }

    public void setMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public void extract(String directory) {
        try {
            File xmlFile = new File(String.format("%s/%s.xml", directory, getMetadata("documentId")));
            xmlFile.createNewFile();
            XStream xstream = new XStream(new StaxDriver());
            xstream.toXML(metadata);


            File pdfFile = new File(String.format("%s/%s.pdf", directory, getMetadata("documentId")));
            pdfFile.createNewFile();
            Files.write(pdfFile.toPath(), Base64.getDecoder().decode(payload));

        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
