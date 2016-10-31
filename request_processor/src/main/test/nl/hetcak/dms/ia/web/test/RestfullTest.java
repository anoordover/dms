package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.ConfigService;
import nl.hetcak.dms.ia.web.DocumentService;
import org.junit.Assert;
import org.junit.Test;

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
    private final static String REQUEST_LIST = "<ArchiefPersoonsnummer>1603575962</ArchiefPersoonsnummer>";
    private final static String REQUEST_DOCUMENT = "<ArchiefDocumentId>1268712898</ArchiefDocumentId><Volgnummer>001</Volgnummer>";
    private final static String REQUEST_SEARCH1 = "<ArchiefDocumentsoort>Z10</ArchiefDocumentsoort><ArchiefVerzenddagBegin>2013-08-01T00:00:00</ArchiefVerzenddagBegin><ArchiefVerzenddagEinde>2016-08-15T00:00:00</ArchiefVerzenddagEinde>";
    private final static String REQUEST_SEARCH2 = "<ArchiefPersoonsnummer>1231189636</ArchiefPersoonsnummer>";
    private final static String REQUEST_SEARCH3 = "<ArchiefDocumentkenmerk>65847</ArchiefDocumentkenmerk><ArchiefVerzenddagBegin>2013-08-01T00:00:00</ArchiefVerzenddagBegin><ArchiefVerzenddagEinde>2016-08-15T00:00:00</ArchiefVerzenddagEinde>";
        
    @Test
    public void testDefaultResponse() throws Exception {
        DocumentService documentService = new DocumentService();
        Response responseEntity = documentService.defaultResponse();
        
        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getEntity());
        Assert.assertTrue(responseEntity.getEntity().toString().length() > 0);
    }
    
    @Test
    public void testConfigurationtResponse() throws Exception {
        ConfigService configService = new ConfigService();
        Response responseEntity = configService.checkConfig();
        
        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getEntity());
        Assert.assertTrue(responseEntity.getEntity().toString().length() > 0);
        Assert.assertFalse(responseEntity.getEntity().toString().contains("[ERROR]"));
    }
    
    @Test
    public void testDocumentList() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.listDocuments(REQUEST_LIST);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        
        Assert.assertTrue(data.length() > 1);
        Assert.assertFalse(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentSearch1() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.searchDocuments(REQUEST_SEARCH1);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        
        Assert.assertTrue(data.length() > 1);
        Assert.assertFalse(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentSearch2() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.searchDocuments(REQUEST_SEARCH2);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        
        Assert.assertTrue(data.length() > 1);
        Assert.assertFalse(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentSearch3() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.searchDocuments(REQUEST_SEARCH3);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 200);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        
        Assert.assertTrue(data.length() > 1);
        Assert.assertFalse(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testDocumentListContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.listDocuments(null);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 406 || documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentListContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.listDocuments("");
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 406 || documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentListErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.listDocuments("<ArchiefPersoonsnummer>16afavfdsa</ArchiefPersoonsnummer>");
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testDocumentsContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentsResponse = documentService.getDocument(null);
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 406 || documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentsContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentsResponse = documentService.getDocument("");
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 406 || documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentsResponse = documentService.getDocument("<ArchiefDocumentId>gsdfhgdsf</ArchiefDocumentId>");
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testSearchDocumentsContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        Response searchDocumentsResponse = documentService.searchDocuments(null);
        
        Assert.assertNotNull(searchDocumentsResponse);
        Assert.assertTrue(searchDocumentsResponse.getStatus() == 406 || searchDocumentsResponse.getStatus() == 500);
        Assert.assertNotNull(searchDocumentsResponse.getEntity());
        
        String data = (String) searchDocumentsResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testSearchDocumentsContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        Response searchDocumentsResponse = documentService.searchDocuments("");
        
        Assert.assertNotNull(searchDocumentsResponse);
        Assert.assertTrue(searchDocumentsResponse.getStatus() == 406 || searchDocumentsResponse.getStatus() == 500);
        Assert.assertNotNull(searchDocumentsResponse.getEntity());
        
        String data = (String) searchDocumentsResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    
    @Test
    public void testSearchDocumentsContentErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        Response searchDocumentsResponse = documentService.searchDocuments("<ArchiefDocumentkenmerk>sfbsdf</ArchiefDocumentkenmerk>");
        
        Assert.assertNotNull(searchDocumentsResponse);
        Assert.assertTrue(searchDocumentsResponse.getStatus() == 500);
        Assert.assertNotNull(searchDocumentsResponse.getEntity());
        
        String data = (String) searchDocumentsResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocument() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentResponse = documentService.getDocument(REQUEST_DOCUMENT);
    
        Assert.assertNotNull(documentResponse);
        Assert.assertTrue(documentResponse.getStatus() == 200);
        Assert.assertNotNull(documentResponse.getEntity());
        
        Assert.assertTrue(documentResponse.getEntity().toString().length() > 20);
        Assert.assertFalse(documentResponse.getEntity().toString().startsWith("<error>"));
    }
}
