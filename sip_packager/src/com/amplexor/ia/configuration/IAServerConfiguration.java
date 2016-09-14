package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAServerConfiguration {
    @XStreamAlias("protocol")
    private String mProtocol;

    @XStreamAlias("host")
    private String mHost;

    @XStreamAlias("port")
    private short mPort;

    public String getProtocol() {
        return mProtocol;
    }

    public String getHost(){
        return mHost;
    }

    public short getPort() {
        return mPort;
    }
}
