package com.amplexor.ia.retention;

import com.amplexor.ia.DocumentSource;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface RetentionManager {
    public IARetentionClass retrieveRetentionClass(DocumentSource source);
}
