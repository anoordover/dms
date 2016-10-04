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
    private static final String KEY_ATTACHMENT = "Attachement";

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
                objParsedDocument.getMetadataKeys().forEach(key -> {
                    if (!"PersoonBurgerservicenummer".equals(key)) {
                        objDocument.setMetadata(key, objParsedDocument.getMetadata(key));
                    }
                });
                objDocument.setContent(KEY_ATTACHMENT, ((IADocument) objInstance).loadContent(KEY_ATTACHMENT));
                objDocument.setDocumentId(objDocument.getMetadata("ArchiefDocumentId"));
                objReturn.add(objDocument);

                IADocument objDocumentUitwijk = new CAKDocument();
                objParsedDocument.getMetadataKeys().forEach(key -> {
                    if (!"ArchiefPersoonsnummer".equals(key)) {
                        objDocumentUitwijk.setMetadata(key, objParsedDocument.getMetadata(key));
                    }
                });
                objDocumentUitwijk.setContent(KEY_ATTACHMENT, ((IADocument) objInstance).loadContent(KEY_ATTACHMENT));
                objDocumentUitwijk.setDocumentId(objDocumentUitwijk.getMetadata("ArchiefDocumentId"));
                objReturn.add(objDocumentUitwijk);

                info(this, "Data parsed into IADocument " + objDocument.getDocumentId());
            }
        } catch (Exception ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_INVALID_INPUT, ex);
        }
        return objReturn;
    }
}
