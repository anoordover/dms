package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
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
    public Document retrieveDocument() {
        Document rval = null;
        try {
            if (connection == null) {
                connection = connectionFactory.createConnection();
            }
        } catch (JMSException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return rval;
    }

    @Override
    public Node retrieveMetadata() {
        return null;
    }

    @Override
    public void postResult() {

    }
}
