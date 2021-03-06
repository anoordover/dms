package nl.hetcak.dms.ia.web.restfull.consumers;

import nl.hetcak.dms.ia.web.exceptions.RequestParsingException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.query.InfoArchiveQueryBuilder;
import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;
import nl.hetcak.dms.ia.web.util.SchemaUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "RaadplegenDocumentLijstRequest", namespace = "urn:hetcak:dms:raadplegenuitingarchief:2016:11")
public class ListDocumentRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListDocumentRequestConsumer.class);
    private final static String ARCHIEF_DOCUMENT_TITLE = "ArchiefDocumenttitel";
    private final static String ARCHIEF_DOCUMENT_TITLES = "ArchiefDocumenttitels";
    private final static String ARCHIEF_DOCUMENT_ID = "ArchiefDocumentId";
    private final static String ARCHIEF_DOCUMENT_KENMERK = "ArchiefDocumentkenmerk";
    private final static String ARCHIEF_DOCUMENT_SOORT = "ArchiefDocumentsoort";
    private final static String ARCHIEF_DOCUMENT_TYPE = "ArchiefDocumenttype";
    private final static String ARCHIEF_DOCUMENT_STATUS = "ArchiefDocumentstatus";

    private final static String ARCHIEF_REGELING = "ArchiefRegeling";
    private final static String ARCHIEF_REGELING_JAAR = "ArchiefRegelingsjaar";

    private final static String ARCHIEF_PERSOONS_NUMMER = "ArchiefPersoonsnummer";
    private final static String ARCHIEF_PERSOONS_NUMMERS = "ArchiefPersoonsnummers";

    private final static String ARCHIEF_VERZENDDAG = "ArchiefVerzenddag";
    private final static String ARCHIEF_VERZENDDATUM_VAN = "VerzenddatumPeriodeVan";
    private final static String ARCHIEF_VERZENDDATUM_TM = "VerzenddatumPeriodeTm";

    private ArchiefDocumenttitels mcArchiefDocumenttitels;
    private String msArchiefDocumentId;
    private String msArchiefDocumentkenmerk;
    private String msArchiefDocumentsoort;
    private String msArchiefDocumenttype;
    private String msArchiefRegeling;
    private String msArchiefRegelingsjaar;
    private ArchiefPersoonsnummers mcArchiefPersoonsnummers;
    private String msArchiefDocumentstatus;
    private String msVerzenddatumPeriodeVan;
    private String msVerzenddatumPeriodeTm;

    @XmlElement(name = ARCHIEF_DOCUMENT_TITLES)
    public ArchiefDocumenttitels getArchiefDocumenttitels() {
        return mcArchiefDocumenttitels;
    }

    public void setArchiefDocumenttitels(ArchiefDocumenttitels cArchiefDocumenttitels) {
        this.mcArchiefDocumenttitels = cArchiefDocumenttitels;
    }

    @XmlElement(name = ARCHIEF_DOCUMENT_ID)
    public String getArchiefDocumentId() {
        return msArchiefDocumentId;
    }

    public void setArchiefDocumentId(String archiefDocumentId) {
        this.msArchiefDocumentId = archiefDocumentId;
    }

    @XmlElement(name = ARCHIEF_DOCUMENT_KENMERK)
    public String getArchiefDocumentkenmerk() {
        return msArchiefDocumentkenmerk;
    }

    public void setArchiefDocumentkenmerk(String archiefDocumentkenmerk) {
        this.msArchiefDocumentkenmerk = archiefDocumentkenmerk;
    }

    @XmlElement(name = ARCHIEF_DOCUMENT_SOORT)
    public String getArchiefDocumentsoort() {
        return msArchiefDocumentsoort;
    }

    public void setArchiefDocumentsoort(String archiefDocumentsoort) {
        this.msArchiefDocumentsoort = archiefDocumentsoort;
    }

    @XmlElement(name = ARCHIEF_DOCUMENT_TYPE)
    public String getArchiefDocumenttype() {
        return msArchiefDocumenttype;
    }

    public void setArchiefDocumenttype(String archiefDocumenttype) {
        this.msArchiefDocumenttype = archiefDocumenttype;
    }

    @XmlElement(name = ARCHIEF_REGELING)
    public String getArchiefRegeling() {
        return msArchiefRegeling;
    }

    public void setArchiefRegeling(String archiefRegeling) {
        this.msArchiefRegeling = archiefRegeling;
    }

    @XmlElement(name = ARCHIEF_REGELING_JAAR)
    public String getArchiefRegelingsjaar() {
        return msArchiefRegelingsjaar;
    }

    public void setArchiefRegelingsjaar(String archiefRegelingsjaar) {
        this.msArchiefRegelingsjaar = archiefRegelingsjaar;
    }

    @XmlElement(name = ARCHIEF_PERSOONS_NUMMERS)
    public ArchiefPersoonsnummers getArchiefPersoonsnummers() {
        return mcArchiefPersoonsnummers;
    }

    public void setArchiefPersoonsnummers(ArchiefPersoonsnummers cArchiefPersoonsnummers) {
        this.mcArchiefPersoonsnummers = cArchiefPersoonsnummers;
    }

    @XmlElement(name = ARCHIEF_DOCUMENT_STATUS)
    public String getArchiefDocumentstatus() {
        return msArchiefDocumentstatus;
    }

    public void setArchiefDocumentstatus(String archiefDocumentstatus) {
        this.msArchiefDocumentstatus = archiefDocumentstatus;
    }

    @XmlElement(name = ARCHIEF_VERZENDDATUM_VAN)
    public String getVerzenddatumPeriodeVan() {
        return msVerzenddatumPeriodeVan;
    }

    @XmlTransient
    public String getVerzenddatumPeriodeVanAsInfoArchiveString() {
        try {
            return InfoArchiveDateUtil.convertToInfoArchiveDate(msVerzenddatumPeriodeVan);
        } catch (ParseException parseExc) {
            LOGGER.error("Can't parse string to InfoArchive format. is string empty?", parseExc);
            return null;
        }
    }

    public void setVerzenddatumPeriodeVan(String verzenddatumPeriodeVan) {
        this.msVerzenddatumPeriodeVan = verzenddatumPeriodeVan;
    }

    @XmlElement(name = ARCHIEF_VERZENDDATUM_TM)
    public String getVerzenddatumPeriodeTm() {
        return msVerzenddatumPeriodeTm;
    }

    public String getVerzenddatumPeriodeTmAsInfoArchiveString() {
        try {
            return InfoArchiveDateUtil.convertToInfoArchiveDate(msVerzenddatumPeriodeTm);
        } catch (ParseException parseExc) {
            LOGGER.error("Can't parse string to InfoArchive format. is string empty?", parseExc);
            return null;
        }
    }

    public void setVerzenddatumPeriodeTm(String verzenddatumPeriodeTm) {
        this.msVerzenddatumPeriodeTm = verzenddatumPeriodeTm;
    }

    public boolean hasContent() {
        if (checkArchiefDocumentContentPartOne() || checkArchiefDocumentContentPartTwo() || checkArchiefRegelingContent() || checkVerzenddatumPeriodeContent()) {
            return true;
        }

        return false;
    }

    private boolean checkVerzenddatumPeriodeContent() {
        if (StringUtils.isNotBlank(msVerzenddatumPeriodeVan) && StringUtils.isNotBlank(msVerzenddatumPeriodeTm))
            return true;

        return false;
    }


    private boolean checkArchiefRegelingContent() {
        if (StringUtils.isNotBlank(msArchiefRegeling))
            return true;
        if (StringUtils.isNotBlank(msArchiefRegelingsjaar))
            return true;

        return false;
    }

    private boolean checkArchiefDocumentContentPartOne() {
        if (StringUtils.isNotBlank(msArchiefDocumentId))
            return true;

        if (StringUtils.isNotBlank(msArchiefDocumentsoort))
            return true;

        if (StringUtils.isNotBlank(msArchiefDocumentkenmerk))
            return true;

        if (StringUtils.isNotBlank(msArchiefDocumentstatus))
            return true;

        return false;
    }

    private boolean checkArchiefDocumentContentPartTwo() {
        if (Objects.nonNull(mcArchiefDocumenttitels))
            return true;

        if (StringUtils.isNotBlank(msArchiefDocumenttype))
            return true;

        if (Objects.nonNull(mcArchiefPersoonsnummers))
            return true;

        return false;
    }


    public InfoArchiveQueryBuilder adaptToQuery() {
        InfoArchiveQueryBuilder queryBuilder = new InfoArchiveQueryBuilder();
        queryBuilder = convertArchiefDocumentContent(queryBuilder);
        queryBuilder = convertOtherContent(queryBuilder);
        return queryBuilder;
    }

    private InfoArchiveQueryBuilder convertOtherContent(InfoArchiveQueryBuilder queryBuilder) {
        if (Objects.nonNull(mcArchiefPersoonsnummers)) {
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_PERSOONS_NUMMER, mcArchiefPersoonsnummers.getArchiefPersoonsnummers());
        }

        if (StringUtils.isNotBlank(msArchiefRegeling)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefRegeling);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_REGELING, values);
        }

        if (StringUtils.isNotBlank(msArchiefRegelingsjaar)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefRegelingsjaar);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_REGELING_JAAR, values);
        }

        if (StringUtils.isNotBlank(msVerzenddatumPeriodeVan) && StringUtils.isNotBlank(msVerzenddatumPeriodeTm))
            queryBuilder = queryBuilder.addBetweenCriteria(ARCHIEF_VERZENDDAG, msVerzenddatumPeriodeVan, msVerzenddatumPeriodeTm);

        return queryBuilder;
    }

    private InfoArchiveQueryBuilder convertArchiefDocumentContent(InfoArchiveQueryBuilder queryBuilder) {
        if (StringUtils.isNotBlank(msArchiefDocumentId)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefDocumentId);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_DOCUMENT_ID, values);
        }

        if (StringUtils.isNotBlank(msArchiefDocumentsoort)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefDocumentsoort);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_DOCUMENT_SOORT, values);
        }

        if (StringUtils.isNotBlank(msArchiefDocumentkenmerk)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefDocumentkenmerk);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_DOCUMENT_KENMERK, values);
        }

        if (StringUtils.isNotBlank(msArchiefDocumentstatus)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefDocumentstatus);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_DOCUMENT_STATUS, values);
        }

        if (Objects.nonNull(mcArchiefDocumenttitels)) {
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_DOCUMENT_TITLE, mcArchiefDocumenttitels.getArchiefDocumenttitels());
        }

        if (StringUtils.isNotBlank(msArchiefDocumenttype)) {
            List<String> values = new ArrayList<>();
            values.add(msArchiefDocumenttype);
            queryBuilder = queryBuilder.addEqualCriteria(ARCHIEF_DOCUMENT_TYPE, values);
        }

        return queryBuilder;
    }

    /**
     * Unmarshal the xml to a {@link ListDocumentRequestConsumer} object.
     *
     * @param input The xml input.
     * @return a converted xml {#link String} to {@link ListDocumentRequestConsumer}.
     * @throws JAXBException Errors during the unmarshalling.
     */
    public static ListDocumentRequestConsumer unmarshalRequest(String input) throws RequestResponseException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ListDocumentRequestConsumer.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(SchemaUtil.getSchema());

            StringReader reader = new StringReader(input);
            return (ListDocumentRequestConsumer) unmarshaller.unmarshal(reader);
        } catch (JAXBException jaxbExc) {
            LOGGER.error(input);
            throw new RequestParsingException(jaxbExc);
        }
    }
}
