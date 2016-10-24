package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.DocumentService;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class RestfullTest {
    private final static String REQUEST_LIST = "<ArchiefPersoonsnummer>1231189636</ArchiefPersoonsnummer>";
    private final static String REQUEST_DOCUMENT = "<ArchiefDocumentId>1268712898</ArchiefDocumentId><Volgnummer>001</Volgnummer>";
        
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
        DocumentService documentService = new DocumentService();
        Response responseEntity = documentService.testData();
        
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
        
        Assert.assertTrue(data.length() > 20);
        Assert.assertFalse(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentListContentGrabbingCase1() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.listDocuments(null);
    
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 406);
        Assert.assertNotNull(documentListResponse.getEntity());
    
        String data = (String) documentListResponse.getEntity();
        Assert.assertTrue(data.startsWith("<error>"));
    }
    
    @Test
    public void testDocumentListContentGrabbingCase2() throws Exception {
        DocumentService documentService = new DocumentService();
        Response documentListResponse = documentService.listDocuments("");
    
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 406);
        Assert.assertNotNull(documentListResponse.getEntity());
    
        String data = (String) documentListResponse.getEntity();
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
