package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.restfull.consumers.DocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.SearchDocumentRequestConsumer;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
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
    private static final String XML_EXAMPLE_1 = "<RaadplegenDocumentLijstRequest><ArchiefPersoonsnummer>20001</ArchiefPersoonsnummer></RaadplegenDocumentLijstRequest>";
    private static final String RESULT_1 = "20001";
    private static final String XML_EXAMPLE_2 = "<RaadplegenDocumentLijstRequest><ArchiefPersoonsnummer>802341</ArchiefPersoonsnummer></RaadplegenDocumentLijstRequest>";
    private static final String RESULT_2 = "802341";
    private static final String XML_EXAMPLE_3 = "<RaadplegenDocumentRequest><ArchiefDocumentId>7654324</ArchiefDocumentId></RaadplegenDocumentRequest>";
    private static final String RESULT_3_1 = "7654324";
    private static final String RESULT_3_2 = "001";
    private static final String XML_EXAMPLE_4 = "<RaadplegenDocumentLijstRequest><ArchiefDocumenttitel>Z01</ArchiefDocumenttitel><ArchiefDocumentkenmerk>97348</ArchiefDocumentkenmerk><ArchiefPersoonsnummer>802341</ArchiefPersoonsnummer><VerzenddatumPeriodeVan>2016-08-01T00:00:00</VerzenddatumPeriodeVan><VerzenddatumPeriodeTm>2016-08-15T00:00:00</VerzenddatumPeriodeTm></RaadplegenDocumentLijstRequest>";
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
        Assert.assertTrue(request.getArchiefPersoonsnummer().contentEquals(RESULT_1));
        Assert.assertTrue(request.hasContent());
        
        ListDocumentRequestConsumer request2 = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(request2.getArchiefPersoonsnummer().contentEquals(RESULT_2));
        Assert.assertTrue(request2.hasContent());
    }
    
    @Test
    public void documentRequestConsumerTest() throws JAXBException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_3);
        Assert.assertTrue(docRequest1.getArchiveDocumentNumber().contentEquals(RESULT_3_1));
        Assert.assertTrue(docRequest1.hasContent());
    }
    
    @Test
    public void listDocumentRequestConsumerTest2() throws JAXBException, ParseException {
        ListDocumentRequestConsumer searchRequest = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_4);
        Assert.assertTrue(searchRequest.getArchiefDocumenttitel().contentEquals(RESULT_4_1_1));
        Assert.assertTrue(searchRequest.getArchiefPersoonsnummer().contentEquals(RESULT_4_1_2));
        Assert.assertTrue(searchRequest.getArchiefDocumentkenmerk().contentEquals(RESULT_4_1_3));
        Assert.assertTrue(searchRequest.getVerzenddatumPeriodeVan().contentEquals(RESULT_4_2));
        Assert.assertTrue(searchRequest.getVerzenddatumPeriodeTm().contentEquals(RESULT_4_3));
        Assert.assertTrue(searchRequest.getVerzenddatumPeriodeVanAsInfoArchiveString().contentEquals(RESULT_4_4));
        Assert.assertTrue(searchRequest.getVerzenddatumPeriodeTmAsInfoArchiveString().contentEquals(RESULT_4_5));
        Assert.assertTrue(searchRequest.hasContent());
    }

    @Test
    public void parseListDocumentConsumerTest() throws JAXBException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest("<RaadplegenDocumentLijstRequest><ArchiefDocumentTitle></ArchiefDocumentTitle></RaadplegenDocumentLijstRequest>");
        Assert.assertFalse(request.hasContent());
    }
    
    @Test(expected = UnmarshalException.class)
    public void faildocumentRequestConsumerTest() throws JAXBException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_1);
        Assert.assertFalse(docRequest1.hasContent());
    }
    
    @Test
    public void listDocumentRequestConsumerTest() throws JAXBException {
        ListDocumentRequestConsumer docRequest1 = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(docRequest1.hasContent());
    }
}
