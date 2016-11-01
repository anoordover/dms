package nl.hetcak.dms;

import com.amplexor.ia.configuration.MessageParserConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.parsing.MessageParser;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
public class CAKMessageParser implements MessageParser {
    MessageParserConfiguration mobjConfiguration;

    public CAKMessageParser(MessageParserConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public List<IADocument> parse(String sData) {
        info(this, "Parsing Document");
        List<IADocument> objReturn = new ArrayList<>();
        IADocument objStandardDocument = parseDocument(sData, mobjConfiguration.getParameter("schema_name"), "PersoonBurgerservicenummer");
        if (objStandardDocument != null) {
            objReturn.add(objStandardDocument);
        }
        return objReturn;
    }

    protected IADocument parseDocument(String sData, String sValidationSchema, String... cExceptions) {
        CAKDocument objReturn = null;
        CAKDocument objSourceDocument = null;
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias("ArchiefDocument", CAKDocument.class);
        objXStream.processAnnotations(CAKDocument.class);
        Object objStreamOutput = objXStream.fromXML(sData);
        if (objStreamOutput instanceof CAKDocument) {
            objSourceDocument = (CAKDocument) objStreamOutput;
        }

        if (objSourceDocument != null) {
            objReturn = parseMetadata(objSourceDocument, cExceptions);
            byte[] cPayload = objSourceDocument.getContent(CAKDocument.KEY_ATTACHMENT);
            if (cPayload.length > 0) {
                objReturn.setContent(CAKDocument.KEY_ATTACHMENT, cPayload);
            } else {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, objReturn, new Exception("Error loading PayloadPdf"));
            }
            if (objReturn != null) {
                String sAIUDataFinal = createAIUData(objReturn, cExceptions);
                validateDocument(objReturn, String.format("%s/%s", mobjConfiguration.getParameter("schema_location"), sValidationSchema));
                objReturn.setAIU(sAIUDataFinal);
            }
        }

        return objReturn;
    }

    private CAKDocument parseMetadata(IADocument objSourceDocument, String... cExceptions) {
        CAKDocument objReturn = new CAKDocument();
        for (String sKey : objSourceDocument.getMetadataKeys()) {
            setDocumentMetadata(sKey, objReturn, objSourceDocument, cExceptions);
        }
        return objReturn;
    }

    private void setDocumentMetadata(String sKey, IADocument objDocument, IADocument objSourceDocument, String... cExceptions) {
        for (String sException : cExceptions) {
            if (!sException.equals(sKey)) {
                if (sKey.equals(mobjConfiguration.getDocumentIdElement())) {
                    objDocument.setDocumentId(objSourceDocument.getMetadata(sKey));
                }
                objDocument.setMetadata(sKey, objSourceDocument.getMetadata(sKey));
            }
        }
    }

