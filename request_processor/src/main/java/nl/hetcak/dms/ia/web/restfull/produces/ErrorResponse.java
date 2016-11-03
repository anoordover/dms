package nl.hetcak.dms.ia.web.restfull.produces;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "Error")
public class ErrorResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResponse.class);
    private int errorCode;
    private String errorDescription;

    @XmlElement(name="Code")
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @XmlElement(name="Description")
    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }


    public String getAsXML() {
        LOGGER.info("Building query for InfoArchive.");
        java.io.StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(ErrorResponse.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, sw);
        } catch (JAXBException jaxExc) {
            LOGGER.error("JAXB failed to create xml.", jaxExc);
        }
        LOGGER.info("Returning xml query String for InfoArchive.");
        return sw.toString();
    }
}
