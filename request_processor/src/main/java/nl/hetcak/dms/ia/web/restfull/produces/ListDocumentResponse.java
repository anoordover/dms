package nl.hetcak.dms.ia.web.restfull.produces;

import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class ListDocumentResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListDocumentResponse.class);
    List<InfoArchiveDocument> documents;
    
    public ListDocumentResponse(List<InfoArchiveDocument> documents) {
        this.documents = documents;
    }
    
    public String getAsXML() {
        StringBuilder stringBuilder = new StringBuilder();
        for(InfoArchiveDocument document : documents) {
            try {
                stringBuilder.append(document.getXMLString());
            } catch (JAXBException jaxbExc) {
                //parse exceptions
                LOGGER.error("Detected jaxb errors in document.", jaxbExc);
            }
        }
        return stringBuilder.toString();
    }
}
