import static org.mockito.Mockito.*;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.MessageParser;
import com.amplexor.ia.cache.CacheManager;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.configuration.SIPPackagerConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.ingest.ArchiveManager;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;
import com.amplexor.ia.sip.AMPSipManager;
import nl.hetcak.dms.CAKMessageParser;
import nl.hetcak.dms.CAKMessageParserUitwijk;
import nl.hetcak.dms.CAKRetentionManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private AMPSipManager mobjSipManager;
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
        mobjMessageParser = new CAKMessageParserUitwijk(objConfig.getMessageParser());
        mobjRetentionManager = new CAKRetentionManager(objConfig.getRetentionManager());
        mobjCacheManager = new CacheManager(objConfig.getCacheConfiguration());
        mobjSipManager = new AMPSipManager(objConfig.getSipConfiguration());
        mobjArchiveManager = new ArchiveManager(objConfig.getServerConfiguration());
        ExceptionHelper.getExceptionHelper().setExceptionConfiguration(mobjConfigManager.getConfiguration().getExceptionConfiguration());
    }

    @Test
    public void testRun() throws Exception {
        String sDocumentData = mobjDocumentSource.retrieveDocumentData();
        assertEquals(sDocumentData, msTestData);

        IADocument objDocument = mobjMessageParser.parse(sDocumentData).get(0);
        assertNotNull(objDocument);

        IARetentionClass objRetentionClass = mobjRetentionManager.retrieveRetentionClass(objDocument);
        //assertNotNull(objRetentionClass);
        //assertEquals(objRetentionClass.getName(), "Strorno Al Aanmaning");

        IACache objCache = new IACache(0, objRetentionClass);
        objCache.add(objDocument);
        objCache.close();

        assertTrue(mobjSipManager.getSIPFile(objCache));
        //assertNotNull(objCache.getSipFile());

        assertTrue(mobjArchiveManager.ingestSip(objCache.getSipFile().toString()));
        mobjDocumentSource.postResult(objDocument);
    }

    @Test
    public void testMultipleMessages() throws Exception {
        mobjCacheManager.initializeCache();
        String sDocumentData = mobjDocumentSource.retrieveDocumentData();
        for (int i = 0; i < 100; ++i) {
            IADocument objDocument = mobjMessageParser.parse(sDocumentData).get(1);
            IARetentionClass objRetentionClass = mobjRetentionManager.retrieveRetentionClass(objDocument);

            int iDocId = Integer.parseInt(objDocument.getDocumentId());
            objDocument.setDocumentId(String.format("%d", (iDocId + i)));
            objDocument.setMetadata("ArchiefDocumentId", objDocument.getDocumentId());
            mobjCacheManager.add(objDocument, objRetentionClass);
        }
        mobjCacheManager.update();
        mobjCacheManager.saveCaches();
        for(IACache objCache : mobjCacheManager.getClosedCaches()) {
            mobjSipManager.getSIPFile(objCache);
            mobjArchiveManager.ingestSip(objCache.getSipFile().toString());
            mobjDocumentSource.postResult(objCache);
        }
    }
}