package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class IAServerConfiguration {
    @XStreamAlias("protocol")
    private String msProtocol;

    @XStreamAlias("host")
    private String msHost;

    @XStreamAlias("port")
    private short miPort;

    @XStreamAlias("gateway_protocol")
    private String msGatewayProtocol;

    @XStreamAlias("gateway_host")
    private String msGatewayHost;

    @XStreamAlias("gateway_port")
    private short miGatewayPort;

    @XStreamAlias("ingest_user")
    private String msIngestUser;

    @XStreamAlias("encrypted_ingest_password")
    private String msIngestPasswordEnc;

    @XStreamAlias("ingest_tenant")
    private String msIngestTenant;

    @XStreamAlias("ia_application_name")
    private String msIAApplicationName;

    public String getProtocol() {
        return msProtocol;
    }

    public String getHost() {
        return msHost;
    }

    public short getPort() {
        return miPort;
    }

    public String getGatewayProtocol() {
        return msGatewayProtocol;
    }

    public String getGatewayHost() {
        return msGatewayHost;
    }

    public short getGatewayPort() {
        return miGatewayPort;
    }

    public String getIngestUser() {
        return msIngestUser;
    }

    public String getEncryptedIngestPassword() {
        return msIngestPasswordEnc;
    }

    public String getIngestTenant() {
        return msIngestTenant;
    }

    public String getIAApplicationName() {
        return msIAApplicationName;
    }
}
