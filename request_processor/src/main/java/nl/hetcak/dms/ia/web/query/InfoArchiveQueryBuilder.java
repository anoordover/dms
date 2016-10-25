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
    private static final String OPERATOR_EQUAL = "EQUAL";
    private static final String OPERATOR_NOT_EQUAL = "NOT_EQUAL";
    private static final String OPERATOR_GREATER_OR_EQUAL = "GREATER_OR_EQUAL";
    private static final String OPERATOR_GREATER = "GREATER";
    private static final String OPERATOR_LESS_OR_EQUAL = "LESS_OR_EQUAL";
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
     * Added a new Equal {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public InfoArchiveQueryBuilder addEqualCriteria(String name, String value) {
        addCriterion(name, OPERATOR_EQUAL, value);
        return this;
    }
    
    /**
     * Added a new Not Equal {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public InfoArchiveQueryBuilder addNotEqualCriteria(String name, String value) {
        addCriterion(name, OPERATOR_NOT_EQUAL, value);
        return this;
    }
    
    /**
     * Added a new Equal or Greater {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public InfoArchiveQueryBuilder addGreaterOrEqualCriteria(String name, String value) {
        addCriterion(name, OPERATOR_GREATER_OR_EQUAL, value);
        return this;
    }
    
    /**
     * Added a new Greater {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public InfoArchiveQueryBuilder addGreaterCriteria(String name, String value) {
        addCriterion(name, OPERATOR_GREATER, value);
        return this;
    }
    
    /**
     * Added a new Equal or Less {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public InfoArchiveQueryBuilder addLessOrEqualCriteria(String name, String value) {
        addCriterion(name, OPERATOR_LESS_OR_EQUAL, value);
        return this;
    }
    
    /**
     * Added a new Less {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value the value.
     */
    public InfoArchiveQueryBuilder addLessCriteria(String name, String value) {
        addCriterion(name, OPERATOR_LESS, value);
        return this;
    }
    
    /**
     * Added a new Between {@link Criterion} to this query.
     * @param name Name of the column.
     * @param value1 the value.
     * @param value2 the value.
     */
    public InfoArchiveQueryBuilder addBetweenCriteria(String name, String value1, String value2) {
        Criterion criterion = new Criterion();
        criterion.setName(name);
        criterion.setOperator(OPERATOR_BETWEEN);
        criterion.getValues().add(value1);
        criterion.getValues().add(value2);
        LOGGER.debug(String.format("[%s] Added %s criteria", this.toString(), OPERATOR_BETWEEN));
        this.criterions.add(criterion);
        return this;
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
    public String build() throws JAXBException {
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
