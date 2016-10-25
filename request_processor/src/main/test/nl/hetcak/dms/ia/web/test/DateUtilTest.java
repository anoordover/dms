package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class DateUtilTest {
    private static final String REQUEST_DATE = "2016-08-01T12:00:00";
    private static final String INFOARCHIVE_DATE = "2016-08-01";
    
    @Test
    public void testDateUtil() throws Exception{
        String result = InfoArchiveDateUtil.convertToInfoArchiveDate(REQUEST_DATE);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contentEquals(INFOARCHIVE_DATE));
    
        String result2 = InfoArchiveDateUtil.convertToRequestDate(INFOARCHIVE_DATE);
        Assert.assertNotNull(result2);
        Assert.assertTrue(result2.contentEquals(REQUEST_DATE));
    }
}
