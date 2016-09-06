package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import org.w3c.dom.Document;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKMessageParser implements MessageParser {
    public CAKMessageParser(PluggableObjectConfiguration configuration) {

    }

    @Override
    public Document parse(DocumentSource source) {
        return null;
    }
}
