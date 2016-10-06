package nl.hetcak.dms;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKRetentionClass extends IARetentionClass {
    @XStreamAlias("handeling_nr")
    private String msHandelingNr;

    @XStreamImplicit(itemFieldName = "associated_document_title")
    private List<String> msAssociatedDocumentTitles;

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

    public List<String> getAssociatedDocumentTitle() {
        return msAssociatedDocumentTitles;
    }
}