    private String createAIUData(IADocument objDocument, String... cExceptions) {
        try {
            DocumentBuilderFactory objFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder objBuilder = objFactory.newDocumentBuilder();
            Document objXmlDocument = objBuilder.newDocument();
            Element objDocumentElement = objXmlDocument.createElementNS(mobjConfiguration.getAIUNamespace(), mobjConfiguration.getAIUElementName());
            objXmlDocument.appendChild(objDocumentElement);
            for (Map.Entry<String, String> objMapping : mobjConfiguration.getAIUMapping().entrySet()) {
                if (!shouldOmitKey(objMapping, cExceptions)) {
                    List<Integer> cIndicesStart = new ArrayList<>();
                    List<Integer> cIndicesEnd = new ArrayList<>();
                    char[] cMappingCharacters = objMapping.getValue().toCharArray();

                    extractIndices(cMappingCharacters, cIndicesStart, cIndicesEnd);

                    //Ensure that opening have corresponding closing tags
                    checkForTags(cIndicesStart, cIndicesEnd, objMapping);
                    Element objMetadataElement = objXmlDocument.createElement(objMapping.getKey());
                    objMetadataElement.setTextContent(getAIUElementString(objMapping.getValue(), objDocument, cIndicesStart, cIndicesEnd));
                    objDocumentElement.appendChild(objMetadataElement);
                }
            }
            TransformerFactory objTransformerFactory = TransformerFactory.newInstance();
            Transformer objTransformer = objTransformerFactory.newTransformer();
            objTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter objStringWriterDoc = new StringWriter();
            StreamResult objResult = new StreamResult(objStringWriterDoc);
            objTransformer.transform(new DOMSource(objXmlDocument), objResult);
            objDocument.setAIU(objStringWriterDoc.toString());

            StringWriter objStringWriterRval = new StringWriter();
            StreamResult objResultRval = new StreamResult(objStringWriterRval);
            for (int i = 0; i < objDocumentElement.getChildNodes().getLength(); i++) {
                objTransformer.transform(new DOMSource(objDocumentElement.getChildNodes().item(i)), objResultRval);
            }
            return objStringWriterRval.toString();
        } catch (ParserConfigurationException | TransformerException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return "";
    }

    private void checkForTags(List<Integer> cIndicesStart, List<Integer> cIndicesEnd, Map.Entry<String, String> objMapping) {
        if (cIndicesStart.size() != cIndicesEnd.size()) {
            throw new IllegalArgumentException("Error in AIU_mapping, a '{' is missing its closing '}' " + objMapping.getKey());
        }
    }

    private boolean shouldOmitKey(Map.Entry<String, String> objMapping, String... cExceptions) {
        for (String sKey : cExceptions) {
            if (sKey.equals(objMapping.getKey())) {
                return true;
            }
        }
        
        return false;
    }

    private void extractIndices(char[] cMappingCharacters, List<Integer> cIndicesStart, List<Integer> cIndicesEnd) {
        boolean bEscaped = false;
        for (int i = 0; i < cMappingCharacters.length; ++i) {
            if (cMappingCharacters[i] == '{' && !bEscaped) {
                cIndicesStart.add(i);
            } else if (cMappingCharacters[i] == '}' && !bEscaped) {
                cIndicesEnd.add(i);
            } else if (cMappingCharacters[i] == '\\') {
                bEscaped = true;
                continue;
            }

            bEscaped = false;
        }
    }

    private String getAIUElementString(String sData, IADocument objDocument, List<Integer> cIndicesStart, List<Integer> cIndicesEnd) {
        int iParamCount = cIndicesStart.size();
        int iCounter = 0;
        int iLength = sData.length() - 1; //Dont count the '\0' character
        StringBuilder objFormatBuilder = new StringBuilder();
        String[] cParameters = new String[iParamCount];

        if (iParamCount == 0) {
            return sData;
        }

        if (cIndicesStart.get(0) > 0) {
            objFormatBuilder.append(sData.substring(0, cIndicesStart.get(0)));
        }

        while (iCounter < iParamCount) {
            String sKey = sData.substring(cIndicesStart.get(iCounter) + 1, cIndicesEnd.get(iCounter));
            cParameters[iCounter] = objDocument.getMetadata(sKey);
            objFormatBuilder.append("%s");
            if (cIndicesEnd.get(iCounter) < iLength) {
                if (iCounter + 1 < iParamCount) {
                    objFormatBuilder.append(sData.substring(cIndicesEnd.get(iCounter) + 1, cIndicesStart.get(iCounter + 1) - 1));
                } else {
                    objFormatBuilder.append(sData.substring(cIndicesEnd.get(iCounter) + 1, sData.length()));
                }
            }
            iCounter++;
        }

        return String.format(objFormatBuilder.toString(), cParameters);
    }

    private void validateDocument(IADocument objDocument, String sXsdFile) {
        try {
            SchemaFactory objSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema objSchema = objSchemaFactory.newSchema(new File(sXsdFile));
            Validator objValidator = objSchema.newValidator();
            objValidator.validate(new StreamSource(new StringReader(objDocument.getAIU())));
        } catch (SAXException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SOURCE_VALIDATION_FAILED, objDocument, ex);
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
    }
}
