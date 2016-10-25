package nl.hetcak.dms.ia.web.restfull.consumers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.StringReader;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "request")
public class ListDocumentRequestConsumer {
    protected  String archivePersonNumber;
    
    @XmlElement(name = "ArchiefPersoonsnummer")
    public String getArchivePersonNumber() {
        return archivePersonNumber;
    }
    
    public void setArchivePersonNumber(String archivePersonNumber) {
        this.archivePersonNumber = archivePersonNumber;
    }
    
    
    public boolean hasContent() {
        if(archivePersonNumber == null){
            return false;
        }
        
        if(archivePersonNumber.length() == 0) {
            return false;
        }
        return true;
    }
    
    public static ListDocumentRequestConsumer unmarshalRequest(String input) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ListDocumentRequestConsumer.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    
        StringReader reader = new StringReader(input);
        return (ListDocumentRequestConsumer) unmarshaller.unmarshal(reader);
    }
}
