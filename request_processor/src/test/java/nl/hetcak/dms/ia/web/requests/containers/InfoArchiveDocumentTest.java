package nl.hetcak.dms.ia.web.requests.containers;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveDocumentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoArchiveDocumentTest.class);
        
    private static final String DOC_TITLE = "Z01";
    private static final String DOC_REGELING = "Wlz";
    private static final String DOC_ID = "1124296490";
    private static final String DOC_STATUS = "TEST";
    private static final String PERSOON_NUMBER = "6793306496";
    private static final String DOC_SOORT = "Factuur";
    private static final String DOC_KENMERK = "889617";
    private static final String DOC_TYPE = "Inkomend";
    private static final String DOC_REGELINGS_JAAR = "2015";
    private static final String DOC_VERZENDDAG = "2015-08-21";
    
    private static final String RESULT_DOC_TITLE = "<ArchiefDocumenttitel>Z01</ArchiefDocumenttitel>";
    private static final String RESULT_DOC_REGELING = "<ArchiefRegeling>Wlz</ArchiefRegeling>";
    private static final String RESULT_DOC_ID = "<ArchiefDocumentId>1124296490</ArchiefDocumentId>";
    private static final String RESULT_DOC_STATUS = "<ArchiefDocumentstatus>TEST</ArchiefDocumentstatus>";
    private static final String RESULT_PERSOON_NUMBER = "<ArchiefPersoonsnummer>6793306496</ArchiefPersoonsnummer>";
    private static final String RESULT_DOC_SOORT = "<ArchiefDocumentsoort>Factuur</ArchiefDocumentsoort>";
    private static final String RESULT_DOC_KENMERK = "<ArchiefDocumentkenmerk>889617</ArchiefDocumentkenmerk>";
    private static final String RESULT_DOC_TYPE = "<ArchiefDocumenttype>Inkomend</ArchiefDocumenttype>";
    private static final String RESULT_DOC_REGELINGS_JAAR = "<ArchiefRegelingsjaar>2015</ArchiefRegelingsjaar>";
    private static final String RESULT_DOC_VERZENDDAG = "<ArchiefVerzenddag>2015-08-21</ArchiefVerzenddag>";
    
    
    @Test
    public void infoArchiveDocumentTest() throws Exception {
        InfoArchiveDocument document = new InfoArchiveDocument();
        document.setArchiefDocumenttitel(DOC_TITLE);
        document.setArchiefDocumentId(DOC_ID);
        document.setArchiefDocumentkenmerk(DOC_KENMERK);
        document.setArchiefDocumentsoort(DOC_SOORT);
        document.setArchiefDocumenttype(DOC_TYPE);
        document.setArchiefRegeling(DOC_REGELING);
        document.setArchiefRegelingsjaar(DOC_REGELINGS_JAAR);
        document.setArchiefPersoonsnummer(PERSOON_NUMBER);
        document.setArchiefDocumentstatus(DOC_STATUS);
        document.setArchiefVerzenddag(DOC_VERZENDDAG);
        
        String xml = document.getXMLString();
        LOGGER.info(xml);
    
        Assert.assertTrue(xml.contains(RESULT_DOC_TITLE));
        Assert.assertTrue(xml.contains(RESULT_DOC_REGELING));
        Assert.assertTrue(xml.contains(RESULT_DOC_ID));
        Assert.assertTrue(xml.contains(RESULT_DOC_STATUS));
        Assert.assertTrue(xml.contains(RESULT_PERSOON_NUMBER));
        Assert.assertTrue(xml.contains(RESULT_DOC_SOORT));
        Assert.assertTrue(xml.contains(RESULT_DOC_KENMERK));
        Assert.assertTrue(xml.contains(RESULT_DOC_TYPE));
        Assert.assertTrue(xml.contains(RESULT_DOC_REGELINGS_JAAR));
        Assert.assertTrue(xml.contains(RESULT_DOC_VERZENDDAG));
        
        
    }
}
