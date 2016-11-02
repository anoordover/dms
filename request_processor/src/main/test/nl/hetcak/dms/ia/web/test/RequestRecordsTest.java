package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.exceptions.*;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.DocumentRequest;
import nl.hetcak.dms.ia.web.requests.RecordRequest;
import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This test will test the {@link RecordRequest} class.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class RequestRecordsTest {
    private static final String WORKING_CONFIG = "test/config/working.xml";
    
    //external connection
    @Test(timeout = 3000)
    public void getListDocumentsFromInfoArchive() throws Exception  {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
        RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(),credentials);
        List<InfoArchiveDocument> document = recordRequest.requestListDocuments("1971429972");
        assertNotNull(document);
        assertTrue(document.size() > 0);
    }
    
    
    //external connection
    @Test(expected = NoContentAvailableException.class ,timeout = 3000)
    public void getEmptyList() throws Exception {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
        RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(),credentials);
        List<InfoArchiveDocument> document = recordRequest.requestListDocuments("asdfdgre");
        assertNotNull(document);
        assertTrue(document.size() == 0);
    }
    
    @Test
    public void getOneResultAndGetDocument() throws Exception {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
        RecordRequest request = new RecordRequest(connectionManager.getConfiguration(), credentials);
        InfoArchiveDocument document = request.requestDocument("1663298436");
        assertNotNull(document);
        DocumentRequest documentRequest = new DocumentRequest(connectionManager.getConfiguration(), credentials);
        ByteArrayOutputStream documentStream = documentRequest.getContentWithContentId(document.getArchiefFile());
        assertNotNull(documentStream);
    }
}
