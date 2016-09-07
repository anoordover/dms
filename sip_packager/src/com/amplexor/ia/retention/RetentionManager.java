package com.amplexor.ia.retention;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.metadata.IADocument;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface RetentionManager {
    public IARetentionClass retrieveRetentionClass(IADocument source);
}
