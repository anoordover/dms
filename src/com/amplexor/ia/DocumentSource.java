package com.amplexor.ia;

import com.amplexor.ia.metadata.IADocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface DocumentSource {
    public IADocument retrieveDocument();
    public void postResult();
}
