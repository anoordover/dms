package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.metadata.IADocument;
import org.omg.PortableInterceptor.ACTIVE;
import org.w3c.dom.Document;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParser implements MessageParser {
    public CAKMessageParser(PluggableObjectConfiguration configuration) {

    }

    @Override
    public IADocument parse(DocumentSource source) {
        if(source instanceof ActiveMQManager) {
            CAKDocument document = ((ActiveMQManager)source).retrieveDocument();
            //TODO: Parse Metadata??
            return document;
        }
        return null; //We only read from ActiveMQ
    }
}
