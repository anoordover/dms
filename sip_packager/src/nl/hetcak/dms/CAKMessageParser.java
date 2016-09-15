package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.metadata.IADocument;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParser implements MessageParser {
    private PluggableObjectConfiguration mobjConfiguration;

    public CAKMessageParser(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public IADocument parse(DocumentSource objSource) {
        if (objSource instanceof ActiveMQManager) {
            CAKDocument objDocument = ((ActiveMQManager) objSource).retrieveDocument();
            //TODO: Parse Metadata??
            return objDocument;
        }
        return new CAKDocument();
    }
}
