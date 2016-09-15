package com.amplexor.ia.retention;

import com.amplexor.ia.metadata.IADocument;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
@FunctionalInterface
public interface RetentionManager {
    IARetentionClass retrieveRetentionClass(IADocument objSource);
}
