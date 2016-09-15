package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.Properties;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ActiveMQManager implements DocumentSource {
    private static Logger logger = Logger.getLogger("ActiveMQManager");

    private static final String SSL_CONFIG_TRUSTSTORE = "trustStore";
    private static final String SSL_CONFIG_TRUSTSTOREPASS = "trustStorePassword";
    private static final String SSL_CONFIG_BROKER_URL = "brokerUrl";

    ActiveMQConnectionFactory mobjConnectionFactory;
    Connection mobjConnection;

    public ActiveMQManager(PluggableObjectConfiguration objConfiguration) {
        mobjConnectionFactory = new ActiveMQSslConnectionFactory();
        Properties sslConfig = new Properties();
        sslConfig.setProperty(SSL_CONFIG_TRUSTSTORE, objConfiguration.getParameter("truststore"));
        sslConfig.setProperty(SSL_CONFIG_TRUSTSTOREPASS, objConfiguration.getParameter("truststore_password"));
        sslConfig.setProperty(SSL_CONFIG_BROKER_URL, objConfiguration.getParameter("broker"));
        mobjConnectionFactory.setProperties(sslConfig);
    }

    @Override
    public CAKDocument retrieveDocument() {
        CAKDocument objReturn = null;
        try {
            if (mobjConnection == null) {
                mobjConnection = mobjConnectionFactory.createConnection();
            }
            mobjConnection.start();
            Session objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination objDestination = objSession.createQueue("AanleverenArchiefUitingen");
            MessageConsumer objConsumer = objSession.createConsumer(objDestination);
            Message objMessage = objConsumer.receive();
            if (objMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) objMessage;
                logger.info("Received: " + textMessage.getText());
                XStream objXStream = new XStream(new StaxDriver());
                objXStream.alias("Document", CAKDocument.class);
                objXStream.processAnnotations(CAKDocument.class);
                objReturn = (CAKDocument) objXStream.fromXML(textMessage.getText());
            }
            mobjConnection.close();
        } catch (JMSException ex) {
            logger.error(ex);
        }
        return objReturn;
    }

    @Override
    public void postResult() {
        try {
            if (mobjConnection == null) {
                mobjConnection = mobjConnectionFactory.createConnection();
            }
            mobjConnection.start();
             /* TODO: Send Result
            Session session = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("VerwerkArchiefUitingenStatus");
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage();
            message.setText("{ document_id : 10001000, status : ingested }");
            producer.send(destination, message);
            */
            mobjConnection.close();
        } catch (JMSException ex) {
            logger.error(ex);
        }
    }
}
