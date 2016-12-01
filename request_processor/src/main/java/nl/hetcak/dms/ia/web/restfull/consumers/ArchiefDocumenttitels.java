package nl.hetcak.dms.ia.web.restfull.consumers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zimmermannj on 12/1/2016.
 */
@XmlRootElement(name = "ArchiefDocumenttitels")
public class ArchiefDocumenttitels {
    protected List<String> mcArchiefDocumenttitels;

    public ArchiefDocumenttitels() {
        mcArchiefDocumenttitels = new ArrayList<>();
    }

    public ArchiefDocumenttitels(List<String> cArchiefDocumenttitels) {
        mcArchiefDocumenttitels = cArchiefDocumenttitels;
    }

    @XmlElement(name = "ArchiefDocumenttitel")
    public List<String> getArchiefDocumenttitels() {
        return mcArchiefDocumenttitels;
    }
}
