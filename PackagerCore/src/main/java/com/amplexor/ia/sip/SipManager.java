package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

/**
 * Interface used by the SIP Packager to create a SIP file from either an {@link IACache} (Multiple ingestion) or {@link IADocument} (Single ingestion)
 * Created by admjzimmermann on 22-9-2016.
 */
public interface SipManager {
    /**
     * Creates a SIP file based on the contents of objCache, the SIP location can be queried by using {@link IACache}.getSIPFile()
     * @param objCache The cache whose contents should be exported into a SIP file
     * @return a boolean value indicating whether creation was successful
     */
    boolean getSIPFile(IACache objCache);

    /**
     * Creates a SIP file based on the contents of objCache, the SIP location can be queried by using {@link IADocument}.getSIPFile()
     * @param objDocument The document that should be used for creating a SIP file
     * @param objRetentionClass The retention class that applies to objDocument
     * @return a boolean value indication whether creation was successful
     */
    IACache getSIPFile(IADocumentReference objDocument, IARetentionClass objRetentionClass);
}
