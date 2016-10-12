package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.enums.*;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.utils.RandomGenerator;
import com.amplexor.ia.utils.TransformPDF;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.util.Enumeration;

/**
 * Created by admjzimmermann on 4-10-2016.
 */
public class MetaDataProducer {
    private MetaDataProducer() { //Hide implicit public ctor

    }

    public static void main(String[] cArgs) {
        ConfigManager objConfigManager = new ConfigManager("data_generator/IAArchiver.xml");
        objConfigManager.loadConfiguration();
        ExceptionHelper.getExceptionHelper().setExceptionConfiguration(objConfigManager.getConfiguration().getExceptionConfiguration());

        ActiveMQSslConnectionFactory objConnectionFactory = new ActiveMQSslConnectionFactory(objConfigManager.getConfiguration().getDocumentSource().getParameter("broker"));
        Connection objConnection = null;
        try {
            objConnectionFactory.setTrustStore(objConfigManager.getConfiguration().getDocumentSource().getParameter("truststore"));
            objConnectionFactory.setTrustStorePassword(objConfigManager.getConfiguration().getDocumentSource().getParameter("truststore_password"));

            objConnection = objConnectionFactory.createConnection();
            objConnection.start();

            Session objSession = objConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue objDestination = objSession.createQueue(objConfigManager.getConfiguration().getDocumentSource().getParameter("input_queue_name"));


            QueueBrowser objBrowser = objSession.createBrowser(objDestination);
            int iCount = 0;
            Enumeration objEnum = objBrowser.getEnumeration();
            while (objEnum.hasMoreElements()) {
                iCount++;
                objEnum.nextElement();
            }
            Logger.info(MetaDataProducer.class, "Found " + iCount + "Messages in Queue: " + objConfigManager.getConfiguration().getDocumentSource().getParameter("input_queue_name"));

            if (objConfigManager.getConfiguration().getDocumentSource().getParameter("queue_action").equals("clean")) {
                Logger.info(MetaDataProducer.class, "Emptying Queue");
                MessageConsumer objConsumer = objSession.createConsumer(objDestination);
                for (int iMsg = 0; iMsg < iCount; ++iMsg) {
                    objConsumer.receive(500);
                }
                objConsumer.close();

                iCount = 0;
                objEnum = objBrowser.getEnumeration();
                while (objEnum.hasMoreElements()) {
                    iCount++;
                    objEnum.nextElement();
                }

                if (iCount == 0) {
                    Logger.info(MetaDataProducer.class, "Queue Empty, Feeding Random MetaData now.");
                }
            }
            MessageProducer objProducer = objSession.createProducer(objDestination);
            objProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            for (int i = 0; i < Integer.parseInt(objConfigManager.getConfiguration().getDocumentSource().getParameter("output_amount")); ++i) {
                RandomGenerator rg = new RandomGenerator();
                XmlDocument objDocument = new XmlDocument();
                objDocument.setArchiefDocumentId(rg.generateArchiefDocumentId());
                objDocument.setArchiefPersoonsnummer(rg.generateArchiefPersoonNummer());
                objDocument.setPersoonBurgerservicenummer(rg.generatePersoonBurgersservicenummer());
                objDocument.setArchiefDocumenttitel(rg.generateRandomEnum(ArchiefDocumenttitel.class));
                objDocument.setArchiefDocumentsoort(rg.generateRandomEnum(ArchiefDocumentsoort.class));
                objDocument.setArchiefRegeling(rg.generateRandomEnum(ArchiefRegeling.class));
                objDocument.setArchiefDocumentkenmerk(rg.generateDocumentKenmerkNr());
                objDocument.setVerzenddag(rg.generateVerzendDag());
                objDocument.setArchiefDocumenttype(rg.generateRandomEnum(ArchiefDocumenttype.class));
                objDocument.setArchiefDocumentstatus(rg.generateRandomEnum(ArchiefDocumentstatus.class));
                objDocument.setRegelingjaar(rg.generateVerzendDag().getYear());
                objDocument.setPayloadPdf(TransformPDF.encodeBase64(objConfigManager.getConfiguration().getDocumentSource().getParameter("pdf_path") + File.separatorChar + rg.generateIntOneToSix()));

                TextMessage objMessage = objSession.createTextMessage(objDocument.getXml());
                Logger.info(MetaDataProducer.class, "Generated Document With ID:" + objDocument.getArchiefDocumentId());
                objProducer.send(objMessage);
            }
            objProducer.close();
            objSession.close();
            objConnection.close();
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
    }
}
