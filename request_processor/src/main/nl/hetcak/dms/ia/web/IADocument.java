package nl.hetcak.dms.ia.web;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by admjzimmermann on 13-10-2016.
 */
@XmlRootElement(name = "Document")
public class IADocument {
    @XmlElement(name="ArchiefDocumentId")
    private String msArchiefDocumentId;
    @XmlElement(name = "ArchiefPersoonsnummer")
    private String msArchiefPersoonsnummer;
    @XmlElement(name = "ArchiefDocumenttitel")
    private String msArchiefDocumenttitel;
    @XmlElement(name = "ArchiefDocumentsoort")
    private String msArchiefDocumentsoort;
    @XmlElement(name = "ArchiefRegeling")
    private String msArchiefRegeling;
    @XmlElement(name = "ArchiefDocumentkenmerk")
    private String msArchiefDocumentkenmerk;
    @XmlElement(name = "ArchiefVerzenddag")
    private Date mobjArchiefVerzenddag;
    @XmlElement(name = "ArchiefDocumenttype")
    private String msArchiefDocumenttype;
    @XmlElement(name = "ArchiefDocumentstatus")
    private String msArchiefDocumentstatus;
    @XmlElement(name = "ArchiefRegelingsjaar")
    private String msArchiefRegelingsjaar;
    @XmlElement(name = "Attachment")
    private String msArchiefFile;

    public String getArchiefPersoonsnummer() {
        return msArchiefPersoonsnummer;
    }

    public void setArchiefPersoonsnummer(String sArchiefPersoonsnummer) {
        this.msArchiefPersoonsnummer = sArchiefPersoonsnummer;
    }

    public String getArchiefDocumentId() {
        return msArchiefDocumentId;
    }

    public void setArchiefDocumentId(String sArchiefDocumentId) {
        this.msArchiefDocumentId = sArchiefDocumentId;
    }

    public String getArchiefDocumenttitel() {
        return msArchiefDocumenttitel;
    }

    public void setArchiefDocumenttitel(String sArchiefDocumenttitel) {
        this.msArchiefDocumenttitel = sArchiefDocumenttitel;
    }

    public String getArchiefDocumentsoort() {
        return msArchiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(String sArchiefDocumentsoort) {
        this.msArchiefDocumentsoort = sArchiefDocumentsoort;
    }

    public String getArchiefRegeling() {
        return msArchiefRegeling;
    }

    public void setArchiefRegeling(String sArchiefRegeling) {
        this.msArchiefRegeling = sArchiefRegeling;
    }

    public String getArchiefDocumentkenmerk() {
        return msArchiefDocumentkenmerk;
    }

    public void setArchiefDocumentkenmerk(String sArchiefDocumentkenmerk) {
        this.msArchiefDocumentkenmerk = sArchiefDocumentkenmerk;
    }

    public Date getArchiefVerzenddag() {
        return mobjArchiefVerzenddag;
    }

    public void setArchiefVerzenddag(Date objArchiefVerzenddag) {
        this.mobjArchiefVerzenddag = objArchiefVerzenddag;
    }

    public String getArchiefDocumenttype() {
        return msArchiefDocumenttype;
    }

    public void setArchiefDocumenttype(String sArchiefDocumenttype) {
        this.msArchiefDocumenttype = sArchiefDocumenttype;
    }

    public String getArchiefDocumentstatus() {
        return msArchiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(String sArchiefDocumentstatus) {
        this.msArchiefDocumentstatus = sArchiefDocumentstatus;
    }

    public String getArchiefRegelingsjaar() {
        return msArchiefRegelingsjaar;
    }

    public void setArchiefRegelingsjaar(String sArchiefRegelingsjaar) {
        this.msArchiefRegelingsjaar = sArchiefRegelingsjaar;
    }

    public String getArchiefFile() {
        return msArchiefFile;
    }

    public void setArchiefFile(String sArchiefFile) {
        this.msArchiefFile = sArchiefFile;
    }
}
