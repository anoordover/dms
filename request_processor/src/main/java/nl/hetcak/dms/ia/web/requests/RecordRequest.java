package nl.hetcak.dms.ia.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.exceptions.UnexpectedResultException;
import nl.hetcak.dms.ia.web.query.InfoArchiveQueryBuilder;
import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;
import nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil;
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
    
    public List<InfoArchiveDocument> requestListDocuments(String archivePersonNumber) throws JAXBException, IOException, ServerConnectionFailureException, ParseException {
        String response = requestUtil.responseReader(executeListDocumentsRequest(archivePersonNumber));
        return parseDocumentList(response);
    }
    
    public List<InfoArchiveDocument> requestListDocuments(String documentType, String sendDate1, String sendDate2) throws JAXBException, IOException, ServerConnectionFailureException, ParseException {
        String response = requestUtil.responseReader(executeListDocumentsRequest(documentType, sendDate1, sendDate2));
        return parseDocumentList(response);
    }
    
    public InfoArchiveDocument requestDocument(String archiveDocumentNumber) throws JAXBException, IOException, ServerConnectionFailureException, ParseException, UnexpectedResultException {
        String response = requestUtil.responseReader(executeDocumentsRequest(archiveDocumentNumber));
        List<InfoArchiveDocument> documents = parseDocumentList(response);
        if(documents.size() == 0 || documents.size() > 1) {
            throw new UnexpectedResultException("Got "+documents.size()+" results, however the request handler expected one result.");
        }
        return documents.get(0);
    }
    
    private HttpResponse executeListDocumentsRequest(String archivePersonNumber) throws JAXBException, IOException, ServerConnectionFailureException {
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl(SEARCH_POST_REQUEST, configuration.getSearchCompositionUUID());
        String requestBody = queryBuilder.addEqualCriteria(VALUE_ARCHIVE_PERSON_NUMBER, archivePersonNumber).build();
        return requestUtil.executePostRequest(url, CONTENT_TYPE_APP_XML, requestHeader, requestBody);
    }
    
    private HttpResponse executeListDocumentsRequest(String documentType, String sendDate1, String sendDate2) throws JAXBException, IOException, ServerConnectionFailureException {
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl(SEARCH_POST_REQUEST, configuration.getSearchCompositionUUID());
        String requestBody = queryBuilder.addEqualCriteria(PARSE_DOCUMENT_TITLE, documentType).addBetweenCriteria(PARSE_DOCUMENT_SEND_DATE, sendDate1, sendDate2).build();
        return requestUtil.executePostRequest(url, CONTENT_TYPE_APP_XML, requestHeader, requestBody);
    }
    
    private HttpResponse executeDocumentsRequest(String archiveDocumentNumber) throws JAXBException, IOException, ServerConnectionFailureException {
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerUrl(SEARCH_POST_REQUEST, configuration.getSearchCompositionUUID());
        String requestBody = queryBuilder.addEqualCriteria(VALUE_ARCHIVE_DOCUMENT_NUMBER, archiveDocumentNumber).build();
        return requestUtil.executePostRequest(url, CONTENT_TYPE_APP_XML, requestHeader, requestBody);
    }
    
    private List<InfoArchiveDocument> parseDocumentList(String response) throws ParseException {
        List<InfoArchiveDocument> documents = new ArrayList<>();
        
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        if (jsonResponse.has(PARSE_RESPONSE_EMBEDDED)) {
            JsonObject embedded = jsonResponse.getAsJsonObject(PARSE_RESPONSE_EMBEDDED);
            if (embedded.has(PARSE_RESPONSE_RESULTS)) {
                JsonArray results = embedded.getAsJsonArray(PARSE_RESPONSE_RESULTS);
                for (int i_Results = 0; i_Results < results.size(); i_Results++) {
                    JsonObject result = results.get(i_Results).getAsJsonObject();
                    if (result.has(PARSE_RESPONSE_ROWS)) {
                        JsonArray rows = result.get(PARSE_RESPONSE_ROWS).getAsJsonArray();
                        for (int i_Rows = 0; i_Rows < rows.size(); i_Rows++) {
                            JsonObject row = rows.get(i_Rows).getAsJsonObject();
                            documents.add(parseDocument(row));
                        }
                    }
                }
            }
        }
        
        return documents;
    }
    
    private InfoArchiveDocument parseDocument(JsonObject document) throws ParseException {
        InfoArchiveDocument infoArchiveDocument = new InfoArchiveDocument();
        JsonArray columns = document.getAsJsonArray(PARSE_RESPONSE_COLUMNS);
        for (int i_column = 0; i_column < columns.size(); i_column++) {
            JsonObject column = columns.get(i_column).getAsJsonObject();
            
            if (column.has(PARSE_RESPONSE_NAME)) {
                if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_ID)) {
                    infoArchiveDocument.setArchiefDocumentId(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_PERSON_NUMBER)) {
                    infoArchiveDocument.setArchiefPersoonsnummer(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_TITLE)) {
                    infoArchiveDocument.setArchiefDocumenttitel(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_KIND)) {
                    infoArchiveDocument.setArchiefDocumentsoort(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_PROTOCOL)) {
                    infoArchiveDocument.setArchiefRegeling(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_CHARACTERISTIC)) {
                    infoArchiveDocument.setArchiefDocumentkenmerk(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_SEND_DATE)) {
                    infoArchiveDocument.setArchiefVerzenddag(
                        InfoArchiveDateUtil.convertToRequestDate(column.get(PARSE_RESPONSE_VALUE).getAsString()));
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_TYPE)) {
                    infoArchiveDocument.setArchiefDocumenttype(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_STATUS)) {
                    infoArchiveDocument.setArchiefDocumentstatus(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_YEAR)) {
                    infoArchiveDocument.setArchiefRegelingsjaar(column.get(PARSE_RESPONSE_VALUE).getAsString());
                } else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_ATTACHMENT)) {
                    infoArchiveDocument.setArchiefFile(column.get(PARSE_RESPONSE_VALUE).getAsString());
                }else if (column.get(PARSE_RESPONSE_NAME).getAsString().contentEquals(PARSE_DOCUMENT_HANDLING_NUMBER)) {
                    infoArchiveDocument.setArchiefHandelingsnummer(column.get(PARSE_RESPONSE_VALUE).getAsString());
                }
            }
        }
        
        return infoArchiveDocument;
    }
}
