package nl.hetcak.dms.ia.web.query;

import nl.hetcak.dms.ia.web.query.InfoArchiveQueryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This test will test the Query Generator.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class QueryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryTest.class);
    
    @Test
    public void testEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addEqualCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("EQUAL"));
    }
    
    @Test
    public void testEqualQueryMultieValue() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        values.add("456");
        values.add("789");
        String query = queryBuilder.addEqualCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("456"));
        Assert.assertTrue(query.contains("789"));
        Assert.assertTrue(query.contains("EQUAL"));
    }
    
    @Test
    public void testNotEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addNotEqualCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("NOT_EQUAL"));
    }
    
    @Test
    public void testGreaterQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addGreaterCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("GREATER"));
    }
    
    @Test
    public void testGreaterOrEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addGreaterOrEqualCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("GREATER_OR_EQUAL"));
    }
    
    @Test
    public void testLessQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addLessCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("LESS"));
    }
    
    @Test
    public void testLessOrEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addLessOrEqualCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("LESS_OR_EQUAL"));
    }
    
    @Test
    public void testStartsWithQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addStartsWithCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("STARTS_WITH"));
    }
    
    @Test
    public void testEndsWithQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        List<String> values = new ArrayList<>();
        values.add("123");
        String query = queryBuilder.addEndsWithCriteria("Test", values).build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("ENDS_WITH"));
    }
    
    @Test
    public void testFullSearchQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addFullTextCriteria("Test", "123").build();
        LOGGER.info(query);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("FULLTEXT"));
    }
}
