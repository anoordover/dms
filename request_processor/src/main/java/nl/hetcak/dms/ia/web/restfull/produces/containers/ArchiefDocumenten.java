package nl.hetcak.dms.ia.web.restfull.produces.containers;

import nl.hetcak.dms.ia.web.requests.containers.InfoArchiveDocument;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@XmlRootElement(name = "ArchiefDocumenten", namespace = "urn:hetcak:dms:raadplegenuitingarchief:2016:11")
public class ArchiefDocumenten {
    private List<InfoArchiveDocument> documentList = new ArrayList<>();

    @XmlElement(name = "ArchiefDocument")
    public List<InfoArchiveDocument> getDocumentList() {
        return documentList;
    }
}
