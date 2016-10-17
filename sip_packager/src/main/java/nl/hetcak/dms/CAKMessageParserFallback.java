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
public class CAKMessageParserFallback implements MessageParser {

    public CAKMessageParserFallback(PluggableObjectConfiguration objConfiguration) {

    }

    @Override
    public List<IADocument> parse(String sData) {
        info(this, "Parsing Document");
        List<IADocument> objReturn = new ArrayList<>();
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        try {
            Object objInstance = objXStream.fromXML(sData);
            if (objInstance != null && objInstance instanceof IADocument) {
                IADocument objParsedDocument = (IADocument) objInstance;
                IADocument objDocument = new CAKDocument();
                for (String sKey : objParsedDocument.getMetadataKeys()) {
                    if (!"PersoonBurgerservicenummer".equals(sKey)) {
                        objDocument.setMetadata(sKey, objParsedDocument.getMetadata(sKey));
                    }
                }
                objDocument.setDocumentId(objDocument.getMetadata("ArchiefDocumentId"));
                byte[] cPayloadData = objParsedDocument.loadContent(CAKDocument.KEY_ATTACHMENT);
                if (cPayloadData.length > 0) {
                    objDocument.setContent(CAKDocument.KEY_ATTACHMENT, cPayloadData);
                } else {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, objDocument, new Exception("Error parsing PayloadPdf"));
                }
                objReturn.add(objDocument);

                IADocument objDocumentUitwijk = new CAKDocument();
                for (String sKey : objParsedDocument.getMetadataKeys()) {
                    if (!"ArchiefPersoonsnummer".equals(sKey)) {
                        objDocumentUitwijk.setMetadata(sKey, objParsedDocument.getMetadata(sKey));
                    }
                }
                objDocumentUitwijk.setDocumentId(objDocumentUitwijk.getMetadata("ArchiefDocumentId"));
                if (cPayloadData.length > 0) {
                    objDocumentUitwijk.setContent(CAKDocument.KEY_ATTACHMENT, cPayloadData);
                } else {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, objDocumentUitwijk, new Exception("Error parsing PayloadPdf"));
                }
                objReturn.add(objDocumentUitwijk);

                info(this, "Data parsed into IADocument " + objDocument.getDocumentId());
            }
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_INVALID_INPUT, ex);
        }
        return objReturn;
    }
}
