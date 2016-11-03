package nl.hetcak.dms.ia.web.restfull.consumers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "Request")
public class ListDocumentRequestConsumer {
    protected String archivePersonNumber;

    /**
     * Unmarshal the xml to a {@link ListDocumentRequestConsumer} object.
     *
     * @param input The xml input.
     * @return a converted xml {#link String} to {@link ListDocumentRequestConsumer}.
     * @throws JAXBException Errors during the unmarshalling.
     */
    public static ListDocumentRequestConsumer unmarshalRequest(String input) throws JAXBException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ListDocumentRequestConsumer.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(input);
            return (ListDocumentRequestConsumer) unmarshaller.unmarshal(reader);
        } catch (UnmarshalException unmExc) {
            throw new JAXBException("Error in xml.", unmExc);
        }
    }

    /**
     * Gets the archive person number.
     *
     * @return the archive person number.
     */
    @XmlElement(name = "ArchiefPersoonsnummer", required = true)
    public String getArchivePersonNumber() {
        return archivePersonNumber;
    }

    /**
     * Sets the archive person number.
     *
     * @param archivePersonNumber the archive person number.
     */
    public void setArchivePersonNumber(String archivePersonNumber) {
        this.archivePersonNumber = archivePersonNumber;
    }

    /**
     * Checks if this consumer object has content.
     *
     * @return true, if there is content.
     */
    public boolean hasContent() {
        if (archivePersonNumber == null) {
            return false;
        }

        return archivePersonNumber.length() != 0;
    }
}
