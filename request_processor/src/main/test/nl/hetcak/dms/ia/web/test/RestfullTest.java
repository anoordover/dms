package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.ConfigService;
import nl.hetcak.dms.ia.web.DocumentService;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This Test will test the Restfull Method's.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class RestfullTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestfullTest.class);
    private final static String REQUEST_LIST = "<ArchiefPersoonsnummer>1971429972</ArchiefPersoonsnummer>";
    private final static String REQUEST_DOCUMENT = "<ArchiefDocumentId>1663298436</ArchiefDocumentId><Volgnummer>001</Volgnummer>";
    private final static String REQUEST_SEARCH1 = "<ArchiefDocumentsoort>Z06</ArchiefDocumentsoort><ArchiefVerzenddagBegin>2013-08-01T00:00:00</ArchiefVerzenddagBegin><ArchiefVerzenddagEinde>2016-11-15T00:00:00</ArchiefVerzenddagEinde>";
    private final static String REQUEST_SEARCH2 = "<ArchiefPersoonsnummer>1971429972</ArchiefPersoonsnummer>";
    private final static String REQUEST_SEARCH3 = "<ArchiefVerzenddagBegin>2013-08-01T00:00:00</ArchiefVerzenddagBegin><ArchiefVerzenddagEinde>2016-11-15T00:00:00</ArchiefVerzenddagEinde>";
        
    @Test
    public void testDefaultResponse() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response responseEntity = documentService.defaultResponse(request);
        
        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getEntity());
        Assert.assertTrue(responseEntity.getEntity().toString().length() > 0);
    }
    
    @Test
    public void testConfigurationResponse() throws Exception {
        ConfigService configService = new ConfigService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response responseEntity = configService.checkConfig(request);
        
        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getEntity());
        Assert.assertTrue(responseEntity.getEntity().toString().length() > 0);
        Assert.assertFalse(responseEntity.getEntity().toString().contains("[ERROR]"));
    }
    
    @Test
    public void testDocumentList() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.listDocuments(REQUEST_LIST, request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200 || documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        
        Assert.assertTrue(data.length() > 1);

        if(documentListResponse.getStatus() == 200) {
            Assert.assertFalse(data.contains("<error>"));
        } else {
            Assert.assertTrue(data.contains("<error>"));
        }
    }
    
    @Test
    public void testDocumentSearch1() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.searchDocuments(REQUEST_SEARCH1, request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200 || documentListResponse.getStatus() == 500);

        Assert.assertNotNull(documentListResponse.getEntity());
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);

        Assert.assertTrue(data.length() > 1);

        if(documentListResponse.getStatus() == 200) {
            Assert.assertFalse(data.contains("<error>"));
        } else {
            Assert.assertTrue(data.contains("<error>"));
            Assert.assertFalse(data.contains("<document>"));
        }
    }
    
    @Test
    public void testDocumentSearch2() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.searchDocuments(REQUEST_SEARCH2, request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        
        Assert.assertTrue(data.length() > 1);
        Assert.assertFalse(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentSearch3() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.searchDocuments(REQUEST_SEARCH3, request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200 || documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);

        Assert.assertTrue(data.length() > 1);

        if(documentListResponse.getStatus() == 200) {
            Assert.assertFalse(data.contains("<error>"));
        } else {
            Assert.assertTrue(data.contains("<error>"));
        }
    }
    
    
    @Test
    public void testDocumentListContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.listDocuments(null, request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 406 || documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentListContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.listDocuments("", request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 406 || documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentListErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.listDocuments("<ArchiefPersoonsnummer>16afavfdsa</ArchiefPersoonsnummer>", request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testDocumentsContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentsResponse = documentService.getDocument(null, request);
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 406 || documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentsContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentsResponse = documentService.getDocument("", request);
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 406 || documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentsResponse = documentService.getDocument("<ArchiefDocumentId>gsdfhgdsf</ArchiefDocumentId>", request);
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testSearchDocumentsContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response searchDocumentsResponse = documentService.searchDocuments(null, request);
        
        Assert.assertNotNull(searchDocumentsResponse);
        Assert.assertTrue(searchDocumentsResponse.getStatus() == 406 || searchDocumentsResponse.getStatus() == 500);
        Assert.assertNotNull(searchDocumentsResponse.getEntity());
        
        String data = (String) searchDocumentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testSearchDocumentsContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response searchDocumentsResponse = documentService.searchDocuments("", request);
        
        Assert.assertNotNull(searchDocumentsResponse);
        Assert.assertTrue(searchDocumentsResponse.getStatus() == 406 || searchDocumentsResponse.getStatus() == 500);
        Assert.assertNotNull(searchDocumentsResponse.getEntity());
        
        String data = (String) searchDocumentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testSearchDocumentsContentErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response searchDocumentsResponse = documentService.searchDocuments("<ArchiefDocumentkenmerk>sfbsdf</ArchiefDocumentkenmerk>", request);
        
        Assert.assertNotNull(searchDocumentsResponse);
        Assert.assertTrue(searchDocumentsResponse.getStatus() == 500);
        Assert.assertNotNull(searchDocumentsResponse.getEntity());
        
        String data = (String) searchDocumentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocument() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentResponse = documentService.getDocument(REQUEST_DOCUMENT, request);
    
        Assert.assertNotNull(documentResponse);
        Assert.assertTrue(documentResponse.getStatus() == 200);
        Assert.assertNotNull(documentResponse.getEntity());

        LOGGER.info(documentResponse.getEntity().toString());
        Assert.assertTrue(documentResponse.getEntity().toString().length() > 20);
        Assert.assertFalse(documentResponse.getEntity().toString().startsWith("<error>"));
    }
}
