package com.amplexor.ia.retention;

import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

/**
 * Interface used by the SIP Packager to retrieve the retention class associated with a {@link IADocument}
 * The available retention classes should be defined in the configuration xml file
 * Created by admjzimmermann on 6-9-2016.
 */
@FunctionalInterface
public interface RetentionManager {
    /**
     * Retrieves the retention class of the objSource document
     * @param objSource The document whose retention class should be retrieved
     * @return The retention class associated with objSource
     */
    IARetentionClass retrieveRetentionClass(IADocument objSource);
}
