import static org.mockito.Mockito.*;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.ingest.ArchiveManager;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;
import com.amplexor.ia.sip.SipManager;
import nl.hetcak.dms.CAKMessageParser;
import nl.hetcak.dms.CAKRetentionManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Created by admjzimmermann on 20-9-2016.
 */
public class CAKDocumentRetrieval {
    public static String CONFIG_FILE = "IAArchiver.xml";
    public static String TEST_DATA_FILE = "resources\\testdata.xml";

    private ConfigManager mobjConfigManager;
    private DocumentSource mobjDocumentSource;
    private MessageParser mobjMessageParser;
    private RetentionManager mobjRetentionManager;
    private CacheManager mobjCacheManager;
    private SipManager mobjSipManager;
    private ArchiveManager mobjArchiveManager;

    private String msTestData;

    @Before
    public void setup() {
        mobjConfigManager = new ConfigManager(CONFIG_FILE);
        mobjConfigManager.loadConfiguration();
        Path objTestDataPath = Paths.get(System.getProperty("user.dir") + "/" + TEST_DATA_FILE);
        StringBuilder objBuilder = new StringBuilder();
        try {
            Files.readAllLines(objTestDataPath).forEach(sTestDataLine -> objBuilder.append(sTestDataLine));
        } catch (IOException ex) {
            fail(String.format("Could not retrieve data at %s, Message: %s", objTestDataPath.toString(), ex.getLocalizedMessage()));
        }
        msTestData = objBuilder.toString();

        mobjDocumentSource = mock(DocumentSource.class);
        when(mobjDocumentSource.retrieveDocumentData()).thenReturn(msTestData);

        SIPPackagerConfiguration objConfig = mobjConfigManager.getConfiguration();
        mobjMessageParser = new CAKMessageParser(objConfig.getMessageParser());
        mobjRetentionManager = new CAKRetentionManager(objConfig.getRetentionManager());
        mobjCacheManager = new CacheManager(objConfig.getCacheConfiguration());
        mobjSipManager = new SipManager(objConfig.getSipConfiguration());
        mobjArchiveManager = new ArchiveManager(objConfig.getServerConfiguration());
    }

    @Test
    public void testRun() throws Exception {
        String sDocumentData = mobjDocumentSource.retrieveDocumentData();
        assertEquals(sDocumentData, msTestData);

        IADocument objDocument = mobjMessageParser.parse(sDocumentData);
        assertNotNull(objDocument);

        IARetentionClass objRetentionClass = mobjRetentionManager.retrieveRetentionClass(objDocument);
        assertNotNull(objRetentionClass);
        assertEquals(objRetentionClass.getName(), "Strorno Al Aanmaning");

        IACache objCache = new IACache(0, objRetentionClass);
        objCache.add(objDocument);
        objCache.close();

        Path objSipPath = mobjSipManager.getSIPFile(objCache);
        assertNotNull(objSipPath);

        assertTrue(mobjArchiveManager.ingestSip(objSipPath.toString()));
        mobjDocumentSource.postResult(objDocument);

    }
}