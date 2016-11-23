package nl.hetcak.dms.ia.web;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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
    private final static String REQUEST_LIST = "<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefPersoonsnummer>8823502042</urn:ArchiefPersoonsnummer></urn:RaadplegenDocumentLijstRequest>";
    private final static String REQUEST_DOCUMENT = "<urn:RaadplegenDocumentRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefDocumentId>1305362675</urn:ArchiefDocumentId></urn:RaadplegenDocumentRequest>";

    private final static String BAD_REQUEST_LIST = "<urn:ArchiefPersoonsnummer  xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\">1892011538</urn:ArchiefPersoonsnummer>";
    private final static String BAD_REQUEST_DOCUMENT = "<urn:ArchiefDocumentId  xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\">1921071631</urn:ArchiefDocumentId>";
    
    @Test
    public void testJavaRsConfig() {
        RequestProcessor rp = new RequestProcessor();
        Assert.assertNotNull(rp);
    }
    
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
            Assert.assertTrue(data.contains("<urn:ResultCode>0000</urn:ResultCode>"));
            Assert.assertTrue(data.contains("<urn:ArchiefDocumentId>"));
            Assert.assertTrue(data.contains("<urn:ArchiefDocumentsoort>"));
            Assert.assertTrue(data.contains("<urn:ArchiefPersoonsnummer>"));
            Assert.assertTrue(data.contains("<urn:ArchiefDocumenttitel>"));
        } else {
            Assert.assertTrue(data.contains("<urn:ResultCode>9999</urn:ResultCode>"));
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
        Assert.assertTrue(data.contains("<urn:RaadplegenLijstDocumentResponse"));
        Assert.assertTrue(data.contains("<urn:ResultCode>9999</urn:ResultCode>"));
        Assert.assertTrue(data.contains("</urn:RaadplegenLijstDocumentResponse>"));
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
        Assert.assertTrue(data.contains("<urn:ResultCode>2007</urn:ResultCode>"));
    }
    
    @Test
    public void testDocumentListErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.listDocuments("<urn:RaadplegenDocumentLijstRequest xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\"><urn:ArchiefPersoonsnummer>16afavfdsa</urn:ArchiefPersoonsnummer></urn:RaadplegenDocumentLijstRequest>", request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.contains("<urn:ResultCode>2005</urn:ResultCode>"));
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
        Assert.assertTrue(data.contains("<urn:ResultCode>9999</urn:ResultCode>"));
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
        Assert.assertTrue(data.contains("<urn:ResultCode>2007</urn:ResultCode>"));
    }
    
    @Test
    public void testDocumentErrorRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentsResponse = documentService.getDocument("<urn:ArchiefDocumentId  xmlns:urn=\"urn:hetcak:dms:raadplegenuitingarchief:2016:11\">gsdfhgdsf</urn:ArchiefDocumentId>", request);
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.contains("<urn:ResultCode>2007</urn:ResultCode>"));
    }
    
    @Test
    public void testDocumentBadRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentsResponse = documentService.getDocument(BAD_REQUEST_DOCUMENT, request);
        
        Assert.assertNotNull(documentsResponse);
        Assert.assertTrue(documentsResponse.getStatus() == 500);
        Assert.assertNotNull(documentsResponse.getEntity());
        
        String data = (String) documentsResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.contains("<urn:ResultCode>2007</urn:ResultCode>"));
    }
    
    @Test
    public void testDocumentListBadRequest() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentListResponse = documentService.listDocuments(BAD_REQUEST_LIST, request);
        
        Assert.assertNotNull(documentListResponse);
        Assert.assertTrue(documentListResponse.getStatus() == 500);
        Assert.assertNotNull(documentListResponse.getEntity());
        
        String data = (String) documentListResponse.getEntity();
        LOGGER.info(data);
        Assert.assertTrue(data.contains("<urn:ResultCode>2007</urn:ResultCode>"));
    }
    
    @Test
    public void testDocument() throws Exception {
        DocumentService documentService = new DocumentService();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Response documentResponse = documentService.getDocument(REQUEST_DOCUMENT, request);
    
        Assert.assertNotNull(documentResponse);
        Assert.assertNotNull(documentResponse.getEntity());
        LOGGER.info(documentResponse.getEntity().toString());
        Assert.assertTrue(documentResponse.getStatus() == 200);
        Assert.assertTrue(documentResponse.getEntity().toString().length() > 20);
        Assert.assertTrue(documentResponse.getEntity().toString().contains("<urn:ResultCode>0000</urn:ResultCode>"));

    }
}
