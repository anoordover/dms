package nl.hetcak.dms.ia.web.requests.containers;

import nl.hetcak.dms.ia.web.query.InfoArchiveQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by admjzimmermann on 13-10-2016.
 *
 * @author Joury.Zimmermann@AMPLEXOR.com
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "ArchiefDocument")
public class InfoArchiveDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoArchiveDocument.class);

    private String msArchiefDocumenttitel;
    private String msArchiefDocumentId;
    private String msArchiefDocumentkenmerk;
    private String msArchiefDocumentsoort;
    private String msArchiefDocumenttype;
    private String msArchiefRegeling;
    private String msArchiefRegelingsjaar;
    private String msArchiefPersoonsnummer;
    private String msArchiefDocumentstatus;
    private String mobjArchiefVerzenddag;
    private String msArchiefFile;

    @XmlElement(name = "ArchiefPersoonsnummer")
    public String getArchiefPersoonsnummer() {
        return msArchiefPersoonsnummer;
    }

    public void setArchiefPersoonsnummer(String sArchiefPersoonsnummer) {
        this.msArchiefPersoonsnummer = sArchiefPersoonsnummer;
    }

    @XmlElement(name="ArchiefDocumentId")
    public String getArchiefDocumentId() {
        return msArchiefDocumentId;
    }

    public void setArchiefDocumentId(String sArchiefDocumentId) {
        this.msArchiefDocumentId = sArchiefDocumentId;
    }

    @XmlElement(name = "ArchiefDocumenttitel")
    public String getArchiefDocumenttitel() {
        return msArchiefDocumenttitel;
    }

    public void setArchiefDocumenttitel(String sArchiefDocumenttitel) {
        this.msArchiefDocumenttitel = sArchiefDocumenttitel;
    }

    @XmlElement(name = "ArchiefDocumentsoort")
    public String getArchiefDocumentsoort() {
        return msArchiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(String sArchiefDocumentsoort) {
        this.msArchiefDocumentsoort = sArchiefDocumentsoort;
    }

    @XmlElement(name = "ArchiefRegeling")
    public String getArchiefRegeling() {
        return msArchiefRegeling;
    }

    public void setArchiefRegeling(String sArchiefRegeling) {
        this.msArchiefRegeling = sArchiefRegeling;
    }

    @XmlElement(name = "ArchiefDocumentkenmerk")
    public String getArchiefDocumentkenmerk() {
        return msArchiefDocumentkenmerk;
    }

    public void setArchiefDocumentkenmerk(String sArchiefDocumentkenmerk) {
        this.msArchiefDocumentkenmerk = sArchiefDocumentkenmerk;
    }

    @XmlElement(name = "ArchiefVerzenddag")
    public String getArchiefVerzenddag() {
        return mobjArchiefVerzenddag;
    }

    public void setArchiefVerzenddag(String objArchiefVerzenddag) {
        this.mobjArchiefVerzenddag = objArchiefVerzenddag;
    }

    @XmlElement(name = "ArchiefDocumenttype")
    public String getArchiefDocumenttype() {
        return msArchiefDocumenttype;
    }

    public void setArchiefDocumenttype(String sArchiefDocumenttype) {
        this.msArchiefDocumenttype = sArchiefDocumenttype;
    }

    @XmlElement(name = "ArchiefDocumentstatus")
    public String getArchiefDocumentstatus() {
        return msArchiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(String sArchiefDocumentstatus) {
        this.msArchiefDocumentstatus = sArchiefDocumentstatus;
    }

    @XmlElement(name = "ArchiefRegelingsjaar")
    public String getArchiefRegelingsjaar() {
        return msArchiefRegelingsjaar;
    }

    public void setArchiefRegelingsjaar(String sArchiefRegelingsjaar) {
        this.msArchiefRegelingsjaar = sArchiefRegelingsjaar;
    }
    
    @XmlTransient
    public String getArchiefFile() {
        return msArchiefFile;
    }

    public void setArchiefFile(String sArchiefFile) {
        this.msArchiefFile = sArchiefFile;
    }

    /**
     * Get the current class as XML string.
     * @return XML formatted String
     * @throws JAXBException Failed to create XML exception.
     */
    public String getXMLString() throws JAXBException {
        java.io.StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(InfoArchiveDocument.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, sw);
        } catch (JAXBException jaxExc) {
            LOGGER.error("JAXB failed to create xml.", jaxExc);
            throw jaxExc;
        }
        return sw.toString();
    }
}
