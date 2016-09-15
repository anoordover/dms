import com.amplexor.ia.configuration.ConfigManager;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import javafx.util.converter.DateTimeStringConverter;
import nl.hetcak.dms.CAKDocument;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.jms.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Random;

/**
 * Created by admjzimmermann on 15-9-2016.
 */
public class DocumentProducer {
    private static final String SSL_CONFIG_TRUSTSTORE = "trustStore";
    private static final String SSL_CONFIG_TRUSTSTOREPASS = "trustStorePassword";
    private static final String SSL_CONFIG_BROKER_URL = "brokerURL";

    private static Logger logger;
    private static String configLocation = System.getProperty("user.dir") + "/IAArchiver.xml";
    private static ActiveMQSslConnectionFactory objConnectionFactory;
    private static Connection objConnection;

    private static Random objRandom = new Random(System.nanoTime());
    private static int iNextId = 10000000;

    public static void main(String[] cArgs) {
        parseArguments(cArgs);
        try {
            setupActiveMQConnection();
            if (objConnection != null) {
                objConnection.start();
                Session objSession = objConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination objDestination = objSession.createQueue("AanleverenArchiefUitingen");
                MessageProducer objProducer = objSession.createProducer(objDestination);
                TextMessage objMessage = objSession.createTextMessage();
                objMessage.setText(generateDocument());
                objProducer.send(objDestination, objMessage);
            }
        } catch (JMSException | IOException ex) {
            logger.error(ex);
        }

    }

    public static String generateDocument() throws IOException {
        CAKDocument objDocument = new CAKDocument();
        objDocument.setDocumentId(Integer.toString(iNextId++));
        objDocument.setMetadata("ArchiefDocumentId", Integer.toString(iNextId));
        objDocument.setMetadata("ArchiefPersoonnummer", Integer.toString(100000000 + objRandom.nextInt(17000000)));
        objDocument.setMetadata("PersoonBurgerservicenummer", Integer.toString(10000000 + objRandom.nextInt(17000000)));
        objDocument.setMetadata("ArchiefDocumenttitel", "B01");
        objDocument.setMetadata("ArchiefRegeling", "WMO");
        objDocument.setMetadata("ArchiefVerzenddag", "2016-09-15");
        objDocument.setMetadata("ArchiefDocumentsoort", "Factuur");
        objDocument.setMetadata("ArchiefDocumentkenmerk", "12345-WMO");
        objDocument.setMetadata("ArchiefDocumenttype", "uitgaand");
        objDocument.setMetadata("ArchiefDocumentstatus", "definitief");
        objDocument.setMetadata("ArchiefRegelinsjaar", "2016");

        Path objPayloadPath = Paths.get(System.getProperty("user.dir") + "/payload.pdf");
        objDocument.setPayload(Files.readAllBytes(objPayloadPath));

        QNameMap objMap = new QNameMap();
        objMap.setDefaultNamespace("urn:hetcak:dms:uitingarchief:2016:08");
        objMap.setDefaultPrefix("urn");

        XStream objXStream = new XStream(new StaxDriver(objMap));
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        return objXStream.toXML(objDocument);
    }

    public static void setupActiveMQConnection() throws JMSException {
        ConfigManager objConfigManager = new ConfigManager(configLocation);
        objConfigManager.loadConfiguration();
        PropertyConfigurator.configure(objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        logger = Logger.getLogger("DocumentProducer");
        logger.info("Logging configured using " + objConfigManager.getConfiguration().getArchiverConfiguration().getLog4JPropertiesPath());
        objConnectionFactory = new ActiveMQSslConnectionFactory();
        Properties sslConfig = new Properties();
        sslConfig.setProperty(SSL_CONFIG_TRUSTSTORE, objConfigManager.getConfiguration().getDocumentSource().getParameter("truststore"));
        sslConfig.setProperty(SSL_CONFIG_TRUSTSTOREPASS, objConfigManager.getConfiguration().getDocumentSource().getParameter("truststore_password"));
        sslConfig.setProperty(SSL_CONFIG_BROKER_URL, objConfigManager.getConfiguration().getDocumentSource().getParameter("broker"));
        objConnectionFactory.setProperties(sslConfig);
        objConnection = objConnectionFactory.createConnection();
    }

    public static void parseArguments(String[] cArgs) {
        for (int i = 0; i < cArgs.length; ++i) {
            if ((i + 1) % 2 == 0 && i > 0 && "-config".equals(cArgs[i - 1])) {
                configLocation = cArgs[i];
            }
        }
    }

}
