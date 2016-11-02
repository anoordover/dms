package com.amplexor.ia;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.utils.RandomGenerator;
import com.amplexor.ia.utils.TransformPDF;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.util.Enumeration;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 4-10-2016.
 */
public class MetaDataProducer {
    private static final String[] ARCHIEF_DOCUMENT_SOORT_ARRAY = {"Factuur"};
    private static final String[] ARCHIEF_DOCUMENT_STATUS_ARRAY = {"Concept", "Definitief"};
    private static final String[] ARCHIEF_DOCUMENT_TITEL_ARRAY = {"Z01", "Z02", "Z03", "B01", "B02", "B03"};
    private static final String[] ARCHIEF_DOCUMENT_TYPE_ARRAY = {"Inkomend", "Intern", "Uitgaand"};
    private static final String[] ARCHIEF_REGELING_ARRAY = {"Wlz", "Wmo"};

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

            info(MetaDataProducer.class, "Connecting");
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
            info(MetaDataProducer.class, "Found " + iCount + "Messages in Queue: " + objConfigManager.getConfiguration().getDocumentSource().getParameter("input_queue_name"));

            if ("clean".equals(objConfigManager.getConfiguration().getDocumentSource().getParameter("queue_action"))) {
                info(MetaDataProducer.class, "Emptying Queue");
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
                    info(MetaDataProducer.class, "Queue Empty, Feeding Random MetaData now.");
                }
            }

            info(MetaDataProducer.class, "Generating " + objConfigManager.getConfiguration().getDocumentSource().getParameter("output_amount") + " Messages");
            MessageProducer objProducer = objSession.createProducer(objDestination);
            objProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            for (int i = 0; i < Integer.parseInt(objConfigManager.getConfiguration().getDocumentSource().getParameter("output_amount")); ++i) {
                RandomGenerator rg = new RandomGenerator();
                XmlDocument objDocument = new XmlDocument();
                objDocument.setMetadata("ArchiefDocumentId", rg.generateId(10));
                objDocument.setMetadata("ArchiefPersoonsnummer", rg.generateId(10));
                objDocument.setMetadata("PersoonBurgerservicenummer", rg.generateId(9));
                objDocument.setMetadata("ArchiefDocumenttitel", ARCHIEF_DOCUMENT_TITEL_ARRAY[rg.randomInt(ARCHIEF_DOCUMENT_TITEL_ARRAY.length - 1)]);
                objDocument.setMetadata("ArchiefDocumentsoort", ARCHIEF_DOCUMENT_SOORT_ARRAY[rg.randomInt(ARCHIEF_DOCUMENT_SOORT_ARRAY.length - 1)]);
                objDocument.setMetadata("ArchiefRegeling", ARCHIEF_REGELING_ARRAY[rg.randomInt(ARCHIEF_REGELING_ARRAY.length - 1)]);
                objDocument.setMetadata("ArchiefDocumentkenmerk", rg.generateId(6));
                objDocument.setMetadata("ArchiefVerzenddag", rg.generateVerzendDag().toString());
                objDocument.setMetadata("ArchiefDocumenttype", ARCHIEF_DOCUMENT_TYPE_ARRAY[rg.randomInt(ARCHIEF_DOCUMENT_TYPE_ARRAY.length - 1)]);
                objDocument.setMetadata("ArchiefDocumentstatus", ARCHIEF_DOCUMENT_STATUS_ARRAY[rg.randomInt(ARCHIEF_DOCUMENT_STATUS_ARRAY.length - 1)]);
                objDocument.setMetadata("ArchiefRegelingsjaar", String.format("%04d", rg.generateVerzendDag().getYear()));
                objDocument.setPayloadPdf(TransformPDF.encodeBase64(objConfigManager.getConfiguration().getDocumentSource().getParameter("pdf_path") + File.separatorChar + rg.randomInt(6) + ".pdf"));

                TextMessage objMessage = objSession.createTextMessage(objDocument.getXml());
                objProducer.send(objMessage);
            }
            objProducer.close();
            objSession.close();
            objConnection.close();
            info(MetaDataProducer.class, "DONE");
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
    }
}
