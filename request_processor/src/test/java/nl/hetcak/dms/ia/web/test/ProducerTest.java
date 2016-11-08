package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.restfull.produces.ListDocumentResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ProducerTest {
    
    @Test
    public void produceListDocuments() {
        List<InfoArchiveDocument> documents = new ArrayList<InfoArchiveDocument>();
        InfoArchiveDocument document1 = new InfoArchiveDocument();
        document1.setArchiefDocumenttitel("test");
        document1.setArchiefHandelingsnummer("1");
        document1.setArchiefPersoonsnummer("10000001");
        document1.setArchiefRegelingsjaar("2016");
        document1.setArchiefDocumentId("1");
        document1.setArchiefDocumentkenmerk("TEST");
        document1.setArchiefVerzenddag("2016-08-15T00:00:00");
        document1.setArchiefDocumentsoort("TEST");
        document1.setArchiefDocumentstatus("DRAFT");
        document1.setArchiefRegeling("TEST");
    
        InfoArchiveDocument document2 = new InfoArchiveDocument();
        document2.setArchiefDocumenttitel("test2");
        document2.setArchiefHandelingsnummer("2");
        document2.setArchiefPersoonsnummer("10000002");
        document2.setArchiefRegelingsjaar("2016");
        document2.setArchiefDocumentId("2");
        document2.setArchiefDocumentkenmerk("TEST");
        document2.setArchiefVerzenddag("2016-08-15T00:00:00");
        document2.setArchiefDocumentsoort("TEST");
        document2.setArchiefDocumentstatus("DRAFT");
        document2.setArchiefRegeling("TEST");
    
        documents.add(document1);
        documents.add(document2);
    
        ListDocumentResponse response = new ListDocumentResponse();
        response.getDocuments().addAll(documents);
        String xmlString = response.getAsXML();
        Assert.assertNotNull(xmlString);
        Assert.assertTrue(xmlString.contains("test"));
        Assert.assertTrue(xmlString.contains("test2"));
    }
}
