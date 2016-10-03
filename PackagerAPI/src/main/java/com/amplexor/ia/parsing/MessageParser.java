package com.amplexor.ia.parsing;

import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.metadata.IADocument;

import java.util.List;

/**
 * Interface used by the SIP Packager to retrieve IADocument(s) from the data retrieved by a {@link DocumentSource}
 * Created by admjzimmermann on 6-9-2016.
 */
@FunctionalInterface
public interface MessageParser {
    /**
     * Should return a list of IADocuments parsed from sData
     * @param sData The data returned by the {@link DocumentSource} implementation
     * @return A list of documents parsed from sData
     */
    List<IADocument> parse(String sData);
}
