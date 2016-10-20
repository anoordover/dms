package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.restfull.consumes.ListDocumentRequest;
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
    
    @Test
    public void ListDocumentTest() throws JAXBException {
        ListDocumentRequest request = ListDocumentRequest.unmarshalRequest(XML_EXAMPLE_1);
        Assert.assertTrue(request.getArchivePersonNumber().contentEquals(RESULT_1));
        
        ListDocumentRequest request2 = ListDocumentRequest.unmarshalRequest(XML_EXAMPLE_2);
        Assert.assertTrue(request2.getArchivePersonNumber().contentEquals(RESULT_2));
    }
}
