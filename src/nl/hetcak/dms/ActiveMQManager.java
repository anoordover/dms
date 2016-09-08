package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.io.File;
import java.util.Properties;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ActiveMQManager implements DocumentSource {
    ActiveMQConnectionFactory connectionFactory;
    Connection connection;

    public ActiveMQManager(PluggableObjectConfiguration configuration) {
        connectionFactory = new ActiveMQSslConnectionFactory();
        Properties sslConfig = new Properties();
        sslConfig.setProperty("trustStore", configuration.getParameter("truststore"));
        sslConfig.setProperty("trustStorePassword", configuration.getParameter("truststore_password"));
        sslConfig.setProperty("brokerURL", "ssl://muleaq.ont.esb.func.cak-bz.local:10844");
        connectionFactory.setProperties(sslConfig);
    }

    @Override
    public CAKDocument retrieveDocument() {
        CAKDocument rval = null;
        try {
            if (connection == null) {
                connection = connectionFactory.createConnection();
            }
            //TODO: Get Message from ActiveMQ
            String messageBody = "<urn:Document xmlns:urn=\"urn:hetcak:dms:uitingarchief:2016:08\">\n" +
                    "    <urn:MetaData>\n" +
                    "        <urn:DocumentId>0000100001</urn:DocumentId>\n" +
                    "        <urn:Volgnummer>0</urn:Volgnummer>\n" +
                    "        <urn:PersoonId>89001034001</urn:PersoonId>\n" +
                    "        <urn:Logistiek_AanleverReferentie>B01_99_06_25_1036_16</urn:Logistiek_AanleverReferentie>\n" +
                    "        <urn:Logistiek_Receptuurkenmerk>B01</urn:Logistiek_Receptuurkenmerk>\n" +
                    "        <urn:Logistiek_Briefnaam>Beschikking</urn:Logistiek_Briefnaam>\n" +
                    "        <urn:Logistiek_Regeling>WMO</urn:Logistiek_Regeling>\n" +
                    "        <urn:Logistiek_Briefkenmerk>12345-WMO</urn:Logistiek_Briefkenmerk>\n" +
                    "        <urn:Logistiek_Aanleverdatum>2016-08-10T13:49:45</urn:Logistiek_Aanleverdatum>\n" +
                    "        <urn:Logistiek_Verzenddag>2016-08-15T10:00:00</urn:Logistiek_Verzenddag>\n" +
                    "        <urn:Logistiek_Service>24-UUR</urn:Logistiek_Service>\n" +
                    "    </urn:MetaData>\n" +
                    "    <urn:PayloadPdf>JVBERi0xLjQKJeLjz9MKNCAwIG9iajw8L0NbMCAwIDFdL0JvcmRlclswIDAgMF0vQlM8PC9XIDAv\n" +
                    "        Uy9TPj4vQTw8L1VSSShodHRwczovL3dpa2kuY2FrLWJ6LmxvY2FsL2Rpc3BsYXkvSkFWQS9PcGRy\n" +
                    "        YWNodGJlc2NocmlqdmluZytJbnRlZ3JhdGllK2RpZW5zdCt0ZW1wbGF0ZSkvUy9VUkk+Pi9TdWJ0\n" +
                    "        eXBlL0xpbmsvUmVjdFs4NS41IDUwNS45MSAyNTIuNTMgNTE1LjE3XT4+CmVuZG9iago1IDAgb2Jq\n" +
                    "        IDw8L0xlbmd0aCAxNDAxL0ZpbHRlci9GbGF0ZURlY29kZT4+c3RyZWFtCnicjVjJjttGEL3zKxo+\n" +
                    "        JUHQ4SJuvjn2BHGAwHAgwIcgB0psSZS4yBQ1AuZr8ql51Zu6GWlGmMN0satevXrV28z34NdlELK8\n" +
                    "        jNmyDp6WwdfgexDyMMlTdgli9gcm90EUsj+Dv/8JWR0sCpYvFqwL0mwhR60cLQoe0hjTzlDN74Jv\n" +
                    "        QR9EjH7GbbDIeZndAaEwOe0MDcgGvMpFFBVs/hugacpTloc5j0vARjBzY7bazIqSq1Tk61vWldJ4\n" +
                    "        0V0QwyyvHsq04TPsWWaC8+K7IEkzHl09lGnjZ+iz3ATnxXfBAmZy9VCmjZ+hz3ITnBePhsB05FCm\n" +
                    "        jZ+hz3KrFhVZluVs/lu1KFLeWSZ7lPEwN3Z7tTNeRqov0l2bAP9pFqP6nMQuhrSJYOhgWtsFoY9F\n" +
                    "        JEumRNpsHfM+xKwUmEVxZaFMGzED1Bxc4rTGMh47YlhbV2/9XTX8ILVQ08QFkbbNrP19Js5HqYYj\n" +
                    "        YRHdkPQmyLycO711RPVBNROXvtooC0dWa2sNrL+riR9EINiPiQsibZtZ+/tMnI9SE0fIIroh7E2Q\n" +
                    "        eTl3WuwI64NqJi59tdszRxNraw2sv6uJH0QgOFQSF0TaNrP295k4H6UmjpBFdEPYmyDzcu602BHW\n" +
                    "        B9VMXPrqyPK3n7a1Btbf1cQPUiBR4oO4u1b7z48B+1Fq4ghZRDeEvQkyL+dOix1hfVAJEjL6wSGb\n" +
                    "        lzzBgR3ihE4BZsyC4x4tCDADLmTIc6KojXWQZ3TTmjkszMIE6bHEk35kRiGd+yoG47I0eMqAn8ql\n" +
                    "        5xQJFeTyW8srQ31KS4h2pZxSnARJy4InhrA2DGEzJ1nJEDMiLEs3DOlO1iVGPLF0lWHp6jlNUQa5\n" +
                    "        3Hy6IY8dunQlGsJFwXNLWBmWsJ5TNFWQpizxDOW0jHjETJERz0srgDQMZTOnOckgl59HOY946FDO\n" +
                    "        4WEoZyVPLWVlWMp6TvFSQXos8SxlmKWhnMc8tZSVYSnrOcvJHYeWssLEFrR8MW7NGOsHL9bWuM1M\n" +
                    "        6UkoeNv+8huenaiALfGwk7skYguy8zjheOQuu+CHpeiObTWJE2v6SWzHamoEoy+bYex+XO7pXYxI\n" +
                    "        VB6iYdhlGjen1e3gZpE8M8qUnkcE/NRsRV9V43sNcicuQiBdh9fAD2M//Mz+atYHJnr2cRhfB4gB\n" +
                    "        kHoA/74ekCAg96h+2NSibfrtG1QXCCy9wM8fl+yTeBbtcOxEP13lUgeSaYHXgBT3XlrSSpEIfSua\n" +
                    "        Grnd1PH/Uqtne4IwlfiTeEGXqm3TV2wlnquJnfbVqh16KPY8DCMTGPSNOF8EqxvRnybYYlqz4ch2\n" +
                    "        YnJ6bVrNHyEQx7xQBL70tRihGmCbntVEZ9Irib00+/7963CFhAtxw5kO7KpeNYE9VwTINsOZSIO9\n" +
                    "        BGRbcTqKdbNp1gKZVVbkZEM/ndpzo31R6RuVyNRpiSvudmoyRi/9qtmTpish2prVpG7Tb8bqNI3n\n" +
                    "        9XQ+j+wyQAoIL0ZMvlTV4TEOBf6+UBy+ifYgkL1taurJSVUsRhR9oZJYpTR5B5JVv33H2e9o4jSI\n" +
                    "        50FsKTOmqePbcYAiO3wH1x0WlpxxcDGc2K6BgFQGFgig26nZ11DyEcp0xCnKKLbqp8sgS3+o3Czn\n" +
                    "        iZa8PUm6pLEmcZ6GDhRP6x07gLJWtNpshepOzboBfitxrCoYev6C7kAWl0vb7A+y8p0Qm4kNnVzv\n" +
                    "        x3FYtUJ0D5aZ5mZhnpptX7VYED17Ix+mu2H7Vn7sP3SoHU4nqMaeRlUWAC/YQrZuOjeR6BGqi8Is\n" +
                    "        5Gv2Wi9cuSTEajw3B6ylBwj5518Uz86tNM5xyXq3xt0b4h5fPLP0zfPlWI/VejetBLo+Yo/RDvx8\n" +
                    "        vYTUbrbHis5E/6P5GvwHp1QdQgplbmRzdHJlYW0KZW5kb2JqCjEgMCBvYmo8PC9QYXJlbnQgNiAw\n" +
                    "        IFIvQ29udGVudHMgNSAwIFIvVHlwZS9QYWdlL1Jlc291cmNlczw8L1Byb2NTZXQgWy9QREYgL1Rl\n" +
                    "        eHQgL0ltYWdlQiAvSW1hZ2VDIC9JbWFnZUldL0ZvbnQ8PC9GMSAyIDAgUi9GMiAzIDAgUj4+Pj4v\n" +
                    "        TWVkaWFCb3hbMCAwIDYxMiA3OTJdL0Fubm90c1s0IDAgUl0+PgplbmRvYmoKOCAwIG9iajw8L1Bh\n" +
                    "        cmVudCA3IDAgUi9EZXN0WzEgMCBSL1hZWiAwIDc0NCAwXS9UaXRsZShUZW1wbGF0ZXMgaW50ZWdy\n" +
                    "        YXRpZSBwbGF0Zm9ybSk+PgplbmRvYmoKNyAwIG9iajw8L1R5cGUvT3V0bGluZXMvQ291bnQgMS9M\n" +
                    "        YXN0IDggMCBSL0ZpcnN0IDggMCBSPj4KZW5kb2JqCjIgMCBvYmo8PC9CYXNlRm9udC9IZWx2ZXRp\n" +
                    "        Y2EtQm9sZC9UeXBlL0ZvbnQvRW5jb2RpbmcvV2luQW5zaUVuY29kaW5nL1N1YnR5cGUvVHlwZTE+\n" +
                    "        PgplbmRvYmoKMyAwIG9iajw8L0Jhc2VGb250L0hlbHZldGljYS9UeXBlL0ZvbnQvRW5jb2Rpbmcv\n" +
                    "        V2luQW5zaUVuY29kaW5nL1N1YnR5cGUvVHlwZTE+PgplbmRvYmoKNiAwIG9iajw8L1R5cGUvUGFn\n" +
                    "        ZXMvQ291bnQgMS9LaWRzWzEgMCBSXT4+CmVuZG9iago5IDAgb2JqPDwvVHlwZS9DYXRhbG9nL091\n" +
                    "        dGxpbmVzIDcgMCBSL1BhZ2VNb2RlL1VzZU91dGxpbmVzL1BhZ2VzIDYgMCBSPj4KZW5kb2JqCjEw\n" +
                    "        IDAgb2JqPDwvUHJvZHVjZXIoaVRleHQgMi4wLjggXChieSBsb3dhZ2llLmNvbVwpKS9Nb2REYXRl\n" +
                    "        KEQ6MjAxNjA2MTUxMzM4MDcrMDInMDAnKS9DcmVhdGlvbkRhdGUoRDoyMDE2MDYxNTEzMzgwNysw\n" +
                    "        MicwMCcpPj4KZW5kb2JqCnhyZWYKMCAxMQowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDE2OTAg\n" +
                    "        MDAwMDAgbiAKMDAwMDAwMjAyNiAwMDAwMCBuIAowMDAwMDAyMTE4IDAwMDAwIG4gCjAwMDAwMDAw\n" +
                    "        MTUgMDAwMDAgbiAKMDAwMDAwMDIyMSAwMDAwMCBuIAowMDAwMDAyMjA1IDAwMDAwIG4gCjAwMDAw\n" +
                    "        MDE5NjIgMDAwMDAgbiAKMDAwMDAwMTg2OSAwMDAwMCBuIAowMDAwMDAyMjU1IDAwMDAwIG4gCjAw\n" +
                    "        MDAwMDIzMzUgMDAwMDAgbiAKdHJhaWxlcgo8PC9Sb290IDkgMCBSL0lEIFs8NjY3ZWVjMmU4MWY1\n" +
                    "        N2FmNmZhZGUyNWZmN2Y4ZmQ0ZWM+PDU1NmY0NmJkZjFmYjk3MTQ4MDkwOGJiZTM2MWRmZmI2Pl0v\n" +
                    "        SW5mbyAxMCAwIFIvU2l6ZSAxMT4+CnN0YXJ0eHJlZgoyNDY3CiUlRU9GCg==</urn:PayloadPdf>\n" +
                    "</urn:Document>\n";

            XStream xstream = new XStream(new StaxDriver());
            xstream.alias("Document", CAKDocument.class);
            xstream.processAnnotations(CAKDocument.class);
            rval = (CAKDocument)xstream.fromXML(messageBody.trim());
        } catch (JMSException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return rval;
    }

    @Override
    public void postResult() {

    }
}
