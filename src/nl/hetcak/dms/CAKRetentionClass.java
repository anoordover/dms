package nl.hetcak.dms;

import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class CAKRetentionClass extends IARetentionClass {
    @XStreamAlias("handeling_nr")
    private String handelingNr;


    public CAKRetentionClass(String name) {
        this(name, "");
    }

    public CAKRetentionClass(String name, String handelingNr) {
        super(name);
        this.handelingNr = handelingNr;
    }

    public String getHandelingNr() {
        return handelingNr;
    }
}
