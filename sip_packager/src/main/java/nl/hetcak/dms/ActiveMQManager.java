package nl.hetcak.dms;

import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.*;

import static com.amplexor.ia.Logger.debug;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ActiveMQManager implements DocumentSource {

    ActiveMQSslConnectionFactory mobjConnectionFactory;
    Connection mobjConnection;
    PluggableObjectConfiguration mobjConfiguration;

    public ActiveMQManager(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
        mobjConnectionFactory = new ActiveMQSslConnectionFactory();
        try {
            mobjConnectionFactory.setTrustStore(objConfiguration.getParameter("truststore"));
            mobjConnectionFactory.setTrustStoreType("JKS");
            mobjConnectionFactory.setTrustStorePassword(objConfiguration.getParameter("truststore_password"));
            mobjConnectionFactory.setBrokerURL(objConfiguration.getParameter("broker"));
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_INVALID_TRUSTSTORE, ex);
        }
    }

    @Override
    public String retrieveDocumentData() {
        debug(this, "Retrieving Document Data");
        String sReturn = "";
        Session objSession = null;
        try {
            if (mobjConnection != null) {
                objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination objDestination = objSession.createQueue(mobjConfiguration.getParameter("input_queue_name"));
                MessageConsumer objConsumer = objSession.createConsumer(objDestination);
                Message objMessage = objConsumer.receive(Integer.parseInt(mobjConfiguration.getParameter("queue_receive_timeout")));
                if (objMessage != null && objMessage instanceof TextMessage) {
                    TextMessage objTextMessage = (TextMessage) objMessage;
                    debug(this, "Received Data: " + objTextMessage.getText());
                    sReturn = objTextMessage.getText();
                }
            }
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } finally {
            if (objSession != null) {
                try {
                    objSession.close();
                } catch (JMSException ex) {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                }
            }
        }
        debug(this, "Retrieved Data: " + sReturn);
        return sReturn;
    }

    @Override
    public void initialize() {
        try {
            mobjConnection = mobjConnectionFactory.createConnection();
            mobjConnection.start();
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_UNABLE_TO_CONNECT, ex);
        }
    }

    @Override
    public void shutdown() {
        try {
            if (mobjConnection != null) {
                mobjConnection.close();
            }
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
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
