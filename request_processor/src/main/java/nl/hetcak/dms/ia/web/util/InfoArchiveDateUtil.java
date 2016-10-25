package nl.hetcak.dms.ia.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveDateUtil {
    private final static String INFOARCHIVE_DATE_FORMAT = "yyyy-MM-dd";
    private final static String REQUEST_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
    
    /**
     * Converts a Request Date formatted {@link String} to a InfoArchive formatted {@link String}.
     * @param requestDate The request Date {@link String}.
     * @return a InfoArchive formatted Date {@link String}.
     * @throws ParseException is thrown when there is a format mismatch.
     */
    public static String convertToInfoArchiveDate(String requestDate) throws ParseException{
        SimpleDateFormat requestFormat = new SimpleDateFormat(REQUEST_DATE_FORMAT);
        SimpleDateFormat infoArchiveFormat = new SimpleDateFormat(INFOARCHIVE_DATE_FORMAT);
        
        return infoArchiveFormat.format(requestFormat.parse(requestDate));
    }
    
    /**
     * Converts a Request Date to a InfoArchive formatted {@link String}.
     * @param requestDate The request {@link Date}.
     * @return a InfoArchive formatted Date {@link String}.
     * @throws ParseException is thrown when there is a format mismatch.
     */
    public static String convertToInfoArchiveDate(Date requestDate) throws ParseException{
        SimpleDateFormat infoArchiveFormat = new SimpleDateFormat(INFOARCHIVE_DATE_FORMAT);
        
        return infoArchiveFormat.format(requestDate);
    }
    
    /**
     * Converts a InfoArchive Date {@link String} to a Request Response Formatted {@link String}.
     * @param infoarchiveDate The InfoArchive formatted date {@link String}.
     * @return A Request Response Formatted {@link String}.
     * @throws ParseException is thrown when there is a format mismatch.
     */
    public static String convertToRequestDate(String infoarchiveDate) throws ParseException{
        SimpleDateFormat requestFormat = new SimpleDateFormat(REQUEST_DATE_FORMAT);
        SimpleDateFormat infoArchiveFormat = new SimpleDateFormat(INFOARCHIVE_DATE_FORMAT);
        
        return requestFormat.format(infoArchiveFormat.parse(infoarchiveDate));
    }
    
    /**
     * Converts a InfoArchive {@link Date} to a Request Response Formatted {@link String}.
     * @param infoarchiveDate The InfoArchive {@link Date}.
     * @return A Request Response Formatted {@link String}.
     * @throws ParseException is thrown when there is a format mismatch.
     */
    public static String convertToRequestDate(Date infoarchiveDate) throws ParseException{
        SimpleDateFormat requestFormat = new SimpleDateFormat(REQUEST_DATE_FORMAT);
        
        return requestFormat.format(infoarchiveDate);
    }
}
