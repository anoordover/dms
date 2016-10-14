package nl.hetcak.dms.ia.web;

/**
 * Created by zimmermannj on 10/14/2016.
 */
public class RequestProcessorConfiguration {
    private String msGatewayProtocol;
    private String msGatewayHost;
    private short miGatewayPort;
    private String msAPIProtocol;
    private String msAPIHost;
    private short miAPIPort;

    public String getGatewayProtocol() {
        return msGatewayProtocol;
    }

    public void setGatewayProtocol(String sGatewayProtocol) {
        msGatewayProtocol = sGatewayProtocol;
    }

    public String getGatewayHost() {
        return msGatewayHost;
    }

    public void setGatewayHost(String sGatewayHost) {
        this.msGatewayHost = sGatewayHost;
    }

    public short getGatewayPort() {
        return miGatewayPort;
    }

    public void setGatewayPort(short iGatewayPort) {
        this.miGatewayPort = iGatewayPort;
    }

    public String getAPIProtocol() {
        return msAPIProtocol;
    }

    public void setAPIProtocol(String sAPIProtocol) {
        this.msAPIProtocol = sAPIProtocol;
    }

    public String getAPIHost() {
        return msAPIHost;
    }

    public void setAPIHost(String sAPIHost) {
        this.msAPIHost = sAPIHost;
    }

    public short getAPIPort() {
        return miAPIPort;
    }

    public void setAPIPort(short iAPIPort) {
        this.miAPIPort = iAPIPort;
    }
}
