package nl.hetcak.dms;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKRetentionClass extends IARetentionClass {
    @XStreamAlias("handeling_nr")
    private String msHandelingNr;

    public CAKRetentionClass(String sName) {
        this(sName, "");
    }

    public CAKRetentionClass(String sName, String sHandelingNr) {
        super(sName);
        msHandelingNr = sHandelingNr;
    }

    public String getHandelingNr() {
        return msHandelingNr;
    }
}
