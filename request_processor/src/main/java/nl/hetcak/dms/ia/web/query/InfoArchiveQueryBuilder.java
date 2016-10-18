package nl.hetcak.dms.ia.web.query;

import nl.hetcak.dms.ia.web.query.containers.Criterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * Creates queries that InfoArchive can use.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "data")
public class InfoArchiveQueryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoArchiveQueryBuilder.class);
    private static final String OPERATOR_BETWEEN = "BETWEEN";
    private static final String OPERATOR_EQUEL = "EQUAL";
    private static final String OPERATOR_NOT_EQUEL = "NOT_EQUAL";
    private static final String OPERATOR_GREATER_OR_EQUEL = "GREATER_OR_EQUAL";
    private static final String OPERATOR_GREATER = "GREATER";
    private static final String OPERATOR_LESS_OR_EQUEL = "LESS_OR_EQUAL";
    private static final String OPERATOR_LESS = "LESS";
    private static final String OPERATOR_STARTS_WITH = "STARTS_WITH";
    private static final String OPERATOR_ENDS_WITH = "ENDS_WITH";
    private static final String OPERATOR_FULLTEXT = "FULLTEXT";
    
    private List<Criterion> criterions;
    
    @XmlElement(name = "criterion")
    public List<Criterion> getCriterions() {
        return criterions;
    }
    
    public InfoArchiveQueryBuilder() {
        this.criterions = new ArrayList<>();
    }
    
    /**
     * Added a new Equel {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public void addEquelCriteria(String name, String value) {
        addCriterion(name, OPERATOR_EQUEL, value);
    }
    
    /**
     * Added a new Not Equel {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public void addNotEquelCriteria(String name, String value) {
        addCriterion(name, OPERATOR_NOT_EQUEL, value);
    }
    
    /**
     * Added a new Equel or Greater {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public void addGreaterOrEquelCriteria(String name, String value) {
        addCriterion(name, OPERATOR_GREATER_OR_EQUEL, value);
    }
    
    /**
     * Added a new Greater {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public void addGreaterCriteria(String name, String value) {
        addCriterion(name, OPERATOR_GREATER, value);
    }
    
    /**
     * Added a new Equel or Less {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public void addLessOrEquelCriteria(String name, String value) {
        addCriterion(name, OPERATOR_LESS_OR_EQUEL, value);
    }
    
    /**
     * Added a new Less {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public void addLessCriteria(String name, String value) {
        addCriterion(name, OPERATOR_LESS, value);
    }
    
    /**
     * Added a new Between {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value1 the value.
     * @param value2 the value.
     */
    public void addBetweenCriteria(String name, String value1, String value2) {
        Criterion criterion = new Criterion();
        criterion.setName(name);
        criterion.setOperator(OPERATOR_BETWEEN);
        criterion.getValues().add(value1);
        criterion.getValues().add(value2);
        LOGGER.debug(String.format("[%s] Added %s criteria", this.toString(), OPERATOR_BETWEEN));
        this.criterions.add(criterion);
    }
    
    private void addCriterion(String name, String operator, String value) {
        Criterion criterion = new Criterion();
        criterion.setName(name);
        criterion.setOperator(operator);
        criterion.getValues().add(value);
        this.criterions.add(criterion);
        LOGGER.debug(String.format("[%s] Added %s criteria", this.toString(), operator));
    }
    
    /**
     * Get the current class as XML string.
     * @return XML formatted String
     * @throws JAXBException Failed to create XML exception.
     */
    public String getXMLString() throws JAXBException {
        java.io.StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(InfoArchiveQueryBuilder.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, sw);
        } catch (JAXBException jaxExc) {
            LOGGER.error("JAXB failed to create xml.", jaxExc);
            throw jaxExc;
        }
        return sw.toString();
    }
    
}
