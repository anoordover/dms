package com.amplexor.ia;

import org.w3c.dom.Document;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public interface MessageParser {
    public Document parse(DocumentSource source);
}
