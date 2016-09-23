package nl.hetcak.dms;

import com.amplexor.ia.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.util.ArrayList;
import java.util.List;

import static com.amplexor.ia.Logger.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParserUitwijk implements MessageParser {
    public CAKMessageParserUitwijk(PluggableObjectConfiguration objConfiguration) {

    }

    @Override
    public List<IADocument> parse(String sData) {
        info(this, "Parsing Document");
        List<IADocument> objReturn = new ArrayList<>();
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        Object objInstance = objXStream.fromXML(sData);
        if (objInstance != null && objInstance instanceof IADocument) {
            IADocument objParsedDocument = (IADocument) objInstance;
            IADocument objDocument = new CAKDocument();
            objParsedDocument.getMetadataKeys().forEach(key -> {
                if(!"ArchiefBurgerservicenummer".equals(key)) {
                    objDocument.setMetadata(key, objParsedDocument.getMetadata(key));
                }
            });
            objDocument.setDocumentId(objDocument.getMetadata("ArchiefDocumentId"));
            objReturn.add(objDocument);

            IADocument objDocumentUitwijk = new CAKDocument();
            objParsedDocument.getMetadataKeys().forEach(key -> {
                if(!"ArchiefPersoonsnummer".equals(key)) {
                    objDocumentUitwijk.setMetadata(key, objParsedDocument.getMetadata(key));
                }
            });
            objDocumentUitwijk.setDocumentId(objDocumentUitwijk.getMetadata("ArchiefDocumentId"));
            objReturn.add(objDocumentUitwijk);

            info(this, "Data parsed into IADocument " + objDocument.getDocumentId());
        } else {
            warn(this, "Data could not be parsed (Set level to debug to print retrieved data)");
            debug(this, "Document Data: " + sData);
        }
        return objReturn;
    }
}