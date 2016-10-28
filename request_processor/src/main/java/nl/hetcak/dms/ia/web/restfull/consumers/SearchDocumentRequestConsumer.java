package nl.hetcak.dms.ia.web.restfull.consumers;

import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.text.ParseException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "request")
public class SearchDocumentRequestConsumer {
    private String personNumber = "";
    private String documentKind = "";
    private String documentCharacteristics = "";
    private String documentSendDate1 = "";
    private String documentSendDate2 = "";
    
    @XmlElement(name = "ArchiefDocumentsoort", required = true)
    public String getDocumentKind() {
        return documentKind;
    }
    
    public void setDocumentKind(String documentKind) {
        this.documentKind = documentKind;
    }
    
    @XmlElement(name = "ArchiefVerzenddagBegin", required = true)
    public String getDocumentSendDate1() {
        return documentSendDate1;
    }
    
    public String getDocumentSendDate1AsInfoArchiveString() throws ParseException{
        return InfoArchiveDateUtil.convertToInfoArchiveDate(documentSendDate1);
    }
    
    public void setDocumentSendDate1(String documentSendDate1) {
        this.documentSendDate1 = documentSendDate1;
    }
    
    @XmlElement(name = "ArchiefVerzenddagEinde", required = true)
    public String getDocumentSendDate2() {
        return documentSendDate2;
    }
    
    public String getDocumentSendDate2AsInfoArchiveString() throws ParseException{
        return InfoArchiveDateUtil.convertToInfoArchiveDate(documentSendDate2);
    }
    
    public void setDocumentSendDate2(String documentSendDate2) {
        this.documentSendDate2 = documentSendDate2;
    }
    
    
    @XmlElement(name = "ArchiefPersoonsnummer", required = true)
    public String getPersonNumber() {
        return personNumber;
    }
    
    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }
    
    @XmlElement(name = "ArchiefDocumentkenmerk", required = true)
    public String getDocumentCharacteristics() {
        return documentCharacteristics;
    }
    
    
    public void setDocumentCharacteristics(String documentCharacteristics) {
        this.documentCharacteristics = documentCharacteristics;
    }
    
    public static SearchDocumentRequestConsumer unmarshalRequest(String input) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SearchDocumentRequestConsumer.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        
        StringReader reader = new StringReader(input);
        return (SearchDocumentRequestConsumer) unmarshaller.unmarshal(reader);
    }
    
    public boolean hasContent() {
        Boolean content = true;
    
        if(this.documentSendDate1 == null) {
            content =  false;
        }
        if(this.documentSendDate2 == null) {
            content =  false;
        }
        if(this.documentSendDate1.length() == 0) {
            content =  false;
        }
        if(this.documentSendDate2.length() == 0) {
            content =  false;
        }
        if(this.documentKind == null && this.documentCharacteristics == null && this.personNumber == null) {
            content = false;
        }
        if(this.documentKind.length() == 0 && this.documentCharacteristics.length() == 0 && this.personNumber.length() == 0) {
            content = false;
        }
        
        return content;
    }
}
