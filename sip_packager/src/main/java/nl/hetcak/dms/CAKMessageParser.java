package nl.hetcak.dms;

import com.amplexor.ia.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import static com.amplexor.ia.Logger.*;

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
        info(this, "Parsing Document");
        IADocument objReturn = null;
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        Object objInstance = objXStream.fromXML(sData);
        if (objInstance != null && objInstance instanceof IADocument) {
            objReturn = (IADocument) objInstance;
            objReturn.setDocumentId(objReturn.getMetadata("ArchiefDocumentId"));
            info(this, "Data parsed into IADocument " + objReturn.getDocumentId());
        } else {
            warn(this, "Data could not be parsed (Set level to debug to print retrieved data)");
            debug(this, "Document Data: " + sData);
        }
        return objReturn;
    }
}
