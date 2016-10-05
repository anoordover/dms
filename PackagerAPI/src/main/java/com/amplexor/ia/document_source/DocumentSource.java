package com.amplexor.ia.document_source;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.metadata.IADocument;

import java.util.List;

/**
 * This interface defines the methods used by the SIP Packager to retrieve data and post back the result to whatever datasource is implemented
 * Created by admjzimmermann on 6-9-2016.
 */
public interface DocumentSource {
    /**
     * Retrieve data from the document source and return the data as a String
     * @return The retrieved data in the form of a {@link String}
     */
    String retrieveDocumentData();

    /**
     * Post the resulting status of the {@link IACache} back to the document source
     * @param objCache The cache whose status should be posted
     */
    void postResult(List<IADocumentReference> objCache);

    /**
     * This method will initialize the document source, such as setting up connections
     */
    void initialize();

    /**
     * This method will shutdown the document source by, for example closing open connections.
     */
    void shutdown();
}
