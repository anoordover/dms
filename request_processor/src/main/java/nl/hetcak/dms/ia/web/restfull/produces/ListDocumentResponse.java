package nl.hetcak.dms.ia.web.restfull.produces;

import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "ArchiefDocument")
public class ListDocumentResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListDocumentResponse.class);
    private List<InfoArchiveDocument> documents = new ArrayList<>();
    private ErrorResponse error;

    @XmlElement(name = "Error")
    public ErrorResponse getError() {
        return error;
    }

    @XmlElement(name = "Document")
    public List<InfoArchiveDocument> getDocuments() {
        return documents;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public String getAsXML() {
        LOGGER.info("Building query for InfoArchive.");
        java.io.StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(ListDocumentResponse.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, sw);
        } catch (JAXBException jaxExc) {
            LOGGER.error("JAXB failed to create xml.", jaxExc);
        }
        LOGGER.info("Returning xml query String for InfoArchive.");
        return sw.toString();
    }
}
