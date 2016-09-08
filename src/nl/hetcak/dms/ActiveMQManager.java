package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.jms.*;
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
        sslConfig.setProperty("brokerURL", configuration.getParameter("broker"));
        connectionFactory.setProperties(sslConfig);
    }

    @Override
    public CAKDocument retrieveDocument() {
        CAKDocument rval = null;
        try {
            if (connection == null) {
                connection = connectionFactory.createConnection();
            }
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("AanleverenArchiefUitingen");
            MessageConsumer consumer = session.createConsumer(destination);
            Message message = consumer.receive();
            if(message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage)message;
                System.out.println("Received: " + textMessage.getText());
                XStream xstream = new XStream(new StaxDriver());
                xstream.alias("Document", CAKDocument.class);
                xstream.processAnnotations(CAKDocument.class);
                rval = (CAKDocument)xstream.fromXML(textMessage.getText());
            }
            connection.close();
        } catch (JMSException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return rval;
    }

    @Override
    public void postResult() {
        try {
            if(connection == null) {
                connection = connectionFactory.createConnection();
            }
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("VerwerkArchiefUitingenStatus");
            MessageProducer producer = session.createProducer(destination);
            /* TODO: Send Result
            TextMessage message = session.createTextMessage();
            message.setText("{ document_id : 10001000, status : ingested }");
            producer.send(destination, message);
            */
            connection.close();
        }
        catch(JMSException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
