package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.query.InfoArchiveQueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This test will test the Query Generator.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class QueryTest {
    
    @Test
    public void testEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addEqualCriteria("Test", "123").build();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("EQUAL"));
    }
    
    @Test
    public void testNotEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addNotEqualCriteria("Test", "123").build();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("NOT_EQUAL"));
    }
    
    @Test
    public void testGreaterQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addGreaterCriteria("Test", "123").build();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("GREATER"));
    }
    
    @Test
    public void testGreaterOrEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addGreaterOrEqualCriteria("Test", "123").build();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("GREATER_OR_EQUAL"));
    }
    
    @Test
    public void testLessQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addLessCriteria("Test", "123").build();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("LESS"));
    }
    
    @Test
    public void testLessOrEqualQuery() throws JAXBException {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        String query = queryBuilder.addLessOrEqualCriteria("Test", "123").build();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("123"));
        Assert.assertTrue(query.contains("LESS_OR_EQUAL"));
    }
}
