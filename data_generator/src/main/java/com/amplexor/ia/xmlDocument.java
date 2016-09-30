package com.amplexor.ia;

import com.amplexor.ia.enums.*;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by minkenbergs on 30-9-2016.
 */
public class xmlDocument {
    private long archiefDocumentId;
    private long archiefPersoonsnummer;
    private long persoonBurgerservericenummer;
    private ArchiefDocumenttitel archiefDocumenttitel;
    private ArchiefDocumentsoort archiefDocumentsoort;
    private ArchiefRegeling archiefRegeling;
    private long archiefDocumentKenmerk;
    private LocalDate archiefVerzenddag;
    private ArchiefDocumenttype archiefDocumenttype;
    private ArchiefDocumentstatus archiefDocumentstatus;
    private int archiefRegelingsjaar;
    private String payloadPdf;

    public xmlDocument(long archiefDocumentId, long archiefPersoonsnummer, long persoonBurgerservericenummer, ArchiefDocumenttitel archiefDocumenttitel, ArchiefDocumentsoort archiefDocumentsoort, ArchiefRegeling archiefRegeling, long archiefDocumentKenmerk, LocalDate archiefVerzenddag, ArchiefDocumenttype archiefDocumenttype, ArchiefDocumentstatus archiefDocumentstatus, int archiefRegelingsjaar, String payloadPdf) {
        this.archiefDocumentId = archiefDocumentId;
        this.archiefPersoonsnummer = archiefPersoonsnummer;
        this.persoonBurgerservericenummer = persoonBurgerservericenummer;
        this.archiefDocumenttitel = archiefDocumenttitel;
        this.archiefDocumentsoort = archiefDocumentsoort;
        this.archiefRegeling = archiefRegeling;
        this.archiefDocumentKenmerk = archiefDocumentKenmerk;
        this.archiefVerzenddag = archiefVerzenddag;
        this.archiefDocumenttype = archiefDocumenttype;
        this.archiefDocumentstatus = archiefDocumentstatus;
        this.archiefRegelingsjaar = archiefRegelingsjaar;
        this.payloadPdf = payloadPdf;
    }

    public String getPayloadPdf() {
        return payloadPdf;
    }

    public void setPayloadPdf(String payloadPdf) {
        this.payloadPdf = payloadPdf;
    }

    public long getArchiefDocumentId() {
        return archiefDocumentId;
    }

    public void setArchiefDocumentId(long archiefDocumentId) {
        this.archiefDocumentId = archiefDocumentId;
    }

    public long getArchiefPersoonsnummer() {
        return archiefPersoonsnummer;
    }

    public void setArchiefPersoonsnummer(long archiefPersoonsnummer) {
        this.archiefPersoonsnummer = archiefPersoonsnummer;
    }

    public long getPersoonBurgerservericenummer() {
        return persoonBurgerservericenummer;
    }

    public void setPersoonBurgerservericenummer(long persoonBurgerservericenummer) {
        this.persoonBurgerservericenummer = persoonBurgerservericenummer;
    }

    public ArchiefDocumenttitel getArchiefDocumenttitel() {
        return archiefDocumenttitel;
    }

    public void setArchiefDocumenttitel(ArchiefDocumenttitel archiefDocumenttitel) {
        this.archiefDocumenttitel = archiefDocumenttitel;
    }

    public ArchiefDocumentsoort getArchiefDocumentsoort() {
        return archiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(ArchiefDocumentsoort archiefDocumentsoort) {
        this.archiefDocumentsoort = archiefDocumentsoort;
    }

    public ArchiefRegeling getArchiefRegeling() {
        return archiefRegeling;
    }

    public void setArchiefRegeling(ArchiefRegeling archiefRegeling) {
        this.archiefRegeling = archiefRegeling;
    }

    public long getArchiefDocumentKenmerk() {
        return archiefDocumentKenmerk;
    }

    public void setArchiefDocumentKenmerk(long archiefDocumentKenmerk) {
        this.archiefDocumentKenmerk = archiefDocumentKenmerk;
    }

    public LocalDate getArchiefVerzenddag() {
        return archiefVerzenddag;
    }

    public void setArchiefVerzenddag(LocalDate archiefVerzenddag) {
        this.archiefVerzenddag = archiefVerzenddag;
    }

    public ArchiefDocumenttype getArchiefDocumenttype() {
        return archiefDocumenttype;
    }

    public void setArchiefDocumenttype(ArchiefDocumenttype archiefDocumenttype) {
        this.archiefDocumenttype = archiefDocumenttype;
    }

    public ArchiefDocumentstatus getArchiefDocumentstatus() {
        return archiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(ArchiefDocumentstatus archiefDocumentstatus) {
        this.archiefDocumentstatus = archiefDocumentstatus;
    }

    public long getArchiefRegelingsjaar() {
        return archiefRegelingsjaar;
    }

    public void setArchiefRegelingsjaar(int archiefRegelingsjaar) {
        this.archiefRegelingsjaar = archiefRegelingsjaar;
    }
}
