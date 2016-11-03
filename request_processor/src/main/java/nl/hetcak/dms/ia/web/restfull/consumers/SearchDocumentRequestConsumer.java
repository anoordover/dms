package nl.hetcak.dms.ia.web.restfull.consumers;

import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@XmlRootElement(name = "Request")
public class SearchDocumentRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDocumentRequestConsumer.class);
    //ArchiefPersoonsnummer
    private String personNumber = "";
    //ArchiefDocumentTitle
    private String documentTitle = "";
    //ArchiefDocumentkenmerk
    private String documentCharacteristics = "";
    private String documentSendDate1 = "";
    private String documentSendDate2 = "";

    public static SearchDocumentRequestConsumer unmarshalRequest(String input) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SearchDocumentRequestConsumer.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(input);
        return (SearchDocumentRequestConsumer) unmarshaller.unmarshal(reader);
    }

    @XmlElement(name = "ArchiefDocumentTitle", required = true)
    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    @XmlElement(name = "ArchiefVerzenddagBegin", required = true)
    public String getDocumentSendDate1() {
        return documentSendDate1;
    }

    public void setDocumentSendDate1(String documentSendDate1) {
        this.documentSendDate1 = documentSendDate1;
    }

    public String getDocumentSendDate1AsInfoArchiveString() {
        try {
            return InfoArchiveDateUtil.convertToInfoArchiveDate(documentSendDate1);
        } catch (ParseException parseExc) {
            LOGGER.error("Can't parse string to InfoArchive format. is string empty?", parseExc);
            return null;
        }
    }

    @XmlElement(name = "ArchiefVerzenddagEinde", required = true)
    public String getDocumentSendDate2() {
        return documentSendDate2;
    }

    public void setDocumentSendDate2(String documentSendDate2) {
        this.documentSendDate2 = documentSendDate2;
    }

    public String getDocumentSendDate2AsInfoArchiveString() {
        try {
            return InfoArchiveDateUtil.convertToInfoArchiveDate(documentSendDate2);
        } catch (ParseException parseExc) {
            LOGGER.error("Can't parse string to InfoArchive format. is string empty?", parseExc);
            return null;
        }
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

    public boolean hasContent() {
        Boolean content = true;

        if (this.documentTitle == null && this.documentCharacteristics == null && this.personNumber == null) {
            content = false;
        }
        if (this.documentTitle.length() == 0 && this.documentCharacteristics.length() == 0 && this.personNumber.length() == 0) {
            content = false;
        }

        return hasDate() || content;
    }

    private boolean hasDate() {
        Boolean date = true;

        if (this.documentSendDate1 == null) {
            date = false;
        }
        if (this.documentSendDate2 == null) {
            date = false;
        }
        if (this.documentSendDate1.length() == 0) {
            date = false;
        }
        if (this.documentSendDate2.length() == 0) {
            date = false;
        }
        return date;
    }
}
