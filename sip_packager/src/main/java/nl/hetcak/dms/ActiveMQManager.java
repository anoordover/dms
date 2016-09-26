package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.broker.region.*;

import javax.jms.*;
import javax.jms.Destination;
import javax.net.ssl.SSLException;
import java.security.KeyStoreException;
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
        try {
            mobjConnectionFactory.setKeyStore(objConfiguration.getParameter("truststore"));
            mobjConnectionFactory.setKeyStorePassword(objConfiguration.getParameter("truststore_password"));
            mobjConnectionFactory.setBrokerURL(objConfiguration.getParameter("broker"));
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_INVALID_TRUSTSTORE, ex);
        }
    }

    @Override
    public String retrieveDocumentData() {
        debug(this, "Retrieving Document Data");
        String sReturn = "";
        try {
            if (mobjConnection == null) {
                try {
                    mobjConnection = mobjConnectionFactory.createConnection();
                    mobjConnection.start();
                } catch (JMSException ex) {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_UNABLE_TO_CONNECT, ex);
                }
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
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        debug(this, "Retrieved Data: " + sReturn);
        return sReturn;
    }

    @Override
    public void postResult(IACache objCache) {
        debug(this, "Posting Results for IACache: " + objCache.getId());
        sendResult(String.format(mobjConfiguration.getParameter("results_element"), getResults(objCache)));
    }

    @Override
    public void postResult(IADocument objDocument) {
        debug(this, "Posting Result for IADocument: " + objDocument.toString());
        IACache objTempCache = new IACache(-1, null);
        objTempCache.add(objDocument);
        sendResult(String.format(mobjConfiguration.getParameter("results_element"), getResults(objTempCache)));
    }

    private void sendResult(String sMessageText) {
        try {
            if (mobjConnection == null) {
                mobjConnection = mobjConnectionFactory.createConnection();
            }
            Session objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination objDestination = objSession.createQueue(mobjConfiguration.getParameter("result_queue_name"));
            MessageProducer objProducer = objSession.createProducer(objDestination);
            TextMessage objMessage = objSession.createTextMessage();
            objMessage.setText(sMessageText);
            objProducer.send(objDestination, objMessage);
            objSession.close();
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
    }


    private String getResults(IACache objCache) {
        StringBuilder objBuilder = new StringBuilder();
        for (IADocument objDocument : objCache.getContents()) {
            String sSuccessMessage = mobjConfiguration.getParameter("result_success");
            String sErrorMessage = mobjConfiguration.getParameter("result_error");
            boolean bHasErrors = objDocument.getError() != null;
            String[] cResultValues = mobjConfiguration.getParameter("result_values").split(";");
            String[] cValues = new String[cResultValues.length];
            int iCurrent = 0;
            for (String sResultValue : cResultValues) {
                switch (sResultValue) {
                    case "{STATUS}":
                        cValues[iCurrent++] = bHasErrors ? sSuccessMessage : sErrorMessage;
                        break;
                    case "{ERROR}":
                        cValues[iCurrent++] = bHasErrors ? objDocument.getError() : "";
                        break;
                    default:
                        cValues[iCurrent++] = objDocument.getMetadata(sResultValue);
                        break;
                }
            }
            objBuilder.append(String.format(mobjConfiguration.getParameter("result_format"), cValues));
        }
        return objBuilder.toString();
    }
}
