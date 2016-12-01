package nl.hetcak.dms.ia.web.restfull.consumers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zimmermannj on 12/1/2016.
 */
@XmlRootElement(name = "ArchiefPersoonsnummers")
public class ArchiefPersoonsnummers {
    protected List<String> mcArchiefPersoonsnummers;

    public ArchiefPersoonsnummers() {
        mcArchiefPersoonsnummers = new ArrayList<>();
    }

    public ArchiefPersoonsnummers(List<String> cArchiefPersoonsnummers) {
        mcArchiefPersoonsnummers = cArchiefPersoonsnummers;
    }

    @XmlElement(name = "ArchiefPersoonsnummer")
    public List<String> getArchiefPersoonsnummers() {
        return mcArchiefPersoonsnummers;
    }
}
