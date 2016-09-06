package com.amplexor.ia.cache;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IACache {
    @XStreamAlias("id")
    private int id;

    @XStreamAlias("num_messages")
    @XStreamImplicit(itemFieldName = "message")
    private List<TextMessage> numMessages;

    @XStreamAlias("expires")
    private long expires;

    public IACache() {

    }
}
