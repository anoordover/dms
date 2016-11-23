package nl.hetcak.dms;

import com.amplexor.ia.configuration.MessageParserConfiguration;
import com.amplexor.ia.metadata.IADocument;

import java.util.ArrayList;
import java.util.List;

import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParserFallback extends CAKMessageParser {
    public CAKMessageParserFallback(MessageParserConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public List<IADocument> parse(String sData) {
        info(this, "Parsing Document");
        List<IADocument> objReturn = new ArrayList<>();
        IADocument objStandardDocument = parseDocument(sData, mobjConfiguration.getParameter("schema_name"), "PersoonBurgerservicenummer");
        IADocument objFallbackDocument = parseDocument(sData, mobjConfiguration.getParameter("fallback_schema_name"), "ArchiefPersoonsnummer");
        if (objStandardDocument != null) {
            objReturn.add(objStandardDocument);
        }

        if (objFallbackDocument != null) {
            objReturn.add(objFallbackDocument);
        }

        return objReturn;
    }

}
