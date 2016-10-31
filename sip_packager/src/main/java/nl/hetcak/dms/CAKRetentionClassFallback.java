package nl.hetcak.dms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by zimmermannj on 10/28/2016.
 */
public class CAKRetentionClassFallback extends CAKRetentionClass {
    @XStreamAlias("fallback_policy")
    private String msFallbackPolicy;


    public CAKRetentionClassFallback(String sName) {
        super(sName);
    }

    public CAKRetentionClassFallback(String sName, String sHandelingsNummer) {
        super(sName, sHandelingsNummer);
    }

    public String getFallbackPolicy() {
        return msFallbackPolicy;
    }
}
