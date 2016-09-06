package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAServerConfiguration {
    @XStreamAlias("protocol")
    private String protocol;

    @XStreamAlias("host")
    private String host;

    @XStreamAlias("port")
    private short port;
}
