package nl.hetcak.dms.ia.web.requests;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.exceptions.*;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.restfull.consumers.ArchiefPersoonsnummers;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 * <p>
 * This test will test the {@link RecordRequest} class.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class RequestRecordsTest {
    private static final String WORKING_CONFIG = "test/config/working.xml";

    //external connection
    @Test(timeout = 3000)
    public void getListDocumentsFromInfoArchive() throws Exception {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
        RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), credentials);
        ListDocumentRequestConsumer requestConsumer = new ListDocumentRequestConsumer();
        requestConsumer.setArchiefPersoonsnummers(new ArchiefPersoonsnummers(Arrays.asList("2842398989")));
        List<InfoArchiveDocument> document = recordRequest.requestListDocuments(requestConsumer);
        assertNotNull(document);
        assertTrue(document.size() > 0);
    }

    //external connection
    @Test(timeout = 3000)
    public void getListDocumentsFromInfoArchive2() throws Exception {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
        RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), credentials);
        ListDocumentRequestConsumer requestConsumer = new ListDocumentRequestConsumer();
        requestConsumer.setArchiefPersoonsnummers(new ArchiefPersoonsnummers(Arrays.asList("2842398989")));
        requestConsumer.setArchiefDocumentId("1532575101");
        List<InfoArchiveDocument> document = recordRequest.requestListDocuments(requestConsumer);
        assertNotNull(document);
        assertTrue(document.size() > 0);
    }

    //external connection
    @Test(expected = NoContentAvailableException.class, timeout = 3000)
    public void getEmptyList() throws Exception {
        File config = new File(WORKING_CONFIG);
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setConfigurationFile(config);
        Credentials credentials = connectionManager.getActiveCredentials();
        assertNotNull(credentials);
        assertTrue(credentials.isSecurityTokenValid());
        RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), credentials);
        ListDocumentRequestConsumer requestConsumer = new ListDocumentRequestConsumer();
        requestConsumer.setArchiefPersoonsnummers(new ArchiefPersoonsnummers(Arrays.asList("asdfdgre")));
        List<InfoArchiveDocument> document = recordRequest.requestListDocuments(requestConsumer);
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
        InfoArchiveDocument document = request.requestDocument("1849272060");
        assertNotNull(document);
        DocumentRequest documentRequest = new DocumentRequest(connectionManager.getConfiguration(), credentials);
        ByteArrayOutputStream documentStream = documentRequest.getContentWithContentId(document.getArchiefFile());
        assertNotNull(documentStream);
    }
}
