package nl.hetcak.dms;

import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.exception.ExceptionHelper;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.*;
import java.util.List;

import static com.amplexor.ia.Logger.debug;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ActiveMQManager implements DocumentSource {
    private static final String PARAMETER_BROKER = "broker";
    private static final String PARAMETER_TRUSTSTORE = "truststore";
    private static final String PARAMETER_TRUSTSTORE_PASSWORD = "truststore_password";
    private static final String PARAMETER_INPUT_QUEUE_NAME = "input_queue_name";
    private static final String PARAMETER_RESULT_QUEUE_NAME = "result_queue_name";
    private static final String PARAMETER_QUEUE_RECEIVE_TIMEOUT = "queue_receive_timeout";
    private static final String TRUSTSTORE_TYPE = "JKS";
    private static final String PARAMETER_RESULT_VALUE = "result_value";
    private static final String PARAMETER_RESULT_FORMAT = "result_format";
    private static final String PARAMETER_RESULTS_ELEMENT = "results_element";


    ActiveMQSslConnectionFactory mobjConnectionFactory;
    Connection mobjConnection;
    PluggableObjectConfiguration mobjConfiguration;
    boolean bConnected;

    public ActiveMQManager(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
        mobjConnectionFactory = new ActiveMQSslConnectionFactory();
        mobjConnectionFactory.setBrokerURL(objConfiguration.getParameter(PARAMETER_BROKER));
        if (mobjConfiguration.getParameter(PARAMETER_TRUSTSTORE) != null) {
            try {
                mobjConnectionFactory.setTrustStore(objConfiguration.getParameter(PARAMETER_TRUSTSTORE));
                mobjConnectionFactory.setTrustStoreType(TRUSTSTORE_TYPE);
                mobjConnectionFactory.setTrustStorePassword(objConfiguration.getParameter(PARAMETER_TRUSTSTORE_PASSWORD));
            } catch (Exception ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_INVALID_TRUSTSTORE, ex);
            }
        }
    }

    @Override
    public String retrieveDocumentData() {
        debug(this, "Retrieving Document Data");
        String sReturn = "";
        Session objSession = null;
        MessageConsumer objConsumer = null;
        try {
            if (mobjConnection != null) {
                objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination objDestination = objSession.createQueue(mobjConfiguration.getParameter(PARAMETER_INPUT_QUEUE_NAME) + "?consumer.prefetchSize=1");
                objConsumer = objSession.createConsumer(objDestination);
                Message objMessage = objConsumer.receive(Integer.parseInt(mobjConfiguration.getParameter(PARAMETER_QUEUE_RECEIVE_TIMEOUT)));
                if (objMessage == null) {
                    objConsumer.close();
                    return "";
                }
                if (objMessage instanceof TextMessage) {
                    TextMessage objTextMessage = (TextMessage) objMessage;
                    sReturn = objTextMessage.getText();
                    objMessage.acknowledge();
                }

                objConsumer.close();
            }
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } finally {
            try {
                if (objConsumer != null) {
                    objConsumer.close();
                }

                if (objSession != null) {
                    objSession.close();
                }
            } catch (JMSException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }
        debug(this, "Retrieved Data: " + sReturn);
        return sReturn;
    }

    @Override
    public boolean initialize() {
        boolean bReturn = false;
        try {
            if (mobjConnection == null) {
                mobjConnection = mobjConnectionFactory.createConnection();
            }
            if (!bConnected) {
                mobjConnection.start();
                bConnected = bReturn = true;
            }
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_UNABLE_TO_CONNECT, ex);
            shutdown();
        }

        return bReturn;
    }

    @Override
    public boolean shutdown() {
        boolean bReturn = false;
        try {
            if (mobjConnection != null && bConnected) {
                mobjConnection.close();
                bConnected = false;
                bReturn = true;
            }
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return bReturn;
    }

    @Override
    public boolean postResult(List<IADocumentReference> cDocuments) {
        boolean bReturn = false;
        Session objSession = null;
        MessageProducer objProducer = null;

        if (mobjConnection == null) {
            return false;
        }

        try {
            objSession = mobjConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue objDestination = objSession.createQueue(mobjConfiguration.getParameter(PARAMETER_RESULT_QUEUE_NAME));
            objProducer = objSession.createProducer(objDestination);
            TextMessage objMessage = objSession.createTextMessage(getResultXml(cDocuments));
            objProducer.send(objDestination, objMessage);
            bReturn = true;
        } catch (JMSException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } finally {
            try {
                if (objProducer != null) {
                    objProducer.close();
                }

                if (objSession != null) {
                    objSession.close();
                }
            } catch (JMSException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                bReturn = false;
            }
        }

        return bReturn;
    }

    private String getResultXml(List<IADocumentReference> cDocuments) {
        StringBuilder objBuilder = new StringBuilder();
        for (IADocumentReference objReference : cDocuments) {
            String[] cResultValues = mobjConfiguration.getParameter(PARAMETER_RESULT_VALUE).split(";");
            String[] cValues = new String[cResultValues.length];
            int iCurrent = 0;
            for (String sResultValue : cResultValues) {
                switch (sResultValue) {
                    case "{ERROR}":
                        cValues[iCurrent++] = String.format("%04d", objReference.getErrorCode());
                        break;
                    case "{MESSAGE}":
                        cValues[iCurrent++] = objReference.getErrorMessage();
                        break;
                    case "{ID}":
                        cValues[iCurrent++] = objReference.getDocumentId();
                        break;
                    default:
                        cValues[iCurrent++] = cValues[iCurrent];
                        break;
                }
            }
            objBuilder.append(String.format(mobjConfiguration.getParameter(PARAMETER_RESULT_FORMAT), cValues));
        }
        return String.format(mobjConfiguration.getParameter(PARAMETER_RESULTS_ELEMENT), objBuilder.toString());
    }
}
