package com.amplexor.ia.retention;

import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
@FunctionalInterface
public interface RetentionManager {
    IARetentionClass retrieveRetentionClass(IADocument objSource);
}
