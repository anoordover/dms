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

    private static final String ERROR_RESPONSE_GENERIC = "Something went wrong, please notify an administrator.";
    private static final String ERROR_RESPONSE_MESSAGE_TEMPLATE = "<error><code>%d</code><description>%s</description></error>";
    
    @POST
    @Path("/listDocuments")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response listDocuments(String sBody) {
        LOGGER.info(Version.PROGRAM_NAME+" "+Version.currentVersion());
        LOGGER.info("Incoming request for /listDocuemnts.");
        LOGGER.debug(sBody);
        StringBuilder input = new StringBuilder();
        input.append("<request>");
        input.append(sBody);
        input.append("</request>");
        try {
            ListDocumentRequestConsumer request = ListDocumentRequestConsumer.unmarshalRequest(input.toString());
            if (request.hasContent()) {
                LOGGER.info("Request content is valid");
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
                ListDocumentResponse response = new ListDocumentResponse(recordRequest.requestListDocuments(request.getArchivePersonNumber()));
                return Response.ok(response.getAsXML()).build();
            } else {
                LOGGER.info("Request content is invalid");
                throw new ContentGrabbingException("Content grabbing attempt detected. Canceling request.");
            }
        } catch (ContentGrabbingException cgExc) {
            LOGGER.error("Content grabbing attempt detected. Returning http error.");
            return Response.status(Response.Status.NOT_ACCEPTABLE).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, cgExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MisconfigurationException misConExc) {
            LOGGER.error(misConExc.ERROR_MESSAGE, misConExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, misConExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MissingConfigurationException missingConExc) {
            LOGGER.error(missingConExc.ERROR_MESSAGE, missingConExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, missingConExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (ServerConnectionFailureException serConFailExc) {
            LOGGER.error(serConFailExc.ERROR_MESSAGE, serConFailExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, serConFailExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (LoginFailureException loginFailExc) {
            LOGGER.error(loginFailExc.ERROR_MESSAGE, loginFailExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, loginFailExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (ToManyResultsException tmrExc) {
            LOGGER.error(tmrExc.ERROR_MESSAGE, tmrExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, tmrExc.ERROR_CODE, tmrExc.ERROR_MESSAGE)).build();
        } catch (NoContentAvailableException noConAvaExc) {
            LOGGER.error(noConAvaExc.ERROR_MESSAGE, noConAvaExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, noConAvaExc.ERROR_CODE, noConAvaExc.ERROR_MESSAGE)).build();
        }catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error("Something went wrong during the request.", exc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, -1, ERROR_RESPONSE_GENERIC)).build();
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
        input.append("<request>");
        input.append(sBody);
        input.append("</request>");

        try {
            DocumentRequestConsumer documentRequestConsumer = DocumentRequestConsumer.unmarshallerRequest(input.toString());
            if (documentRequestConsumer.hasContent()) {
                LOGGER.info("Request content is valid");
                ConnectionManager connectionManager = ConnectionManager.getInstance();

                RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
                InfoArchiveDocument infoArchiveDocument = recordRequest.requestDocument(documentRequestConsumer.getArchiveDocumentNumber());
                DocumentRequest iaDocumentRequest = new DocumentRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
                ByteArrayOutputStream byteArray = iaDocumentRequest.getContentWithContentId(infoArchiveDocument.getArchiefFile());
                StreamingOutput outputStream = new StreamingOutput() {
                    @Override
                    public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                        try {
                            outputStream.write(byteArray.toByteArray());
                            outputStream.close();
                        } catch (Exception e) {
                            throw new WebApplicationException(e);
                        }
                    }
                };
                return Response.ok(outputStream).build();
            } else {
                LOGGER.info("Request content is invalid");
                throw new ContentGrabbingException("Content grabbing attempt detected. Canceling request.");
            }
        } catch (ContentGrabbingException cgExc) {
            LOGGER.error("Content grabbing attempt detected. Returning http error.");
            return Response.status(Response.Status.NOT_ACCEPTABLE).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, cgExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MisconfigurationException misConExc) {
            LOGGER.error(misConExc.ERROR_MESSAGE, misConExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, misConExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MissingConfigurationException missingConExc) {
            LOGGER.error(missingConExc.ERROR_MESSAGE, missingConExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, missingConExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (ServerConnectionFailureException serConFailExc) {
            LOGGER.error(serConFailExc.ERROR_MESSAGE, serConFailExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, serConFailExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (LoginFailureException loginFailExc) {
            LOGGER.error(loginFailExc.ERROR_MESSAGE, loginFailExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, loginFailExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MultipleDocumentsException multiDocExc) {
            LOGGER.error(multiDocExc.ERROR_MESSAGE, multiDocExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, multiDocExc.ERROR_CODE, multiDocExc.ERROR_MESSAGE)).build();
        } catch (ToManyResultsException tmrExc) {
            LOGGER.error(tmrExc.ERROR_MESSAGE, tmrExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, tmrExc.ERROR_CODE, tmrExc.ERROR_MESSAGE)).build();
        } catch (NoContentAvailableException noConAvaExc) {
            LOGGER.error(noConAvaExc.ERROR_MESSAGE, noConAvaExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, noConAvaExc.ERROR_CODE, noConAvaExc.ERROR_MESSAGE)).build();
        }catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error("Something went wrong during the request.", exc);
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
        input.append("<request>");
        input.append(sBody);
        input.append("</request>");
        
        try {
            SearchDocumentRequestConsumer request = SearchDocumentRequestConsumer.unmarshalRequest(input.toString());
            //disable content grabbing with empty strings.
            if (request.hasContent()) {
                LOGGER.info("Request content is valid");
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                RecordRequest recordRequest = new RecordRequest(connectionManager.getConfiguration(), connectionManager.getActiveCredentials());
                ListDocumentResponse response = new ListDocumentResponse(recordRequest.requestListDocuments(request.getDocumentKind(), request.getDocumentSendDate1AsInfoArchiveString(), request.getDocumentSendDate2AsInfoArchiveString()));
                return Response.ok(response.getAsXML()).build();
    
                } else {
                    LOGGER.info("Request content is invalid");
                    throw new ContentGrabbingException("Content grabbing attempt detected. Canceling request.");
                }
            } catch (ContentGrabbingException cgExc) {
            LOGGER.error("Content grabbing attempt detected. Returning '406 - unaccepted' http error.");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, cgExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MisconfigurationException misConExc) {
            LOGGER.error(misConExc.ERROR_MESSAGE, misConExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, misConExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (MissingConfigurationException missingConExc) {
            LOGGER.error(missingConExc.ERROR_MESSAGE, missingConExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, missingConExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (ServerConnectionFailureException serConFailExc) {
            LOGGER.error(serConFailExc.ERROR_MESSAGE, serConFailExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, serConFailExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (LoginFailureException loginFailExc) {
            LOGGER.error(loginFailExc.ERROR_MESSAGE, loginFailExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, loginFailExc.ERROR_CODE, ERROR_RESPONSE_GENERIC)).build();
        } catch (ToManyResultsException tmrExc) {
            LOGGER.error(tmrExc.ERROR_MESSAGE, tmrExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, tmrExc.ERROR_CODE, tmrExc.ERROR_MESSAGE)).build();
        } catch (NoContentAvailableException noConAvaExc) {
            LOGGER.error(noConAvaExc.ERROR_MESSAGE, noConAvaExc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(String.format(ERROR_RESPONSE_MESSAGE_TEMPLATE, noConAvaExc.ERROR_CODE, noConAvaExc.ERROR_MESSAGE)).build();
        }catch (Exception exc) {
            //catch all error and return error output.
            LOGGER.error("Something went wrong during the request.", exc);
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
    
    @GET
    @Path("/checkConfig")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkConfig() {
        LOGGER.info(Version.PROGRAM_NAME+" "+Version.currentVersion());
        LOGGER.info("Running log checker.");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading config file...\n");
        ConfigurationManager configurationManager = new ConfigurationManager();
        try {
            Configuration configuration = configurationManager.loadConfiguration(null);
            if(!configuration.hasBasicInformation()) {
                throw new MisconfigurationException("Some elements of the configuration have not been configured.");
            }
            stringBuilder.append("[OK] Config file found and loaded.\n");
        } catch (MissingConfigurationException missingcexc) {
            stringBuilder.append("[ERROR] Config file not found.\n");
        } catch (MisconfigurationException mcexc) {
            stringBuilder.append("Config file found.\n");
            stringBuilder.append("[ERROR] Config file is invalid.\n");
        }
        LOGGER.info(stringBuilder.toString());
        return Response.ok(stringBuilder.toString()).build();
    }
}
