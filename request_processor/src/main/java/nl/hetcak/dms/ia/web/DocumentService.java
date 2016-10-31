package nl.hetcak.dms.ia.web;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.*;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import nl.hetcak.dms.ia.web.managers.ConnectionManager;
import nl.hetcak.dms.ia.web.requests.DocumentRequest;
import nl.hetcak.dms.ia.web.requests.RecordRequest;
import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.restfull.consumers.DocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.ListDocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.consumers.SearchDocumentRequestConsumer;
import nl.hetcak.dms.ia.web.restfull.produces.ListDocumentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by admjzimmermann on 13-10-2016.
 *
 * @author Joury.Zimmermann@AMPLEXOR.com
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@Path("/rest")
public class DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
    
    private static final String XML_REQUEST_START = "<request>";
    private static final String XML_REQUEST_STOP = "</request>";
    private static final String ERROR_RESPONSE_GENERIC = "Something went wrong, please notify an administrator.";
    private static final String ERROR_RESPONSE_MESSAGE_TEMPLATE = "<error><code>%d</code><description>%s</description></error>";
    private static final String LOGGER_IO_OR_PARSE_EXC = "IO or Parsing error.";
    private static final String LOGGER_VALID_INCOMING_REQUEST = "Incoming request content is valid.";
    private static final String LOGGER_INVALID_INCOMING_REQUEST = "Incoming request content is invalid!";
    private static final String ERROR_CONTENT_GRABBING = "Content grabbing attempt detected. Canceling request.";
    
    private RecordRequest createRecordRequest() throws RequestResponseException{
        try {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
            return recordRequest;
        } catch (RequestResponseException reqResExc){
            LOGGER.error(reqResExc.getUserErrorMessage(), reqResExc);
            throw reqResExc;
        }
    }
    
    private ListDocumentResponse listDocumentResponse(RecordRequest recordRequest, ListDocumentRequestConsumer request) throws RequestResponseException {
        try{
            return new ListDocumentResponse(recordRequest.requestListDocuments(request.getArchivePersonNumber()));
        } catch (RequestResponseException reqresExc) {
            LOGGER.error(reqresExc.getUserErrorMessage(),reqresExc);
            throw reqresExc;
        } catch (InfoArchiveResponseException iaRespExc) {
            LOGGER.error("Got error response from InfoArchive.",iaRespExc);
            throw new RequestResponseException(iaRespExc.getErrorCode(), "Got error response from InfoArchive.");
        } catch (Exception exc) {
            LOGGER.error("Io or parsing error", exc);
            throw new RequestResponseException(-1, LOGGER_IO_OR_PARSE_EXC);
        }
    }
    
    @POST
    @Path("/listDocuments")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response listDocuments(String sBody) {
        LOGGER.info(Version.PROGRAM_NAME+" "+Version.currentVersion());
        LOGGER.info("Incoming request for /listDocuemnts.");
        LOGGER.debug(sBody);
        StringBuilder input = new StringBuilder();
        input.append(XML_REQUEST_START);
        input.append(sBody);
        input.append(XML_REQUEST_STOP);
        try {
            ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest(input.toString());
            if (request.hasContent()) {
                LOGGER.info(LOGGER_VALID_INCOMING_REQUEST);
                RecordRequest recordRequest = createRecordRequest();
                ListDocumentResponse response = listDocumentResponse(recordRequest, request);
                return Response.ok(response.getAsXML()).build();
            } else {
                LOGGER.info(LOGGER_INVALID_INCOMING_REQUEST);
                throw new ContentGrabbingException(ERROR_CONTENT_GRABBING);
            }
        } catch (RequestResponseException rrExc) {
            LOGGER.error(rrExc.getMessage(), rrExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, rrExc.getErrorCode(), rrExc.getUserErrorMessage())).build();
        } catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error(ERROR_CONTENT_GRABBING, exc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, -1, ERROR_RESPONSE_GENERIC)).build();
        }
    }
    
    private DocumentRequest createDocumentRequest() throws RequestResponseException{
        try {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            DocumentRequest iaDocumentRequest = new DocumentRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
            return iaDocumentRequest;
        } catch (RequestResponseException reqresExc){
            LOGGER.error(reqresExc.getUserErrorMessage(), reqresExc);
            throw reqresExc;
        }
    }
    
    private InfoArchiveDocument documentResponse(RecordRequest recordRequest, DocumentRequestConsumer documentRequestConsumer) throws RequestResponseException {
        try{
            return recordRequest.requestDocument(documentRequestConsumer.getArchiveDocumentNumber());
        } catch (RequestResponseException reqresExc) {
            LOGGER.error(reqresExc.getUserErrorMessage(),reqresExc);
            throw reqresExc;
        } catch (InfoArchiveResponseException iaRespExc) {
            LOGGER.error("Search for document resulted in a InfoArchive Exception.",iaRespExc);
            throw new RequestResponseException(iaRespExc.getErrorCode(), "Search for document resulted in a InfoArchive Exception.");
        } catch (Exception exc) {
            LOGGER.error(LOGGER_IO_OR_PARSE_EXC, exc);
            throw new RequestResponseException(-1, LOGGER_IO_OR_PARSE_EXC);
        }
    }
    
    private ByteArrayOutputStream documentTransfer(DocumentRequest documentRequest, InfoArchiveDocument infoArchiveDocument) throws RequestResponseException {
        try{
            return documentRequest.getContentWithContentId(infoArchiveDocument.getArchiefFile());
        } catch (RequestResponseException reqResExc) {
            LOGGER.error(reqResExc.getUserErrorMessage(),reqResExc);
            throw reqResExc;
        } catch (Exception exc) {
            LOGGER.error(LOGGER_IO_OR_PARSE_EXC, exc);
            throw new RequestResponseException(-1, LOGGER_IO_OR_PARSE_EXC);
        }
    }
    
    
    @POST
    @Path("/document")
    @Produces("application/pdf")
    @Consumes(MediaType.APPLICATION_XML)
    public Response getDocument(String sBody) {
        LOGGER.info(Version.PROGRAM_NAME + " " + Version.currentVersion());
        LOGGER.info("Incoming request for /document.");
        LOGGER.debug(sBody);
        StringBuilder input = new StringBuilder();
        input.append(XML_REQUEST_START);
        input.append(sBody);
        input.append(XML_REQUEST_STOP);

        try {
            DocumentRequestConsumer documentRequestConsumer = DocumentRequestConsumer.unmarshallerRequest(input.toString());
            if (documentRequestConsumer.hasContent()) {
                LOGGER.info(LOGGER_VALID_INCOMING_REQUEST);
                RecordRequest recordRequest = createRecordRequest();
                DocumentRequest documentRequest = createDocumentRequest();
                InfoArchiveDocument document = documentResponse(recordRequest, documentRequestConsumer);
                
                ByteArrayOutputStream byteArray = documentTransfer(documentRequest, document);
                LOGGER.info("Getting Stream ready to send.");
                StreamingOutput streamingOutput = outputStream ->  {
                        LOGGER.info("Sending Stream as response.");
                            outputStream.write(byteArray.toByteArray());
                            outputStream.close();
                };
                return Response.ok(streamingOutput).build();
            } else {
                LOGGER.info(LOGGER_INVALID_INCOMING_REQUEST);
                throw new ContentGrabbingException(ERROR_CONTENT_GRABBING);
            }
        } catch (RequestResponseException rrExc) {
            LOGGER.error(rrExc.getMessage(), rrExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, rrExc.getErrorCode(), rrExc.getUserErrorMessage())).build();
        } catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error("The incoming xml could not pe parsed.", exc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, -1, ERROR_RESPONSE_GENERIC)).build();
        }
    }
    
    @POST
    @Path("/searchDocuments")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response searchDocuments(String sBody) {
        LOGGER.info(Version.PROGRAM_NAME+" "+Version.currentVersion());
        LOGGER.info("Incoming request for /searchDocuments.");
        LOGGER.debug(sBody);
        StringBuilder input = new StringBuilder();
        input.append(XML_REQUEST_START);
        input.append(sBody);
        input.append(XML_REQUEST_STOP);
        
        try {
            SearchDocumentRequestConsumer request = SearchDocumentRequestConsumer.unmarshalRequest(input.toString());
            //disable content grabbing with empty strings.
            if (request.hasContent()) {
                LOGGER.info(LOGGER_VALID_INCOMING_REQUEST);
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
                ListDocumentResponse response = new ListDocumentResponse(recordRequest.requestListDocuments(request.getDocumentKind(), request.getPersonNumber(), request.getDocumentCharacteristics(), request.getDocumentSendDate1AsInfoArchiveString(), request.getDocumentSendDate2AsInfoArchiveString()));
                return Response.ok(response.getAsXML()).build();
    
                } else {
                    LOGGER.info(LOGGER_INVALID_INCOMING_REQUEST);
                    throw new ContentGrabbingException(ERROR_CONTENT_GRABBING);
                }
            } catch (ContentGrabbingException cgExc) {
            LOGGER.error("Content grabbing attempt detected. Returning '406 - unaccepted' http error.", cgExc);
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, ERROR_CONTENT_GRABBING, ERROR_RESPONSE_GENERIC)).build();
        } catch (RequestResponseException rrExc) {
            LOGGER.error(rrExc.getMessage(), rrExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, rrExc.getErrorCode(), rrExc.getUserErrorMessage())).build();
        } catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error("The incoming xml could not pe parsed.", exc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, -1, ERROR_RESPONSE_GENERIC)).build();
        }
    }
    
    /**
     * Basic response
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response defaultResponse() {
        return Response.ok("<html><head><title>DMS</title></head><body><h1>DMS Request Processor</h1><p>System running.</p></body></html>").build();
    }
}
