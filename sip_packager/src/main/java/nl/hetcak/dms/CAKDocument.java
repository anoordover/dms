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
    private static final String KEY_ATTACHMENT = "Attachement";

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
        rval.add(KEY_ATTACHMENT);
        return rval;
    }

    @Override
    public long getSizeEstimate() {
        long lReturn = 0;
        for (Map.Entry<String, String> objEntry : mcMetadata.entrySet()) {
            lReturn += (objEntry.getKey().length() * 2) + 5;
            lReturn += objEntry.getValue().length();
        }
        lReturn += msPayload.length();

        return lReturn;
    }

    @Override
    public byte[] loadContent(String sKey) {
        if (KEY_ATTACHMENT.equals(sKey)) {
            return Base64.getDecoder().decode(msPayload);
        }

        return new byte[0];
    }

    @Override
    public void setContent(String sKey, byte[] cContent) {
        if (KEY_ATTACHMENT.equals(sKey)) {
            msPayload = Base64.getEncoder().encodeToString(cContent);
        }
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
