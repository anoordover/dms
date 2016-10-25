package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.restfull.consumers.DocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ConsumerTest {
    private static final String XML_EXAMPLE_1 = "<request><ArchiefPersoonsnummer>20001</ArchiefPersoonsnummer></request>";
    private static final String RESULT_1 = "20001";
    private static final String XML_EXAMPLE_2 = "<request><ArchiefPersoonsnummer>802341</ArchiefPersoonsnummer></request>";
    private static final String RESULT_2 = "802341";
    private static final String XML_EXAMPLE_3 = "<request><ArchiefDocumentId>7654324</ArchiefDocumentId><Volgnummer>001</Volgnummer></request>";
    private static final String RESULT_3_1 = "7654324";
    private static final String RESULT_3_2 = "001";
    
    @Test
    public void ListDocumentTest() throws JAXBException {
        ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_1);
        Assert.assertTrue(request.getArchivePersonNumber().contentEquals(RESULT_1));
        
        ListDocumentRequestConsumer request2 = ListDocumentRequestConsumer.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(request2.getArchivePersonNumber().contentEquals(RESULT_2));
    
        DocumentRequestConsumer docRequest1 = DocumentRequestConsumer.unmarshallerRequest(XML_EXAMPLE_3);
        Assert.assertTrue(docRequest1.getArchiveDocumentNumber().contentEquals(RESULT_3_1));
        Assert.assertTrue(docRequest1.getContinuationNumber().contentEquals(RESULT_3_2));
    }
}
