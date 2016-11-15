package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.exceptions.SchemaLoadingException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class SchemaUtil {
    private static final String SCHEMA_FILE_NAME = "RaadplegenArchief.xsd";
    private static Schema mobjLoadedSchema;
    
    private SchemaUtil() {
        //see static methods
    }
    
    /**
     * Loads the schema if its not loaded yet.
     * @return The schema used to validate requests.
     * @throws SchemaLoadingException Schema loading problems.
     */
    public static Schema getSchema() throws SchemaLoadingException {
        if(mobjLoadedSchema == null) {
            mobjLoadedSchema = loadSchema();
        }
        return mobjLoadedSchema;
    }
    
    private static synchronized Schema loadSchema() throws SchemaLoadingException {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            File schemaFile = new File(classloader.getResource(SCHEMA_FILE_NAME).getFile());
            
            if(schemaFile.exists()) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                
                //SAXException while loading schema
                Schema schema = schemaFactory.newSchema(schemaFile);
                return schema;
            }
        } catch (SAXException saxeExc) {
            throw new SchemaLoadingException("Failed to parse XSD document.",saxeExc);
        } catch (NullPointerException nullExc) {
            throw new SchemaLoadingException("Unable to load Schema.", nullExc);
        }
        //Schema not found
        throw new SchemaLoadingException("Schema not found.");
    }
    
    
}
