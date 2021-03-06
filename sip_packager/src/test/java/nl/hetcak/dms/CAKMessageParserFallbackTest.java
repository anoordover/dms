package nl.hetcak.dms;

import com.amplexor.ia.configuration.MessageParserConfiguration;
import com.amplexor.ia.metadata.IADocument;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 6-10-2016.
 */
public class CAKMessageParserFallbackTest {
    MessageParserConfiguration objConfiguration;
    Map<String, String> cAIUMapping;

    @Before
    public void setup() {
        cAIUMapping = new HashMap<>();
        cAIUMapping.put("ArchiefDocumentId", "{ArchiefDocumentId}");
        cAIUMapping.put("ArchiefPersoonsnummer", "{ArchiefPersoonsnummer}");
        cAIUMapping.put("PersoonBurgerservicenummer", "{PersoonBurgerservicenummer}");
        cAIUMapping.put("ArchiefDocumenttitel", "{ArchiefDocumenttitel}");
        cAIUMapping.put("ArchiefDocumentsoort", "{ArchiefDocumentsoort}");
        cAIUMapping.put("ArchiefRegeling", "{ArchiefRegeling}");
        cAIUMapping.put("ArchiefDocumentkenmerk", "{ArchiefDocumentkenmerk}");
        cAIUMapping.put("ArchiefVerzenddag", "{ArchiefVerzenddag}");
        cAIUMapping.put("ArchiefDocumenttype", "{ArchiefDocumenttype}");
        cAIUMapping.put("ArchiefHandelingsnummer", "xxxxx");
        cAIUMapping.put("ArchiefDocumentstatus", "{ArchiefDocumentstatus}");
        cAIUMapping.put("ArchiefRegelingsjaar", "{ArchiefRegelingsjaar}");
        cAIUMapping.put("Attachment", "Attachment_{ArchiefDocumentId}.pdf");

        objConfiguration = mock(MessageParserConfiguration.class);
        when(objConfiguration.getAIUElementName()).thenReturn("ArchiefDocument");
        when(objConfiguration.getAIUMapping()).thenReturn(cAIUMapping);
        when(objConfiguration.getAIUNamespace()).thenReturn("urn:hetcak:dms:uitingarchief:2016:08");
        when(objConfiguration.getDocumentIdElement()).thenReturn("ArchiefDocumentId");
        when(objConfiguration.getParameter("schema_location")).thenReturn("../schemas");
        when(objConfiguration.getParameter("schema_name")).thenReturn("standard.xsd");
        when(objConfiguration.getParameter("fallback_schema_name")).thenReturn("fallback.xsd");
    }

