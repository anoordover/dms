package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.metadata.IADocument;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.*;
import java.util.Properties;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.error;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ActiveMQManager implements DocumentSource {

    private static final String SSL_CONFIG_TRUSTSTORE = "trustStore";
    private static final String SSL_CONFIG_TRUSTSTOREPASS = "trustStorePassword";
    private static final String SSL_CONFIG_BROKER_URL = "brokerURL";

    ActiveMQSslConnectionFactory mobjConnectionFactory;
    Connection mobjConnection;
    PluggableObjectConfiguration mobjConfiguration;

    public ActiveMQManager(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
        mobjConnectionFactory = new ActiveMQSslConnectionFactory();
        Properties sslConfig = new Properties();
        sslConfig.setProperty(SSL_CONFIG_TRUSTSTORE, objConfiguration.getParameter("truststore"));
        sslConfig.setProperty(SSL_CONFIG_TRUSTSTOREPASS, objConfiguration.getParameter("truststore_password"));
        sslConfig.setProperty(SSL_CONFIG_BROKER_URL, objConfiguration.getParameter("broker"));
        mobjConnectionFactory.setProperties(sslConfig);
    }

    @Override
    public String retrieveDocumentData() {
        debug(this, "Retrieving Document Data");
        String sReturn = "";
        try {
            if (mobjConnection == null) {
                mobjConnection = mobjConnectionFactory.createConnection();
                mobjConnection.start();
            }
            Session objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination objDestination = objSession.createQueue(mobjConfiguration.getParameter("input_queue_name"));
            MessageConsumer objConsumer = objSession.createConsumer(objDestination);
            Message objMessage = objConsumer.receive(Integer.parseInt(mobjConfiguration.getParameter("queue_receive_timeout")));
            if (objMessage != null && objMessage instanceof TextMessage) {
                TextMessage objTextMessage = (TextMessage) objMessage;
                debug(this, "Received Data: " + objTextMessage.getText());
                sReturn = objTextMessage.getText();
            }
            objSession.close();
        } catch (JMSException ex) {
            error(this, ex);
        }
        debug(this, "Retrieved Data: " + sReturn);
        return sReturn;
    }

    @Override
    public void postResult(IADocument objDocument) {
        debug(this, "Posting Result " + objDocument.toString());
        try {
            if (mobjConnection == null) {
                mobjConnection = mobjConnectionFactory.createConnection();
            }
            mobjConnection.start();
            Session objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination objDestination = objSession.createQueue(mobjConfiguration.getParameter("result_queue_name"));
            MessageProducer objProducer = objSession.createProducer(objDestination);
            TextMessage objMessage = objSession.createTextMessage();

            String sStatusSuccess = mobjConfiguration.getParameter("result_success");
            String sStatusError = mobjConfiguration.getParameter("result_error");
            boolean bStatus = objDocument.getError() == null;
            String[] cResultValues = mobjConfiguration.getParameter("result_values").split(";");
            String[] cValues = new String[cResultValues.length];
            int iCurrent = 0;
            for (String sResultValue : cResultValues) {
                switch (sResultValue) {
                    case "{STATUS}":
                        cValues[iCurrent++] = bStatus ? sStatusSuccess : sStatusError;
                        break;
                    case "{ERROR}":
                        cValues[iCurrent++] = bStatus ? objDocument.getError() : "";
                        break;
                    default:
                        cValues[iCurrent++] = objDocument.getMetadata(sResultValue);
                        break;
                }
            }
            String sMessageText = String.format(mobjConfiguration.getParameter("result_format"), cValues);
            debug(this, "Sending Confirmation for document with ID: " + objDocument.getDocumentId());
            objMessage.setText(sMessageText);
            objProducer.send(objDestination, objMessage);
            mobjConnection.close();
        } catch (JMSException ex) {
            error(this, ex);
        }
        debug(this, "Exiting Post Result");
    }
}
