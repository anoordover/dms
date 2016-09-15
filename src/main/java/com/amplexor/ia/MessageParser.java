package com.amplexor.ia;

import com.amplexor.ia.metadata.IADocument;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
@FunctionalInterface
public interface MessageParser {
    IADocument parse(DocumentSource objSource);
}
