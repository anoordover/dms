package nl.hetcak.dms.ia.web.util;

import nl.hetcak.dms.ia.web.comunication.InfoArchiveServerConnectionInformation;
import nl.hetcak.dms.ia.web.comunication.ServerConnectionInformation;
import org.junit.Assert;
import org.junit.Test;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveRequestUtilTest {
    private static final String SERVER_URL = "1.1.1.1";
    private static final String SERVER_SELECTOR = "application";
    private static final String SERVER_CID = "1234-1234-1234";
    private static final int SERVER_PORT = 1234;
    private static final int MAX_ITEMS = 500;
    
    private static final String EXPECTED_HTTP_URL = "http://1.1.1.1:1234/application/1234-1234-1234?size=500";
    private static final String EXPECTED_HTTPS_URL = "https://1.1.1.1:1234/application/1234-1234-1234?size=500";
    
    @Test
    public void httpUrlTest() {
        InfoArchiveRequestUtil requestUtil = new InfoArchiveRequestUtil(getConnectionInformation(false));
        String url = requestUtil.getServerUrl(SERVER_SELECTOR,SERVER_CID);
        Assert.assertTrue(url.contains("http://"));
        Assert.assertTrue(url.contentEquals(EXPECTED_HTTP_URL));
    }
    
    @Test
    public void httpsUrlTest() {
        InfoArchiveRequestUtil requestUtil = new InfoArchiveRequestUtil(getConnectionInformation(true));
        String url = requestUtil.getServerUrl(SERVER_SELECTOR,SERVER_CID);
        Assert.assertTrue(url.contains("https://"));
        Assert.assertTrue(url.contentEquals(EXPECTED_HTTPS_URL));
    }
    
    private ServerConnectionInformation getConnectionInformation(Boolean useHttps) {
        InfoArchiveServerConnectionInformation iasci = new InfoArchiveServerConnectionInformation();
        iasci.setHttps(useHttps);
        iasci.setMaxItems(MAX_ITEMS);
        iasci.setServerAddress(SERVER_URL);
        iasci.setServerPort(SERVER_PORT);
        return iasci;
    }
}
