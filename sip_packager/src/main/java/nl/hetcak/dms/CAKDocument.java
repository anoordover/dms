package nl.hetcak.dms;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.*;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKDocument extends IADocument {
    @XStreamAlias("MetaData")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> mcMetadata;

    @XStreamAlias("PayloadPdf")
    private String msPayload;

    @XStreamOmitField
    private String msError;

    public CAKDocument() {
        mcMetadata = new HashMap<>();
    }

    @Override
    public Set<String> getMetadataKeys() {
        return mcMetadata.keySet();
    }

    @Override
    public String getMetadata(String sKey) {
        return mcMetadata.get(sKey);
    }

    @Override
    public void setMetadata(String sKey, String sValue) {
        mcMetadata.put(sKey, sValue);
    }

    @Override
    public Set<String> getContentKeys() {
        Set<String> rval = new HashSet<>();
        rval.add("Attachment");
        return rval;
    }

    @Override
    public byte[] loadContent(String key) {
        if("Attachment".equals(key)) {
            return Base64.getDecoder().decode(msPayload);
        }

        return new byte[0];
    }

    @Override
    public void setError(String sError) {
        msError = sError;
    }

    @Override
    public String getError() {
        return msError;
    }

    public void setPayload(byte[] cPayload) {
        msPayload = Base64.getEncoder().encodeToString(cPayload);
    }
}
