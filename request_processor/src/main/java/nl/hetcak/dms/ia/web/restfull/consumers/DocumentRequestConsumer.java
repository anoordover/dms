package nl.hetcak.dms.ia.web.restfull.consumers;

import nl.hetcak.dms.ia.web.exceptions.RequestParsingException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.util.SchemaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "RaadplegenDocumentRequest", namespace = "urn:hetcak:dms:raadplegenuitingarchief:2016:11")
public class DocumentRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentRequestConsumer.class);
    private String archiveDocumentNumber;

    public static DocumentRequestConsumer unmarshallerRequest(String input) throws RequestResponseException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentRequestConsumer.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(SchemaUtil.getSchema());
            
            StringReader reader = new StringReader(input);
            return (DocumentRequestConsumer) unmarshaller.unmarshal(reader);
        }catch (JAXBException jaxbExc) {
            LOGGER.error(input);
            throw new RequestParsingException(jaxbExc);
        }
    }

    @XmlElement(name = "ArchiefDocumentId", required = true)
    public String getArchiveDocumentNumber() {
        return archiveDocumentNumber;
    }

    public void setArchiveDocumentNumber(String archiveDocumentNumber) {
        this.archiveDocumentNumber = archiveDocumentNumber;
    }


    public boolean hasContent() {
        if (archiveDocumentNumber == null) {
            return false;
        }

        return archiveDocumentNumber.length() != 0;
    }
}
