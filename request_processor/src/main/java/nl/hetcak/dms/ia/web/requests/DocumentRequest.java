package nl.hetcak.dms.ia.web.requests;

import nl.hetcak.dms.ia.web.comunication.Credentials;
import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.BigFileException;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.exceptions.ServerConnectionFailureException;
import nl.hetcak.dms.ia.web.util.InfoArchiveRequestUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class DocumentRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentRequest.class);
    private Configuration configuration;
    private Credentials credentials;
    private InfoArchiveRequestUtil requestUtil;

    public DocumentRequest(Configuration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
        this.requestUtil = new InfoArchiveRequestUtil(configuration.getInfoArchiveServerInformation());
    }

    /**
     * Get content from InfoArchive.
     *
     * @param contentID the id of the content.
     * @return A byte stream.
     * @throws MisconfigurationException        problems during reading configuration.
     * @throws ServerConnectionFailureException problems when connection to InfoArchive.
     * @throws IOException                      Failed to read server response or to open a stream.
     */
    public ByteArrayOutputStream getContentWithContentId(String contentID) throws RequestResponseException, IOException {
        LOGGER.info("Executing content request.");
        Map<String, String> requestHeader = requestUtil.createCredentialsMap(credentials);
        String url = requestUtil.getServerContentUrl(configuration.getApplicationUUID(), contentID);
        HttpResponse response = requestUtil.executeGetRequest(url, InfoArchiveRequestUtil.CONTENT_TYPE_JSON, requestHeader);
        LOGGER.info("Returning content byte stream.");
        return responseToStream(response);
    }

    private ByteArrayOutputStream responseToStream(HttpResponse response) throws RequestResponseException, IOException {
        LOGGER.info("Start buffering InfoArchive document stream.");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(response.getEntity().getContent(), byteArrayOutputStream);
        LOGGER.info("Returning buffered InfoArchive document stream.");
        if(byteArrayOutputStream.size() > configuration.getMaxFileSize()){
            throw new BigFileException("File with "+byteArrayOutputStream.size()+ " bytes is blocked because it exceeds the limit of "+configuration.getMaxFileSize()+" bytes.");
        }
        return byteArrayOutputStream;
    }

}
