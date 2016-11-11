package nl.hetcak.dms.ia.web;

import nl.hetcak.dms.ia.web.exceptions.ContentGrabbingException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.DocumentRequest;
import nl.hetcak.dms.ia.web.requests.RecordRequest;
import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.restfull.consumers.DocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.produces.RaadplegenDocumentResponse;
import nl.hetcak.dms.ia.web.restfull.produces.RaadplegenLijstDocumentResponse;
import nl.hetcak.dms.ia.web.restfull.produces.containers.ArchiefDocumenten;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Calendar;

/**
 * Created by admjzimmermann on 13-10-2016.
 *
 * @author Joury.Zimmermann@AMPLEXOR.com
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@Path("/rest")
public class DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
    
    private static final String ERROR_RESPONSE_GENERIC = "Something went wrong, please notify an administrator.";
    private static final String ERROR_RESPONSE_MESSAGE_TEMPLATE = "<error><code>%d</code><description>%s</description></error>";
    private static final String LOGGER_IO_OR_PARSE_EXC = "IO or Parsing error.";
    private static final String LOGGER_VALID_INCOMING_REQUEST = "Incoming request content is valid.";
    private static final String LOGGER_INVALID_INCOMING_REQUEST = "Incoming request content is invalid!";
    private static final String ERROR_CONTENT_GRABBING = "The request is missing values. please notify an administrator if you can't correct it.";
    
    private RecordRequest createRecordRequest() throws RequestResponseException {
        try {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            return new RecordRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
        } catch (RequestResponseException reqResExc) {
            LOGGER.error(reqResExc.getUserErrorMessage(), reqResExc);
            throw reqResExc;
        }
    }
    
    private RaadplegenLijstDocumentResponse listDocumentResponse(RecordRequest recordRequest, ListDocumentRequestConsumer request) throws RequestResponseException {
        try {
            RaadplegenLijstDocumentResponse response = new RaadplegenLijstDocumentResponse();
            ArchiefDocumenten documents = new ArchiefDocumenten();
            documents.getDocumentList().addAll(recordRequest.requestListDocuments(request));
            return response;
        } catch (RequestResponseException reqresExc) {
            LOGGER.error(reqresExc.getUserErrorMessage(), reqresExc);
            throw reqresExc;
        } catch (Exception exc) {
            LOGGER.error("Io or parsing error", exc);
            throw new RequestResponseException(9999, LOGGER_IO_OR_PARSE_EXC);
        }
    }
    
    private DocumentRequest createDocumentRequest() throws RequestResponseException {
        try {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            return new DocumentRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
        } catch (RequestResponseException reqresExc) {
            LOGGER.error(reqresExc.getUserErrorMessage(), reqresExc);
            throw reqresExc;
        }
    }
    
    private InfoArchiveDocument documentResponse(RecordRequest recordRequest, DocumentRequestConsumer documentRequestConsumer) throws RequestResponseException {
        try {
            return recordRequest.requestDocument(documentRequestConsumer.getArchiveDocumentNumber());
        } catch (RequestResponseException reqresExc) {
            LOGGER.error(reqresExc.getUserErrorMessage(), reqresExc);
            throw reqresExc;
        } catch (Exception exc) {
            LOGGER.error(LOGGER_IO_OR_PARSE_EXC, exc);
            throw new RequestResponseException(9999, LOGGER_IO_OR_PARSE_EXC);
        }
    }
    
    private ByteArrayOutputStream documentTransfer(DocumentRequest documentRequest, InfoArchiveDocument infoArchiveDocument) throws RequestResponseException {
        try {
            return documentRequest.getContentWithContentId(infoArchiveDocument.getArchiefFile());
        } catch (RequestResponseException reqResExc) {
            LOGGER.error(reqResExc.getUserErrorMessage(), reqResExc);
            throw reqResExc;
        } catch (Exception exc) {
            LOGGER.error(LOGGER_IO_OR_PARSE_EXC, exc);
            throw new RequestResponseException(9999, LOGGER_IO_OR_PARSE_EXC);
        }
    }
    
    /**
     * Basic response
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response defaultResponse(@Context HttpServletRequest httpRequest) {
        return Response.ok("<html><head><title>DMS</title></head><body><h1>DMS Request Processor</h1><p>System running.</p></body></html>").build();
    }
    
    @POST
    @Path("/listDocuments")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response listDocuments(String sBody, @Context HttpServletRequest httpRequest) {
        Calendar calendar = Calendar.getInstance();
        LOGGER.info(Version.PROGRAM_NAME + " " + Version.currentVersion());
        LOGGER.info("Incoming request for /listDocuments. (" + calendar.getTime().toString() + ")");
        LOGGER.debug(sBody);
        LOGGER.info("Got Request from " + httpRequest.getRemoteAddr());
        try {
            ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest(sBody);
            if (request.hasContent()) {
                LOGGER.info(LOGGER_VALID_INCOMING_REQUEST);
                RecordRequest recordRequest = createRecordRequest();
                RaadplegenLijstDocumentResponse response = listDocumentResponse(recordRequest, request);
                return Response.ok(response.getAsXML()).build();
            } else {
                LOGGER.info(LOGGER_INVALID_INCOMING_REQUEST);
                throw new ContentGrabbingException(ERROR_CONTENT_GRABBING);
            }
        } catch (RequestResponseException rrExc) {
            LOGGER.error(rrExc.getMessage(), rrExc);
            RaadplegenLijstDocumentResponse response = new RaadplegenLijstDocumentResponse();
            response.setResultCode(rrExc.getErrorCode());
            response.setResultDescription(rrExc.getUserErrorMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(response.getAsXML()).build();
        } catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error(ERROR_CONTENT_GRABBING, exc);
            RaadplegenLijstDocumentResponse response = new RaadplegenLijstDocumentResponse();
            response.setResultCode(9999);
            response.setResultDescription(ERROR_RESPONSE_GENERIC);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(response.getAsXML()).build();
        }
    }
    
    @POST
    @Path("/document")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response getDocument(String sBody, @Context HttpServletRequest httpRequest) {
        Calendar calendar = Calendar.getInstance();
        LOGGER.info(Version.PROGRAM_NAME + " " + Version.currentVersion());
        LOGGER.info("Incoming request for /document. (" + calendar.getTime().toString() + ")");
        LOGGER.debug(sBody);
        LOGGER.info("Got Request from " + httpRequest.getRemoteAddr());
        try {
            DocumentRequestConsumer documentRequestConsumer = DocumentRequestConsumer.unmarshallerRequest(sBody);
            if (documentRequestConsumer.hasContent()) {
                LOGGER.info(LOGGER_VALID_INCOMING_REQUEST);
                RecordRequest recordRequest = createRecordRequest();
                DocumentRequest documentRequest = createDocumentRequest();
                InfoArchiveDocument document = documentResponse(recordRequest, documentRequestConsumer);
                
                LOGGER.info("Preparing document for response message.");
                ByteArrayOutputStream byteArray = documentTransfer(documentRequest, document);
                LOGGER.info("Encoding PDF and storing document into the response object.");
                LOGGER.info("Encoding "+byteArray.size()+" bytes.");
                String encodedDocument = new String(Base64.getEncoder().encode(byteArray.toByteArray()));
                RaadplegenDocumentResponse documentResponse = new RaadplegenDocumentResponse();
                documentResponse.setResultCode(0);
                documentResponse.setArchiefDocumentId(document.getArchiefDocumentId());
                documentResponse.setPayloadPdf(encodedDocument);
                LOGGER.info("Sending response.");
                return Response.ok(documentResponse.getAsXML()).build();
            } else {
                LOGGER.info(LOGGER_INVALID_INCOMING_REQUEST);
                throw new ContentGrabbingException(ERROR_CONTENT_GRABBING);
            }
        } catch (RequestResponseException rrExc) {
            LOGGER.error(rrExc.getMessage(), rrExc);
            RaadplegenLijstDocumentResponse response = new RaadplegenLijstDocumentResponse();
            response.setResultCode(rrExc.getErrorCode());
            response.setResultDescription(rrExc.getUserErrorMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(response.getAsXML()).build();
        } catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error(ERROR_CONTENT_GRABBING, exc);
            RaadplegenLijstDocumentResponse response = new RaadplegenLijstDocumentResponse();
            response.setResultCode(9999);
            response.setResultDescription(ERROR_RESPONSE_GENERIC);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(response.getAsXML()).build();
        }
    }
    
}
