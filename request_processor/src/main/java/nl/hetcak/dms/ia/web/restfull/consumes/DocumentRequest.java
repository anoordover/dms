package nl.hetcak.dms.ia.web.restfull.consumes;

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
public class DocumentRequest {
    private String archiveDocumentNumber, continuationNumber;
    
    @XmlElement(name = "ArchiefDocumentId")
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
    
    public static DocumentRequest unmarshalRequest(String input) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentRequest.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        
        StringReader reader = new StringReader(input);
        return (DocumentRequest) unmarshaller.unmarshal(reader);
    }
}
