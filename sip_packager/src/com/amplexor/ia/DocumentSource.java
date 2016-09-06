package com.amplexor.ia;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface DocumentSource {
    public Document retrieveDocument();
    public Node retrieveMetadata();
    public void postResult();
}
