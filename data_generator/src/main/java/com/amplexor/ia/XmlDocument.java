package com.amplexor.ia;

import com.amplexor.ia.configuration.converters.LocalDateConverter;
import com.amplexor.ia.data.CAKXmlDocumentData;
import com.amplexor.ia.enums.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.time.LocalDate;

/**
 * Created by admjzimmermann on 4-10-2016.
 */
public class XmlDocument {
    @XStreamAlias("MetaData")
    private CAKXmlDocumentData mobjMetaData;

    @XStreamAlias("PayloadPdf")
    private String msPayloadPdf;

    public XmlDocument() {
        mobjMetaData = new CAKXmlDocumentData();
    }

    public void setArchiefDocumentId(long lArchiefDocumentId) {
        mobjMetaData.setArchiefDocumentId(lArchiefDocumentId);
    }

    public long getArchiefDocumentId() {
        return mobjMetaData.getArchiefDocumentId();
    }

    public long getArchiefPersoonsnummer() {
        return mobjMetaData.getArchiefPersoonsnummer();
    }

    public void setArchiefPersoonsnummer(long lArchiefPersoonsnummer) {
        mobjMetaData.setArchiefPersoonsnummer(lArchiefPersoonsnummer);
    }

    public long getPersoonBurgerservicenummer() {
        return mobjMetaData.getPersoonBurgerservicenummer();
    }

    public void setPersoonBurgerservicenummer(long lPersoonBurgerservicenummer) {
        mobjMetaData.setPersoonBurgerservicenummer(lPersoonBurgerservicenummer);
    }

    public ArchiefDocumenttitel getArchiefDocumenttitel() {
        return mobjMetaData.getArchiefDocumenttitel();
    }

    public void setArchiefDocumenttitel(ArchiefDocumenttitel objArchiefDocumenttitel) {
        mobjMetaData.setArchiefDocumenttitel(objArchiefDocumenttitel);
    }

    public ArchiefDocumentsoort getArchiefDocumentsoort() {
        return mobjMetaData.getArchiefDocumentsoort();
    }

    public void setArchiefDocumentsoort(ArchiefDocumentsoort objArchiefDocumentsoort) {
        mobjMetaData.setArchiefDocumentsoort(objArchiefDocumentsoort);
    }

    public ArchiefRegeling getArchiefRegeling() {
        return mobjMetaData.getArchiefRegeling();
    }

    public void setArchiefRegeling(ArchiefRegeling objArchiefRegeling) {
        mobjMetaData.setArchiefRegeling(objArchiefRegeling);
    }

    public long getArchiefDocumentkenmerk() {
        return mobjMetaData.getArchiefDocumentkenmerk();
    }

    public void setArchiefDocumentkenmerk(long lArchiefDocumentkenmerk) {
        mobjMetaData.setArchiefDocumentkenmerk(lArchiefDocumentkenmerk);
    }

    public LocalDate getVerzenddag() {
        return mobjMetaData.getVerzenddag();
    }

    public void setVerzenddag(LocalDate objVerzenddag) {
        mobjMetaData.setVerzenddag(objVerzenddag);
    }

    public ArchiefDocumenttype getArchiefDocumenttype() {
        return mobjMetaData.getArchiefDocumenttype();
    }

    public void setArchiefDocumenttype(ArchiefDocumenttype objArchiefDocumenttype) {
        mobjMetaData.setArchiefDocumenttype(objArchiefDocumenttype);
    }

    public ArchiefDocumentstatus getArchiefDocumentstatus() {
        return mobjMetaData.getArchiefDocumentstatus();
    }

    public void setArchiefDocumentstatus(ArchiefDocumentstatus objArchiefDocumentstatus) {
        mobjMetaData.setArchiefDocumentstatus(objArchiefDocumentstatus);
    }

    public int getRegelingjaar() {
        return mobjMetaData.getRegelingjaar();
    }

    public void setRegelingjaar(int iRegelingjaar) {
        mobjMetaData.setRegelingjaar(iRegelingjaar);
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