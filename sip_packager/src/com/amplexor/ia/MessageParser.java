package com.amplexor.ia;

import com.amplexor.ia.metadata.IADocument;
import org.w3c.dom.Document;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface MessageParser {
    public IADocument parse(DocumentSource source);
}
