package nl.hetcak.dms.ia.web.test;

import nl.hetcak.dms.ia.web.util.InfoArchiveDateUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * This test will test the {@link InfoArchiveDateUtil}.
 * The {@link InfoArchiveDateUtil} should be able to read a InfoArchive Date String and convert them to a Response Date String, and vice versa.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class DateUtilTest {
    private static final String REQUEST_DATE = "2016-08-01T12:00:00";
    private static final String INFOARCHIVE_DATE = "2016-08-01";
    
    @Test
    public void testDateUtilWithStrings() throws Exception {
        String result = InfoArchiveDateUtil.convertToInfoArchiveDate(REQUEST_DATE);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contentEquals(INFOARCHIVE_DATE));
    
        String result2 = InfoArchiveDateUtil.convertToRequestDate(INFOARCHIVE_DATE);
        Assert.assertNotNull(result2);
        Assert.assertTrue(result2.contentEquals(REQUEST_DATE));
    }
    
    @Test
    public void testDateUtilWithDates() throws Exception {
        String requestDateFormat = "yyyy-MM-dd'T'hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(requestDateFormat);
        Date date = sdf.parse(REQUEST_DATE);
    
        Assert.assertTrue(INFOARCHIVE_DATE.contentEquals(InfoArchiveDateUtil.convertToInfoArchiveDate(date)));
        Assert.assertTrue(REQUEST_DATE.contentEquals(InfoArchiveDateUtil.convertToRequestDate(date)));
    }
}
