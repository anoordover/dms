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
    private String name, operator;
    private List<String> values;
    
    public Criterion() {
        this.values = new ArrayList<>();
    }
    
    @XmlElement(name="name")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlElement(name="operator")
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    @XmlElement(name="value")
    public List<String> getValues() {
        return values;
    }
}
