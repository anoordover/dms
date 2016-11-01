package nl.hetcak.dms;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.Collections;
import java.util.List;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKRetentionClass extends IARetentionClass {
    @XStreamAlias("handeling_nr")
    private String msHandelingNr;

    @XStreamAlias("policy")
    private String msPolicy;

    @XStreamImplicit(itemFieldName = "associated_title")
    private List<String> msAssociatedTitles;

    public CAKRetentionClass(String sName) {
        this(sName, "");
    }

    public CAKRetentionClass(String sName, String sHandelingNr) {
        this(sName, sHandelingNr, "");
    }

    public CAKRetentionClass(String sName, String sHandelingNr, String sPolicy) {
        super(sName);
        msHandelingNr = sHandelingNr;
        msPolicy = sPolicy;
    }

    public String getHandelingNr() {
        return msHandelingNr;
    }

    @Override
    public String getPolicy() {
        return msPolicy;
    }

    public List<String> getAssociatedTitles() {
        return Collections.unmodifiableList(msAssociatedTitles);
    }
}
