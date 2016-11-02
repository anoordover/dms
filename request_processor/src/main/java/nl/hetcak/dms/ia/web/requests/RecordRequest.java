package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.*;
import nl.hetcak.dms.ia.web.query.InfoArchiveQueryBuilder;
import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;
import nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class RecordRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordRequest.class);
    private static final String SEARCH_POST_REQUEST = "restapi/systemdata/search-compositions";
    private static final String CONTENT_TYPE_APP_XML = "application/xml";
    private static final String VALUE_ARCHIVE_PERSON_NUMBER = "ArchiefPersoonsnummer";
    private static final String VALUE_ARCHIVE_DOCUMENT_NUMBER = "ArchiefDocumentId";

    private static final String PARSE_RESPONSE_EMBEDDED = "_embedded";
    private static final String PARSE_RESPONSE_RESULTS = "results";
    private static final String PARSE_RESPONSE_ROWS = "rows";
    private static final String PARSE_RESPONSE_COLUMNS = "columns";
    private static final String PARSE_RESPONSE_NAME = "name";
    private static final String PARSE_RESPONSE_VALUE = "value";

    private static final String PARSE_RESPONSE_PAGE = "page";
    private static final String PARSE_RESPONSE_TOTAL_ELEMENTS = "totalElements";

    private static final String PARSE_RESPONSE_ERROR = "_errors";
    private static final String PARSE_RESPONSE_ERROR_TITLE = "error";
    private static final String PARSE_RESPONSE_ERROR_MESSAGE = "message";

    private static final String PARSE_DOCUMENT_ID = "ArchiefDocumentId";
    private static final String PARSE_DOCUMENT_PERSON_NUMBER = "ArchiefPersoonsnummer";
    private static final String PARSE_DOCUMENT_TITLE = "ArchiefDocumenttitel";
    private static final String PARSE_DOCUMENT_KIND = "ArchiefDocumentsoort";
    private static final String PARSE_DOCUMENT_PROTOCOL = "ArchiefRegeling";
    private static final String PARSE_DOCUMENT_CHARACTERISTIC = "ArchiefDocumentkenmerk";
    private static final String PARSE_DOCUMENT_SEND_DATE = "ArchiefVerzenddag";
    private static final String PARSE_DOCUMENT_TYPE = "ArchiefDocumenttype";
    private static final String PARSE_DOCUMENT_STATUS = "ArchiefDocumentstatus";
    private static final String PARSE_DOCUMENT_YEAR = "ArchiefRegelingsjaar";
    private static final String PARSE_DOCUMENT_HANDLING_NUMBER = "ArchiefHandelingsnummer";
    private static final String PARSE_DOCUMENT_ATTACHMENT = "Attachment";

    private static final String LOGGING_PARSING_RESULT = "Parsing results";
    private static final String LOGGING_EXPECT_AT_LEAST_ONE_RESULT = ", the request handler expected at least one result.";

    private Configuration configuration;
    private Credentials credentials;
    private InfoArchiveRequestUtil requestUtil;
    private InfoArchiveQueryBuilder queryBuilder;

    public RecordRequest(Configuration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
        this.requestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
        this.queryBuilder = new InfoArchiveQueryBuilder();
    }

    public List<InfoArchiveDocument> requestListDocuments(String archivePersonNumber) throws JAXBException, IOException, ServerConnectionFailureException, ParseException, TooManyResultsException, InfoArchiveResponseException, NoContentAvailableException {
        LOGGER.info("Starting List Documents request for person number:" + archivePersonNumber);
        String response = requestUtil.responseReader(executeListDocumentsRequest(archivePersonNumber));
        LOGGER.info(LOGGING_PARSING_RESULT);
        List<InfoArchiveDocument> result = parseDocumentList(response, true, false);
        if (result.size() == 0) {
            StringBuilder errorMessage = new StringBuilder("Got 0 results for documents with person number:");
            errorMessage.append(archivePersonNumber);
            errorMessage.append(LOGGING_EXPECT_AT_LEAST_ONE_RESULT);
            LOGGER.error(errorMessage.toString());
            LOGGER.debug(response);
            throw new NoContentAvailableException(errorMessage.toString(),NoContentAvailableException.ERROR_CODE_NO_DOCUMENT_LIST);
        }
        LOGGER.info("Returning List with " + result.size() + " documents.");
        return result;
    }

    public List<InfoArchiveDocument> requestListDocuments(String documentTitle, String personNumber, String documentCharacteristics, String sendDate1, String sendDate2) throws JAXBException, IOException, ServerConnectionFailureException, ParseException, TooManyResultsException, InfoArchiveResponseException, NoContentAvailableException {
        StringBuilder logString = new StringBuilder("Starting List Documents request for");
        if (StringUtils.isNotBlank(documentTitle)) {
            logString.append(" document title \"");
            logString.append(documentTitle);
            logString.append("\"");
        }
        if (StringUtils.isNotBlank(personNumber)) {
            logString.append(" person number \"");
            logString.append(personNumber);
            logString.append("\"");
        }
        if (StringUtils.isNotBlank(documentCharacteristics)) {
            logString.append(" document characteristics \"");
            logString.append(documentCharacteristics);
            logString.append("\"");
        }
        if (StringUtils.isNotBlank(sendDate1) && StringUtils.isNotBlank(sendDate2)) {
            logString.append(" senddate between \"");
            logString.append(sendDate1);
            logString.append("\" ");
            logString.append("till \"");
            logString.append(sendDate2);
            logString.append("\"");
        }

        LOGGER.info(logString.toString());

        String response = requestUtil.responseReader(executeListDocumentsRequest(documentTitle, personNumber, documentCharacteristics, sendDate1, sendDate2));
        LOGGER.info(LOGGING_PARSING_RESULT);
        List<InfoArchiveDocument> result = parseDocumentList(response, true, true);
        if (result.size() == 0) {
            String errorMessage = "Got 0 results, invalid search.";
            LOGGER.error(errorMessage);
            LOGGER.debug(response);
            throw new NoContentAvailableException(errorMessage, NoContentAvailableException.ERROR_CODE_NO_DOCUMENT_LIST);
        }
        LOGGER.info("Returning List with " + result.size() + " documents.");
        return result;
    }

    public InfoArchiveDocument requestDocument(String archiveDocumentNumber) throws JAXBException, IOException, ServerConnectionFailureException, ParseException, MultipleDocumentsException, TooManyResultsException, InfoArchiveResponseException, NoContentAvailableException {
        LOGGER.info("Requesting document with number:" + archiveDocumentNumber);
        String response = requestUtil.responseReader(executeDocumentsRequest(archiveDocumentNumber));
        LOGGER.info(LOGGING_PARSING_RESULT);
        List<InfoArchiveDocument> documents = parseDocumentList(response, false, false);
        if (documents.size() > 1) {
            String errorMessage = "Got " + documents.size() + " results for document number:" + archiveDocumentNumber + LOGGING_EXPECT_AT_LEAST_ONE_RESULT;
            LOGGER.error(errorMessage);
            LOGGER.debug(response);
            throw new MultipleDocumentsException(errorMessage);
        } else if (documents.size() == 0) {
            String errorMessage = "Got " + documents.size() + " results for document number:" + archiveDocumentNumber + LOGGING_EXPECT_AT_LEAST_ONE_RESULT;
            LOGGER.error(errorMessage);
            LOGGER.debug(response);
            throw new NoContentAvailableException(errorMessage, NoContentAvailableException.ERROR_CODE_NO_CONTENT);
        }
        LOGGER.info("Returning document.");
        return documents.get(0);
    }

    private HttpResponse executeListDocumentsRequest(String archivePersonNumber) throws JAXBException, IOException, ServerConnectionFailureException {
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl(SEARCH_POST_REQUEST, configuration.getSearchCompositionUUID());
        String requestBody = queryBuilder.addEqualCriteria(VALUE_ARCHIVE_PERSON_NUMBER, archivePersonNumber).build();
        LOGGER.info("Executing HTTPPOST request for a List Documents based on person number.");
        LOGGER.debug(requestBody);
        return requestUtil.executePostRequest(url, CONTENT_TYPE_APP_XML, requestHeader, requestBody);
    }

    private HttpResponse executeListDocumentsRequest(String documentTitle, String personNumber, String documentCharacteristics, String sendDate1, String sendDate2) throws JAXBException, IOException, ServerConnectionFailureException {
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl(SEARCH_POST_REQUEST, configuration.getSearchCompositionUUID());
        InfoArchiveQueryBuilder currentQuery = new InfoArchiveQueryBuilder();
        if (documentTitle != null) {
            currentQuery = currentQuery.addEqualCriteria(PARSE_DOCUMENT_TITLE, documentTitle);
        }
        if (personNumber != null) {
            currentQuery = currentQuery.addEqualCriteria(PARSE_DOCUMENT_PERSON_NUMBER, personNumber);
        }
        if (documentCharacteristics != null) {
            currentQuery = currentQuery.addEqualCriteria(PARSE_DOCUMENT_CHARACTERISTIC, documentCharacteristics);
        }
        if (sendDate1 != null && sendDate2 != null) {
            currentQuery = currentQuery.addBetweenCriteria(PARSE_DOCUMENT_SEND_DATE, sendDate1, sendDate2);
        }
        String requestBody = currentQuery.build();
        LOGGER.info("Executing HTTPPOST request for a List Documents based on Document type and a between senddate constraint.");
        LOGGER.debug(requestBody);
        return requestUtil.executePostRequest(url, CONTENT_TYPE_APP_XML, requestHeader, requestBody);
    }

    private HttpResponse executeDocumentsRequest(String archiveDocumentNumber) throws JAXBException, IOException, ServerConnectionFailureException {
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl(SEARCH_POST_REQUEST, configuration.getSearchCompositionUUID());
        String requestBody = queryBuilder.addEqualCriteria(VALUE_ARCHIVE_DOCUMENT_NUMBER, archiveDocumentNumber).build();
        LOGGER.info("Executing HTTPPOST request for a Documents based on Document Number.");
        LOGGER.debug(requestBody);
        return requestUtil.executePostRequest(url, CONTENT_TYPE_APP_XML, requestHeader, requestBody);
    }

    private List<InfoArchiveDocument> parseDocumentList(String response, boolean expectList, boolean isSearchResults) throws ParseException, TooManyResultsException, InfoArchiveResponseException {
        List<InfoArchiveDocument> documents = new ArrayList<>();
        TooManyResultsException tooManyResultsException = null;

        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();

        //responseErrorCheck
        if (jsonResponse.has(PARSE_RESPONSE_ERROR)) {
            LOGGER.info("Got error in response.");
            StringBuilder exceptionMessage = new StringBuilder();
            JsonArray errors = jsonResponse.getAsJsonArray(PARSE_RESPONSE_ERROR);
            String errorTitle = "";
            for (int i_error = 0; i_error < errors.size(); i_error++) {
                JsonObject error = errors.get(i_error).getAsJsonObject();

                errorTitle = error.get(PARSE_RESPONSE_ERROR_TITLE).getAsString();
                String errorMessage = error.get(PARSE_RESPONSE_ERROR_MESSAGE).getAsString();

                exceptionMessage.append(errorTitle);
                exceptionMessage.append(" ");
                exceptionMessage.append(errorMessage);
                exceptionMessage.append("\n");
            }

            LOGGER.debug(response);
            LOGGER.error(exceptionMessage.toString());

            int errorCode = InfoArchiveResponseException.defineErrorCode(exceptionMessage.toString(), expectList);
            InfoArchiveResponseException infoArchiveResponseException = new InfoArchiveResponseException(errorCode, InfoArchiveResponseException.ERROR_MESSAGE);
            throw infoArchiveResponseException;
        }

        //check response size
        int totalElements = totalElementsFromResponse(jsonResponse);
        if (configuration.getMaxResults() < totalElements) {
            String errorMessage = "InfoArchive responded with " + totalElements + " items, this exceeds the maximum allowed items of " + configuration.getMaxResults() + " set in the configuration.";
            LOGGER.error(errorMessage);
            LOGGER.debug(response);
            tooManyResultsException = new TooManyResultsException(TooManyResultsException.defineErrorCode(isSearchResults) ,errorMessage);

        }

        //read response
        if (jsonResponse.has(PARSE_RESPONSE_EMBEDDED)) {
            JsonObject embedded = jsonResponse.getAsJsonObject(PARSE_RESPONSE_EMBEDDED);
            if (embedded.has(PARSE_RESPONSE_RESULTS)) {
                JsonArray results = embedded.getAsJsonArray(PARSE_RESPONSE_RESULTS);

                for (int i_Results = 0; i_Results < results.size(); i_Results++) {
                    JsonObject result = results.get(i_Results).getAsJsonObject();
                    documents.addAll(parseResponseListDocumentsRow(result));
                }
            }
        }

        if(tooManyResultsException != null) {
            tooManyResultsException.getDocumentsToDisplay().addAll(documents);
            throw tooManyResultsException;
        }
        return documents;
    }

    private List<InfoArchiveDocument> parseResponseListDocumentsRow(JsonObject result) {
        List<InfoArchiveDocument> documents = new ArrayList<>();

        if (result.has(PARSE_RESPONSE_ROWS)) {
            JsonArray rows = result.get(PARSE_RESPONSE_ROWS).getAsJsonArray();
            for (int i_Rows = 0; i_Rows < rows.size(); i_Rows++) {
                JsonObject row = rows.get(i_Rows).getAsJsonObject();
                try {
                    documents.add(parseDocument(row));
                } catch (ParseException parseExc) {
                    LOGGER.error("Failed to parse document", parseExc);
                }
            }
        }
        return documents;
    }

    private int totalElementsFromResponse(JsonObject jsonResponse) {
        if (jsonResponse.has(PARSE_RESPONSE_PAGE)) {
            JsonObject page = jsonResponse.getAsJsonObject(PARSE_RESPONSE_PAGE);
            if (page.has(PARSE_RESPONSE_TOTAL_ELEMENTS)) {
                return page.get(PARSE_RESPONSE_TOTAL_ELEMENTS).getAsInt();
            }
        }
        return 0;
    }

    private InfoArchiveDocument parseDocument(JsonObject document) throws ParseException {
        LOGGER.info("Parsing Document.");
        InfoArchiveDocument infoArchiveDocument = new InfoArchiveDocument();
        JsonArray columns = document.getAsJsonArray(PARSE_RESPONSE_COLUMNS);
        for (int i_column = 0; i_column < columns.size(); i_column++) {
            JsonObject column = columns.get(i_column).getAsJsonObject();

            if (column.has(PARSE_RESPONSE_NAME)) {
                String columnName = column.get(PARSE_RESPONSE_NAME).getAsString();
                LOGGER.debug("Parsing column: " + columnName);
                infoArchiveDocument = parseFirstTenFields(infoArchiveDocument, columnName, column);
                infoArchiveDocument = parseSecondTenField(infoArchiveDocument, columnName, column);
            }
        }
        LOGGER.info("Done parsing document.");

        return infoArchiveDocument;
    }

    //(so it has come to this...) here are some methods to fix cyclomatic complexity...
    private InfoArchiveDocument parseFirstTenFields(InfoArchiveDocument infoArchiveDocument, String columnName, JsonObject column) throws ParseException {
        switch (columnName) {
            case PARSE_DOCUMENT_ID:
                infoArchiveDocument.setArchiefDocumentId(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_PERSON_NUMBER:
                infoArchiveDocument.setArchiefPersoonsnummer(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_TITLE:
                infoArchiveDocument.setArchiefDocumenttitel(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_KIND:
                infoArchiveDocument.setArchiefDocumentsoort(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_PROTOCOL:
                infoArchiveDocument.setArchiefRegeling(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_CHARACTERISTIC:
                infoArchiveDocument.setArchiefDocumentkenmerk(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_SEND_DATE:
                infoArchiveDocument.setArchiefVerzenddag(InfoArchiveDateUtil.convertToRequestDate(column.get(PARSE_RESPONSE_VALUE).getAsString()));
                break;
            case PARSE_DOCUMENT_TYPE:
                infoArchiveDocument.setArchiefDocumenttype(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_STATUS:
                infoArchiveDocument.setArchiefDocumentstatus(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            default:
                break;
        }
        return infoArchiveDocument;
    }

    private InfoArchiveDocument parseSecondTenField(InfoArchiveDocument infoArchiveDocument, String columnName, JsonObject column) {
        switch (columnName) {
            case PARSE_DOCUMENT_YEAR:
                infoArchiveDocument.setArchiefRegelingsjaar(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_ATTACHMENT:
                infoArchiveDocument.setArchiefFile(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            case PARSE_DOCUMENT_HANDLING_NUMBER:
                infoArchiveDocument.setArchiefHandelingsnummer(column.get(PARSE_RESPONSE_VALUE).getAsString());
                break;
            default:
                break;
        }
        return infoArchiveDocument;

    }
}
