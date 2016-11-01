package nl.hetcak.dms.ia.web.restfull.consumers;

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
@XmlRootElement(name = "request")
public class DocumentRequestConsumer {
    private String archiveDocumentNumber, continuationNumber;

    public static DocumentRequestConsumer unmarshallerRequest(String input) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentRequestConsumer.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(input);
        return (DocumentRequestConsumer) unmarshaller.unmarshal(reader);
    }

    @XmlElement(name = "ArchiefDocumentId", required = true)
    public String getArchiveDocumentNumber() {
        return archiveDocumentNumber;
    }

    public void setArchiveDocumentNumber(String archiveDocumentNumber) {
        this.archiveDocumentNumber = archiveDocumentNumber;
    }

    @XmlElement(name = "Volgnummer", nillable = true)
    public String getContinuationNumber() {
        return continuationNumber;
    }

    public void setContinuationNumber(String continuationNumber) {
        this.continuationNumber = continuationNumber;
    }

    public boolean hasContent() {
        if (archiveDocumentNumber == null) {
            return false;
        }

        return archiveDocumentNumber.length() != 0;
    }
}
