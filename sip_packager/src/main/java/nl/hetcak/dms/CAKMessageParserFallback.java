package nl.hetcak.dms;

import com.amplexor.ia.configuration.MessageParserConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.parsing.MessageParser;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
