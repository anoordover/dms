package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.restfull.consumers.DocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.SearchDocumentRequestConsumer;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.text.ParseException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This Test will test the data consumers.
 * The consumers will read the Request Body.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConsumerTest {
    private static final String XML_EXAMPLE_1 = "<Request><ArchiefPersoonsnummer>20001</ArchiefPersoonsnummer></Request>";
    private static final String RESULT_1 = "20001";
    private static final String XML_EXAMPLE_2 = "<Request><ArchiefPersoonsnummer>802341</ArchiefPersoonsnummer></Request>";
    private static final String RESULT_2 = "802341";
    private static final String XML_EXAMPLE_3 = "<Request><ArchiefDocumentId>7654324</ArchiefDocumentId><Volgnummer>001</Volgnummer></Request>";
    private static final String RESULT_3_1 = "7654324";
    private static final String RESULT_3_2 = "001";
    private static final String XML_EXAMPLE_4 = "<Request><ArchiefDocumentTitle>Z01</ArchiefDocumentTitle><ArchiefDocumentkenmerk>97348</ArchiefDocumentkenmerk><ArchiefPersoonsnummer>802341</ArchiefPersoonsnummer><ArchiefVerzenddagBegin>2016-08-01T00:00:00</ArchiefVerzenddagBegin><ArchiefVerzenddagEinde>2016-08-15T00:00:00</ArchiefVerzenddagEinde></Request>";
    private static final String RESULT_4_1_1 = "Z01";
    private static final String RESULT_4_1_2 = "802341";
    private static final String RESULT_4_1_3 = "97348";
    private static final String RESULT_4_2 = "2016-08-01T00:00:00";
    private static final String RESULT_4_3 = "2016-08-15T00:00:00";
    private static final String RESULT_4_4 = "2016-08-01";
    private static final String RESULT_4_5 = "2016-08-15";
    
    
    //start with the good cases.
    @Test
    public void listDocumentConsumerTest() throws JAXBException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_1);
        Assert.assertTrue(request.getArchivePersonNumber().contentEquals(RESULT_1));
        Assert.assertTrue(request.hasContent());
        
        ListDocumentRequestConsumer request2 = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(request2.getArchivePersonNumber().contentEquals(RESULT_2));
        Assert.assertTrue(request2.hasContent());
    }
    
    @Test
    public void documentRequestConsumerTest() throws JAXBException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_3);
        Assert.assertTrue(docRequest1.getArchiveDocumentNumber().contentEquals(RESULT_3_1));
        Assert.assertTrue(docRequest1.getContinuationNumber().contentEquals(RESULT_3_2));
        Assert.assertTrue(docRequest1.hasContent());
    }
    
    @Test
    public void searchDocumentRequestConsumerTest() throws JAXBException, ParseException {
        SearchDocumentRequestConsumer searchRequest = SearchDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_4);
        Assert.assertTrue(searchRequest.getDocumentTitle().contentEquals(RESULT_4_1_1));
        Assert.assertTrue(searchRequest.getPersonNumber().contentEquals(RESULT_4_1_2));
        Assert.assertTrue(searchRequest.getDocumentCharacteristics().contentEquals(RESULT_4_1_3));
        Assert.assertTrue(searchRequest.getDocumentSendDate1().contentEquals(RESULT_4_2));
        Assert.assertTrue(searchRequest.getDocumentSendDate2().contentEquals(RESULT_4_3));
        Assert.assertTrue(searchRequest.getDocumentSendDate1AsInfoArchiveString().contentEquals(RESULT_4_4));
        Assert.assertTrue(searchRequest.getDocumentSendDate2AsInfoArchiveString().contentEquals(RESULT_4_5));
        Assert.assertTrue(searchRequest.hasContent());
    }
    
    //Bad cases
    @Test
    public void failListDocumentConsumerTest() throws JAXBException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest("<Request><ArchiefDocumentTitle></ArchiefDocumentTitle></Request>");
        Assert.assertFalse(request.hasContent());
    }
    
    @Test
    public void failDocumentRequestConsumerTest() throws JAXBException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_1);
        Assert.assertFalse(docRequest1.hasContent());
    }
    
    @Test
    public void failSearchDocumentRequestConsumerTest() throws JAXBException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_2);
        Assert.assertFalse(docRequest1.hasContent());
    }
}
