package nl.hetcak.dms;

import com.amplexor.ia.parsing.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.util.ArrayList;
import java.util.List;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParser implements MessageParser {
    public CAKMessageParser(PluggableObjectConfiguration objConfiguration) {

    }

    @Override
    public List<IADocument> parse(String sData) {
        info(this, "Parsing Document");
        List<IADocument> cReturn = new ArrayList<>();
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        Object objInstance = objXStream.fromXML(sData);
        if (objInstance != null && objInstance instanceof IADocument) {
            IADocument objDocument = (IADocument) objInstance;
            objDocument.setDocumentId(objDocument.getMetadata("ArchiefDocumentId"));
            cReturn.add(objDocument);
            info(this, "Data parsed into IADocument " + objDocument.getDocumentId());
        } else {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_INVALID_INPUT, new Exception("Unable to parse retrieved document data: \n" + sData));
        }
        return cReturn;
    }
}
