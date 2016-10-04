package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.enums.*;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.utils.RandomGenerator;
import com.amplexor.ia.utils.TransformPDF;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.util.Enumeration;

/**
 * Created by admjzimmermann on 4-10-2016.
 */
public class MetaDataProducer {
    public static void main(String[] cArgs) {
        ConfigManager objConfigManager = new ConfigManager("IAArchiver.xml");
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

            MessageProducer objProducer = objSession.createProducer(objDestination);
            objProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            for (int i = 0; i < Integer.parseInt(objConfigManager.getConfiguration().getDocumentSource().getParameter("output_amount")); ++i) {
                RandomGenerator rg = new RandomGenerator();
                XmlDocument objDocument = new XmlDocument(
                        rg.generateArchiefDocumentId(),
                        rg.generateArchiefPersoonNummer(),
                        rg.generatePersoonBurgersservicenummer(),
                        rg.generateRandomEnum(ArchiefDocumenttitel.class),
                        rg.generateRandomEnum(ArchiefDocumentsoort.class),
                        rg.generateRandomEnum(ArchiefRegeling.class),
                        rg.generateDocumentKenmerkNr(),
                        rg.generateVerzendDag(),
                        rg.generateRandomEnum(ArchiefDocumenttype.class),
                        rg.generateRandomEnum(ArchiefDocumentstatus.class),
                        rg.generateVerzendDag().getYear(), TransformPDF.encodeBase64(objConfigManager.getConfiguration().getDocumentSource().getParameter("pdf_path") + File.separatorChar + rg.generateIntOneToSix()));

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
