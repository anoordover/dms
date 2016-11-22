package nl.hetcak.dms.ia.web.restfull;

import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.exceptions.SchemaLoadingException;
import nl.hetcak.dms.ia.web.restfull.consumers.DocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
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
    private static final String XML_EXAMPLE_1 = "<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefPersoonsnummer>20001</urn:ArchiefPersoonsnummer></urn:RaadplegenDocumentLijstRequest>";
    private static final String RESULT_1 = "20001";
    private static final String XML_EXAMPLE_2 = "<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefPersoonsnummer>802341</urn:ArchiefPersoonsnummer></urn:RaadplegenDocumentLijstRequest>";
    private static final String RESULT_2 = "802341";
    private static final String XML_EXAMPLE_3 = "<urn:RaadplegenDocumentRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefDocumentId>7654324456</urn:ArchiefDocumentId></urn:RaadplegenDocumentRequest>";
    private static final String RESULT_3_1 = "7654324456";
    private static final String XML_EXAMPLE_4 = "<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefDocumenttitel>Z01</urn:ArchiefDocumenttitel><urn:ArchiefDocumentkenmerk>97348</urn:ArchiefDocumentkenmerk><urn:ArchiefPersoonsnummer>802341</urn:ArchiefPersoonsnummer><urn:VerzenddatumPeriodeVan>2016-08-01T00:00:00</urn:VerzenddatumPeriodeVan><urn:VerzenddatumPeriodeTm>2016-08-15T00:00:00</urn:VerzenddatumPeriodeTm></urn:RaadplegenDocumentLijstRequest>";
    private static final String RESULT_4_1_1 = "Z01";
    private static final String RESULT_4_1_2 = "802341";
    private static final String RESULT_4_1_3 = "97348";
    private static final String RESULT_4_2 = "2016-08-01T00:00:00";
    private static final String RESULT_4_3 = "2016-08-15T00:00:00";
    private static final String RESULT_4_4 = "2016-08-01";
    private static final String RESULT_4_5 = "2016-08-15";
    
    
    //start with the good cases.
    @Test
    public void listDocumentConsumerTest() throws RequestResponseException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_1);
        Assert.assertTrue(request.getArchiefPersoonsnummer().contentEquals(RESULT_1));
        Assert.assertTrue(request.hasContent());
        
        ListDocumentRequestConsumer request2 = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(request2.getArchiefPersoonsnummer().contentEquals(RESULT_2));
        Assert.assertTrue(request2.hasContent());
    }
    
    @Test
    public void documentRequestConsumerTest() throws RequestResponseException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_3);
        Assert.assertTrue(docRequest1.getArchiveDocumentNumber().contentEquals(RESULT_3_1));
        Assert.assertTrue(docRequest1.hasContent());
    }
    
    @Test
    public void listDocumentRequestConsumerTest2() throws RequestResponseException {
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

    @Test(expected = RequestResponseException.class)
    public void parseFailureListDocumentConsumerTest() throws RequestResponseException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest("<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefDocumenttitel></urn:ArchiefDocumenttitel></urn:RaadplegenDocumentLijstRequest>");

    }
    
    @Test
    public void parseListDocumentConsumerTest() throws RequestResponseException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest("<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefDocumenttitel>B02</urn:ArchiefDocumenttitel></urn:RaadplegenDocumentLijstRequest>");
        Assert.assertTrue(request.hasContent());
    }
    
    @Test(expected = RequestResponseException.class)
    public void faildocumentRequestConsumerTest() throws RequestResponseException {
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_1);
        Assert.assertFalse(docRequest1.hasContent());
    }
    
    @Test
    public void listDocumentRequestConsumerTest() throws RequestResponseException {
        ListDocumentRequestConsumer docRequest1 = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(docRequest1.hasContent());
    }
}
