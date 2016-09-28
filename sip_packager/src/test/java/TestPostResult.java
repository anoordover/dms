import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.metadata.IADocument;
import nl.hetcak.dms.ActiveMQManager;
import nl.hetcak.dms.CAKDocument;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by admjzimmermann on 21-9-2016.
 */
public class TestPostResult {
    public static String CONFIG_FILE = "IAArchiver.xml";
    private ConfigManager mobjConfigManager;
    private DocumentSource mobjDocumentSource;

    @Before
    public void setup() {
        mobjConfigManager = new ConfigManager(CONFIG_FILE);
        mobjConfigManager.loadConfiguration();
        mobjDocumentSource = new ActiveMQManager(mobjConfigManager.getConfiguration().getDocumentSource());
    }

    @Test
    public void testPostResult() throws Exception {
        IADocument objDocument = new CAKDocument();
        objDocument.setMetadata("ArchiefDocumentId", "1000010000");
        objDocument.setError("Some Error Occurred");
        mobjDocumentSource.postResult(objDocument);

    }
}
