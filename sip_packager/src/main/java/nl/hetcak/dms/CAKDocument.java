package nl.hetcak.dms;

import com.amplexor.ia.configuration.converters.ParameterConverter;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKDocument extends IADocument {
    public static final String KEY_ATTACHMENT = "Attachment";

    @XStreamAlias("MetaData")
    @XStreamConverter(ParameterConverter.class)
    private Map<String, String> mcMetadata;

    @XStreamAlias("PayloadPdf")
    private String msPayload;

    @XStreamAlias("PayloadFile")
    private String msPayloadFile;

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

        if (msPayload != null) {
            lReturn += msPayload.length();
        }

        return lReturn;
    }

    @Override
    public void loadContent(String sKey) {
        if (KEY_ATTACHMENT.equals(sKey)) {
            File objPayloadFile = new File(msPayloadFile);
            try (FileInputStream objPayloadStream = new FileInputStream(objPayloadFile)) {
                byte[] pData = new byte[(int) objPayloadFile.length()];
                objPayloadStream.read(pData, 0, (int) objPayloadFile.length());
                msPayload = new String(Base64.getEncoder().encode(pData));
            } catch (IllegalArgumentException | IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }
    }

    @Override
    public void setContent(String sKey, byte[] cContent) {
        if (KEY_ATTACHMENT.equals(sKey)) {
            msPayload = Base64.getEncoder().encodeToString(cContent);
        }
    }

    @Override
    public byte[] getContent(String sKey) {
        if (KEY_ATTACHMENT.equals(sKey)) {
            if (msPayload == null) {
                loadContent(sKey);
            }

            return Base64.getDecoder().decode(msPayload.getBytes());
        }

        return new byte[0];
    }

    @Override
    public void setContentFile(String sKey, String sContentFile) {
        if (KEY_ATTACHMENT.equals(sKey)) {
            msPayloadFile = sContentFile;
        }
    }

    @Override
    public String getDocumentId() {
        return mcMetadata.get("ArchiefDocumentId");
    }

}
