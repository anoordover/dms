package nl.hetcak.dms.ia.web.query.containers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "criterion")
public class Criterion {
    private String msName, msOperator;
    private List<String> mobjaValues;
    
    public Criterion() {
        this.mobjaValues = new ArrayList<>();
    }
    
    @XmlElement(name="name")
    public String getName() {
        return msName;
    }
    
    public void setName(String name) {
        this.msName = name;
    }
    
    @XmlElement(name="operator")
    public String getOperator() {
        return msOperator;
    }
    
    public void setOperator(String operator) {
        this.msOperator = operator;
    }
    
    @XmlElement(name="value")
    public List<String> getValues() {
        return mobjaValues;
    }
}