    @Test
    public void parse() throws Exception {
        StringBuilder objBuilder = new StringBuilder();
        objBuilder.append("<urn:ArchiefDocument xmlns:urn=\"urn:hetcak:dms:uitingarchief:2016:08\">\n");
        objBuilder.append("\t<urn:MetaData>\n");
        objBuilder.append("\t\t<urn:ArchiefDocumentId>1002224232</urn:ArchiefDocumentId>\n");
        objBuilder.append("\t\t<urn:ArchiefPersoonsnummer>1000010001</urn:ArchiefPersoonsnummer>\n");
        objBuilder.append("\t\t<urn:PersoonBurgerservicenummer>100001000</urn:PersoonBurgerservicenummer>\n");
        objBuilder.append("\t\t<urn:ArchiefDocumenttitel>B01</urn:ArchiefDocumenttitel>\n");
        objBuilder.append("\t\t<urn:ArchiefDocumentsoort>Strorno Al Aanmaning</urn:ArchiefDocumentsoort>\n");
        objBuilder.append("\t\t<urn:ArchiefRegeling>WMO</urn:ArchiefRegeling>\n");
        objBuilder.append("\t\t<urn:ArchiefDocumentkenmerk>12345-WMO</urn:ArchiefDocumentkenmerk>\n");
        objBuilder.append("\t\t<urn:ArchiefVerzenddag>2016-09-16</urn:ArchiefVerzenddag>\n");
        objBuilder.append("\t\t<urn:ArchiefDocumenttype>Uitgaand</urn:ArchiefDocumenttype>\n");
        objBuilder.append("\t\t<urn:ArchiefDocumentstatus>Definitief</urn:ArchiefDocumentstatus>\n");
        objBuilder.append("\t\t<urn:ArchiefRegelingsjaar>2016</urn:ArchiefRegelingsjaar>\n");
        objBuilder.append("\t</urn:MetaData>\n");
        objBuilder.append("\t<urn:PayloadPdf>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PC9GMSAyIDAgUi9GMiAzIDAgUj4+Pj4vTWVkaWFCb3hbMCAwIDYxMiA3OTJdL0Fubm90c1s0IDAgUl0+PgplbmRvYmoKOCAwIG9iajw8L1BhcmVudCA3IDAgUi9EZXN0WzEgMCBSL1hZWiAwIDc0NCAwXS9UaXRsZShUZW1wbGF0ZXMgaW50ZWdyYXRpZSBwbGF0Zm9ybSk+PgplbmRvYmoKNyAwIG9iajw8L1R5cGUvT3V0bGluZXMvQ291bnQgMS9MYXN0IDggMCBSL0ZpcnN0IDggMCBSPj4KZW5kb2JqCjIgMCBvYmo8PC9CYXNlRm9udC9IZWx2ZXRpY2EtQm9sZC9UeXBlL0ZvbnQvRW5jb2RpbmcvV2luQW5zaUVuY29kaW5nL1N1YnR5cGUvVHlwZTE+PgplbmRvYmoKMyAwIG9iajw8L0Jhc2VGb250L0hlbHZldGljYS9UeXBlL0ZvbnQvRW5jb2RpbmcvV2luQW5zaUVuY29kaW5nL1N1YnR5cGUvVHlwZTE+PgplbmRvYmoKNiAwIG9iajw8L1R5cGUvUGFnZXMvQ291bnQgMS9LaWRzWzEgMCBSXT4+CmVuZG9iago5IDAgb2JqPDwvVHlwZS9DYXRhbG9nL091dGxpbmVzIDcgMCBSL1BhZ2VNb2RlL1VzZU91dGxpbmVzL1BhZ2VzIDYgMCBSPj4KZW5kb2JqCjEwIDAgb2JqPDwvUHJvZHVjZXIoaVRleHQgMi4wLjggXChieSBsb3dhZ2llLmNvbVwpKS9Nb2REYXRlKEQ6MjAxNjA2MTUxMzM4MDcrMDInMDAnKS9DcmVhdGlvbkRhdGUoRDoyMDE2MDYxNTEzMzgwNyswMicwMCcpPj4KZW5kb2JqCnhyZWYKMCAxMQowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDE2OTAgMDAwMDAgbiAKMDAwMDAwMjAyNiAwMDAwMCBuIAowMDAwMDAyMTE4IDAwMDAwIG4gCjAwMDAwMDAwMTUgMDAwMDAgbiAKMDAwMDAwMDIyMSAwMDAwMCBuIAowMDAwMDAyMjA1IDAwMDAwIG4gCjAwMDAwMDE5NjIgMDAwMDAgbiAKMDAwMDAwMTg2OSAwMDAwMCBuIAowMDAwMDAyMjU1IDAwMDAwIG4gCjAwMDAwMDIzMzUgMDAwMDAgbiAKdHJhaWxlcgo8PC9Sb290IDkgMCBSL0lEIFs8NjY3ZWVjMmU4MWY1N2FmNmZhZGUyNWZmN2Y4ZmQ0ZWM+PDU1NmY0NmJkZjFmYjk3MTQ4MDkwOGJiZTM2MWRmZmI2Pl0vSW5mbyAxMCAwIFIvU2l6ZSAxMT4+CnN0YXJ0eHJlZgoyNDY3CiUlRU9GCg==</urn:PayloadPdf>\n");
        objBuilder.append("</urn:ArchiefDocument>");

        CAKMessageParserFallback cmpf = new CAKMessageParserFallback(objConfiguration);
        IADocument iad = cmpf.parse(objBuilder.toString()).get(0);
        assertEquals(iad.getDocumentId(), "1002224232");
    }

}