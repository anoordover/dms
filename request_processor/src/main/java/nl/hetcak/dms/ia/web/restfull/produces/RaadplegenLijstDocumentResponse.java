package nl.hetcak.dms.ia.web.restfull.produces;

import nl.hetcak.dms.ia.web.restfull.produces.containers.ArchiefDocumenten;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "RaadplegenLijstDocumentResponse")
public class RaadplegenLijstDocumentResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaadplegenLijstDocumentResponse.class);
    private ArchiefDocumenten mobjArchiefDocumenten;
    private int miResultCode;
    private String msResultDescription;

    @XmlElement(name = "ResultCode")
    public int getResultCode() {
        return miResultCode;
    }

    public void setResultCode(int resultCode) {
        this.miResultCode = resultCode;
    }

    @XmlElement(name = "ResultDescription")
    public String getResultDescription() {
        return msResultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.msResultDescription = resultDescription;
    }

    @XmlElement(name = "ArchiefDocumenten")
    public ArchiefDocumenten getMobjArchiefDocumenten() {
        return mobjArchiefDocumenten;
    }

    public void setMobjArchiefDocumenten(ArchiefDocumenten mobjArchiefDocumenten) {
        this.mobjArchiefDocumenten = mobjArchiefDocumenten;
    }

    public String getAsXML() {
        LOGGER.info("Building response.");
        java.io.StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(RaadplegenLijstDocumentResponse.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, sw);
        } catch (JAXBException jaxExc) {
            LOGGER.error("JAXB failed to create xml.", jaxExc);
        }
        LOGGER.info("Returning response.");
        return sw.toString();
    }
}
