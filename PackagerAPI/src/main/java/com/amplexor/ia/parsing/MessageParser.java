package com.amplexor.ia.parsing;

import com.amplexor.ia.metadata.IADocument;

import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
@FunctionalInterface
public interface MessageParser {
    List<IADocument> parse(String sData);
}
