package com.amplexor.ia;

import com.amplexor.ia.enums.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.time.LocalDate;

/**
 * Created by admjzimmermann on 4-10-2016.
 */
public class XmlDocument {
    private class MetaData {
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
        private LocalDate mobjVerzenddag;
        @XStreamAlias("ArchiefDocumenttype")
        private ArchiefDocumenttype mobjArchiefDocumenttype;
        @XStreamAlias("ArchiefDocumentstatus")
        private ArchiefDocumentstatus mobjArchiefDocumentstatus;
        @XStreamAlias("ArchiefRegelingsjaar")
        private int miRegelingjaar;
    }

    @XStreamAlias("MetaData")
    private MetaData mobjMetaData;
    @XStreamAlias("PayloadPdf")
    private String msPayloadPdf;

    public XmlDocument() {
        mobjMetaData = new MetaData();
    }

    public void setArchiefDocumentId(long lArchiefDocumentId) {
        mobjMetaData.mlArchiefDocumentId = lArchiefDocumentId;
    }

    public long getArchiefDocumentId() {
        return mobjMetaData.mlArchiefDocumentId;
    }

    public long getArchiefPersoonsnummer() {
        return mobjMetaData.mlArchiefPersoonsnummer;
    }

    public void setArchiefPersoonsnummer(long lArchiefPersoonsnummer) {
        mobjMetaData.mlArchiefPersoonsnummer = lArchiefPersoonsnummer;
    }

    public long getPersoonBurgerservicenummer() {
        return mobjMetaData.mlPersoonBurgerservicenummer;
    }

    public void setPersoonBurgerservicenummer(long lPersoonBurgerservicenummer) {
        mobjMetaData.mlPersoonBurgerservicenummer = lPersoonBurgerservicenummer;
    }

    public ArchiefDocumenttitel getArchiefDocumenttitel() {
        return mobjMetaData.mobjArchiefDocumenttitel;
    }

    public void setArchiefDocumenttitel(ArchiefDocumenttitel objArchiefDocumenttitel) {
        this.mobjMetaData.mobjArchiefDocumenttitel = objArchiefDocumenttitel;
    }

    public ArchiefDocumentsoort getArchiefDocumentsoort() {
        return mobjMetaData.mobjArchiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(ArchiefDocumentsoort objArchiefDocumentsoort) {
        this.mobjMetaData.mobjArchiefDocumentsoort = objArchiefDocumentsoort;
    }

    public ArchiefRegeling getArchiefRegeling() {
        return mobjMetaData.mobjArchiefRegeling;
    }

    public void setArchiefRegeling(ArchiefRegeling objArchiefRegeling) {
        this.mobjMetaData.mobjArchiefRegeling = objArchiefRegeling;
    }

    public long getArchiefDocumentkenmerk() {
        return mobjMetaData.mlArchiefDocumentkenmerk;
    }

    public void setArchiefDocumentkenmerk(long lArchiefDocumentkenmerk) {
        this.mobjMetaData.mlArchiefDocumentkenmerk = lArchiefDocumentkenmerk;
    }

    public LocalDate getVerzenddag() {
        return mobjMetaData.mobjVerzenddag;
    }

    public void setVerzenddag(LocalDate objVerzenddag) {
        this.mobjMetaData.mobjVerzenddag = objVerzenddag;
    }

    public ArchiefDocumenttype getArchiefDocumenttype() {
        return mobjMetaData.mobjArchiefDocumenttype;
    }

    public void setArchiefDocumenttype(ArchiefDocumenttype objArchiefDocumenttype) {
        this.mobjMetaData.mobjArchiefDocumenttype = objArchiefDocumenttype;
    }

    public ArchiefDocumentstatus getArchiefDocumentstatus() {
        return mobjMetaData.mobjArchiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(ArchiefDocumentstatus objArchiefDocumentstatus) {
        this.mobjMetaData.mobjArchiefDocumentstatus = objArchiefDocumentstatus;
    }

    public int getRegelingjaar() {
        return mobjMetaData.miRegelingjaar;
    }

    public void setRegelingjaar(int iRegelingjaar) {
        this.mobjMetaData.miRegelingjaar = iRegelingjaar;
    }

    public String getPayloadPdf() {
        return msPayloadPdf;
    }

    public void setPayloadPdf(String sPayloadPdf) {
        this.msPayloadPdf = sPayloadPdf;
    }

    public String getXml() {
        QNameMap objQNameMap = new QNameMap();
        objQNameMap.setDefaultNamespace("urn:hetcak:dms:uitingarchief:2016:08");
        XStream objXStream = new XStream(new StaxDriver(objQNameMap));
        objXStream.alias("ArchiefDocument", this.getClass());
        objXStream.processAnnotations(this.getClass());
        return objXStream.toXML(this);
    }
}
