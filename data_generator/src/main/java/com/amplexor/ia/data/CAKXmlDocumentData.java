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
    private long mlArchiefDocumentId;

    @XStreamAlias("ArchiefPersoonsnummer")
    private long mlArchiefPersoonsnummer;

    @XStreamAlias("PersoonBurgerservicenummer")
    private long mlPersoonBurgerservicenummer;

    @XStreamAlias("ArchiefDocumenttitel")
    private ArchiefDocumenttitel mobjArchiefDocumenttitel;

    @XStreamAlias("ArchiefDocumentsoort")
    private ArchiefDocumentsoort mobjArchiefDocumentsoort;

    @XStreamAlias("ArchiefRegeling")
    private ArchiefRegeling mobjArchiefRegeling;

    @XStreamAlias("ArchiefDocumentkenmerk")
    private long mlArchiefDocumentkenmerk;

    @XStreamAlias("ArchiefVerzenddag")
    @XStreamConverter(LocalDateConverter.class)
    private LocalDate mobjVerzenddag;

    @XStreamAlias("ArchiefDocumenttype")
    private ArchiefDocumenttype mobjArchiefDocumenttype;

    @XStreamAlias("ArchiefDocumentstatus")
    private ArchiefDocumentstatus mobjArchiefDocumentstatus;

    @XStreamAlias("ArchiefRegelingsjaar")
    private int miRegelingjaar;

    public long getArchiefDocumentId() {
        return mlArchiefDocumentId;
    }

    public void setArchiefDocumentId(long mlArchiefDocumentId) {
        this.mlArchiefDocumentId = mlArchiefDocumentId;
    }

    public long getArchiefPersoonsnummer() {
        return mlArchiefPersoonsnummer;
    }

    public void setArchiefPersoonsnummer(long mlArchiefPersoonsnummer) {
        this.mlArchiefPersoonsnummer = mlArchiefPersoonsnummer;
    }

    public long getPersoonBurgerservicenummer() {
        return mlPersoonBurgerservicenummer;
    }

    public void setPersoonBurgerservicenummer(long mlPersoonBurgerservicenummer) {
        this.mlPersoonBurgerservicenummer = mlPersoonBurgerservicenummer;
    }

    public ArchiefDocumenttitel getArchiefDocumenttitel() {
        return mobjArchiefDocumenttitel;
    }

    public void setArchiefDocumenttitel(ArchiefDocumenttitel mobjArchiefDocumenttitel) {
        this.mobjArchiefDocumenttitel = mobjArchiefDocumenttitel;
    }

    public ArchiefDocumentsoort getArchiefDocumentsoort() {
        return mobjArchiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(ArchiefDocumentsoort mobjArchiefDocumentsoort) {
        this.mobjArchiefDocumentsoort = mobjArchiefDocumentsoort;
    }

    public ArchiefRegeling getArchiefRegeling() {
        return mobjArchiefRegeling;
    }

    public void setArchiefRegeling(ArchiefRegeling mobjArchiefRegeling) {
        this.mobjArchiefRegeling = mobjArchiefRegeling;
    }

    public long getArchiefDocumentkenmerk() {
        return mlArchiefDocumentkenmerk;
    }

    public void setArchiefDocumentkenmerk(long mlArchiefDocumentkenmerk) {
        this.mlArchiefDocumentkenmerk = mlArchiefDocumentkenmerk;
    }

    public LocalDate getVerzenddag() {
        return mobjVerzenddag;
    }

    public void setVerzenddag(LocalDate mobjVerzenddag) {
        this.mobjVerzenddag = mobjVerzenddag;
    }

    public ArchiefDocumenttype getArchiefDocumenttype() {
        return mobjArchiefDocumenttype;
    }

    public void setArchiefDocumenttype(ArchiefDocumenttype mobjArchiefDocumenttype) {
        this.mobjArchiefDocumenttype = mobjArchiefDocumenttype;
    }

    public ArchiefDocumentstatus getArchiefDocumentstatus() {
        return mobjArchiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(ArchiefDocumentstatus mobjArchiefDocumentstatus) {
        this.mobjArchiefDocumentstatus = mobjArchiefDocumentstatus;
    }

    public int getRegelingjaar() {
        return miRegelingjaar;
    }

    public void setRegelingjaar(int miRegelingjaar) {
        this.miRegelingjaar = miRegelingjaar;
    }
}
