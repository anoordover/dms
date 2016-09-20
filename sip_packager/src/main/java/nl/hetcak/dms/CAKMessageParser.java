package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParser implements MessageParser {
    private PluggableObjectConfiguration mobjConfiguration;

    public CAKMessageParser(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public IADocument parse(String sData) {
        IADocument objReturn = null;
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        Object objInstance = objXStream.fromXML(sData);
        if (objInstance != null && objInstance instanceof IADocument) {
            objReturn = (IADocument) objInstance;
            if (objReturn != null) {
                objReturn.setDocumentId(objReturn.getMetadata("ArchiefDocumentId"));
            }
        }
        return objReturn;
    }
}
