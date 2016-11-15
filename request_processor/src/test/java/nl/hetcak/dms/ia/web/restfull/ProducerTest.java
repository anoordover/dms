package nl.hetcak.dms.ia.web.restfull;

import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.restfull.produces.RaadplegenLijstDocumentResponse;
import nl.hetcak.dms.ia.web.restfull.produces.containers.ArchiefDocumenten;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ProducerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerTest.class);
    
    @Test
    public void produceListDocuments() {
        List<InfoArchiveDocument> documents = new ArrayList<InfoArchiveDocument>();
        InfoArchiveDocument document1 = new InfoArchiveDocument();
        document1.setArchiefDocumenttitel("Z01");
        document1.setArchiefPersoonsnummer("10000001");
        document1.setArchiefRegelingsjaar("2016");
        document1.setArchiefDocumentId("1000000000");
        document1.setArchiefDocumentkenmerk("TEST");
        document1.setArchiefVerzenddag("2016-08-15T00:00:00");
        document1.setArchiefDocumentsoort("Inkomend");
        document1.setArchiefDocumentstatus("DRAFT");
        document1.setArchiefRegeling("Wlz");
        document1.setArchiefDocumenttype("string");
    
        InfoArchiveDocument document2 = new InfoArchiveDocument();
        document2.setArchiefDocumenttitel("Z01");
        document2.setArchiefPersoonsnummer("10000002");
        document2.setArchiefRegelingsjaar("2016");
        document2.setArchiefDocumentId("1000000002");
        document2.setArchiefDocumentkenmerk("TEST");
        document2.setArchiefVerzenddag("2016-08-15T00:00:00");
        document2.setArchiefDocumentsoort("Intern");
        document2.setArchiefDocumentstatus("DRAFT");
        document2.setArchiefRegeling("Wlz");
        document2.setArchiefDocumenttype("string");
    
        documents.add(document1);
        documents.add(document2);

        RaadplegenLijstDocumentResponse response = new RaadplegenLijstDocumentResponse();
        ArchiefDocumenten archiefDocumenten = new ArchiefDocumenten();
        archiefDocumenten.getDocumentList().addAll(documents);
        response.setArchiefDocumenten(archiefDocumenten);
        response.setResultCode(0);
        response.setResultDescription("OK");
        String xmlString = response.getAsXML();
        LOGGER.info(xmlString);
        Assert.assertNotNull(xmlString);
        Assert.assertTrue(xmlString.contains("Wlz"));
        Assert.assertTrue(xmlString.contains("1000000000"));
    }
}
