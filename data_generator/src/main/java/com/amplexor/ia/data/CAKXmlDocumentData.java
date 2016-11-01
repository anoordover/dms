package com.amplexor.ia.data;

import com.amplexor.ia.configuration.converters.LocalDateConverter;
import com.amplexor.ia.enums.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.time.LocalDate;

/**
 * Created by admjzimmermann on 5-10-2016.
 */
public class CAKXmlDocumentData {
    @XStreamAlias("ArchiefDocumentId")
    private String mlArchiefDocumentId;

    @XStreamAlias("ArchiefPersoonsnummer")
    private String mlArchiefPersoonsnummer;

    @XStreamAlias("PersoonBurgerservicenummer")
    private String mlPersoonBurgerservicenummer;

    @XStreamAlias("ArchiefDocumenttitel")
    private String mobjArchiefDocumenttitel;

    @XStreamAlias("ArchiefDocumentsoort")
    private String mobjArchiefDocumentsoort;

    @XStreamAlias("ArchiefRegeling")
    private String mobjArchiefRegeling;

    @XStreamAlias("ArchiefDocumentkenmerk")
    private String mlArchiefDocumentkenmerk;

    @XStreamAlias("ArchiefVerzenddag")
    @XStreamConverter(LocalDateConverter.class)
    private LocalDate mobjVerzenddag;

    @XStreamAlias("ArchiefDocumenttype")
    private String mobjArchiefDocumenttype;

    @XStreamAlias("ArchiefDocumentstatus")
    private String mobjArchiefDocumentstatus;

    @XStreamAlias("ArchiefRegelingsjaar")
    private String miRegelingjaar;

    public String getArchiefDocumentId() {
        return mlArchiefDocumentId;
    }

    public void setArchiefDocumentId(String mlArchiefDocumentId) {
        this.mlArchiefDocumentId = mlArchiefDocumentId;
    }

    public String getArchiefPersoonsnummer() {
        return mlArchiefPersoonsnummer;
    }

    public void setArchiefPersoonsnummer(String mlArchiefPersoonsnummer) {
        this.mlArchiefPersoonsnummer = mlArchiefPersoonsnummer;
    }

    public String getPersoonBurgerservicenummer() {
        return mlPersoonBurgerservicenummer;
    }

    public void setPersoonBurgerservicenummer(String mlPersoonBurgerservicenummer) {
        this.mlPersoonBurgerservicenummer = mlPersoonBurgerservicenummer;
    }

    public String getArchiefDocumenttitel() {
        return mobjArchiefDocumenttitel;
    }

    public void setArchiefDocumenttitel(String mobjArchiefDocumenttitel) {
        this.mobjArchiefDocumenttitel = mobjArchiefDocumenttitel;
    }

    public String getArchiefDocumentsoort() {
        return mobjArchiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(String mobjArchiefDocumentsoort) {
        this.mobjArchiefDocumentsoort = mobjArchiefDocumentsoort;
    }

    public String getArchiefRegeling() {
        return mobjArchiefRegeling;
    }

    public void setArchiefRegeling(String mobjArchiefRegeling) {
        this.mobjArchiefRegeling = mobjArchiefRegeling;
    }

    public String getArchiefDocumentkenmerk() {
        return mlArchiefDocumentkenmerk;
    }

    public void setArchiefDocumentkenmerk(String mlArchiefDocumentkenmerk) {
        this.mlArchiefDocumentkenmerk = mlArchiefDocumentkenmerk;
    }

    public LocalDate getVerzenddag() {
        return mobjVerzenddag;
    }

    public void setVerzenddag(LocalDate mobjVerzenddag) {
        this.mobjVerzenddag = mobjVerzenddag;
    }

    public String getArchiefDocumenttype() {
        return mobjArchiefDocumenttype;
    }

    public void setArchiefDocumenttype(String mobjArchiefDocumenttype) {
        this.mobjArchiefDocumenttype = mobjArchiefDocumenttype;
    }

    public String getArchiefDocumentstatus() {
        return mobjArchiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(String mobjArchiefDocumentstatus) {
        this.mobjArchiefDocumentstatus = mobjArchiefDocumentstatus;
    }

    public String getRegelingjaar() {
        return miRegelingjaar;
    }

    public void setRegelingjaar(String miRegelingjaar) {
        this.miRegelingjaar = miRegelingjaar;
    }
}
