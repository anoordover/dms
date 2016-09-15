package com.amplexor.ia;

import com.amplexor.ia.metadata.IADocument;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface DocumentSource {
    IADocument retrieveDocument();
    void postResult();
}
