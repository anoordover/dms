package nl.hetcak.dms;

import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.retention.IARetentionClass;
import com.emc.ia.sdk.sip.assembly.DataSubmissionSession;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by zimmermannj on 10/28/2016.
 */
public class CAKSipManagerFallback extends CAKSipManager {

    public CAKSipManagerFallback(IASipConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public PackagingInformation getCAKPackageInformation(IARetentionClass objRetentionClass, boolean bIsFallback) {
        GregorianCalendar objCalendar = new GregorianCalendar();
        objCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        objCalendar.set(Calendar.MONTH, 0);
        objCalendar.set(Calendar.DAY_OF_MONTH, 1);
        objCalendar.set(Calendar.SECOND, 0);
        objCalendar.set(Calendar.MINUTE, 0);
        objCalendar.set(Calendar.HOUR, 0);
        objCalendar.set(Calendar.MILLISECOND, 0);
        objCalendar.setTimeZone(TimeZone.getDefault());


        String sApplicationName = bIsFallback ? mobjConfiguration.getParameter("fallback_application_name") : mobjConfiguration.getApplicationName();
        String sHoldingName = bIsFallback ? mobjConfiguration.getParameter("fallback_holding_name") : mobjConfiguration.getHoldingName();
        PackagingInformation.PackagingInformationBuilder objBuilder = PackagingInformation.builder();
        DataSubmissionSession.DataSubmissionSessionBuilder objDssBuilder = objBuilder.dss();
        objDssBuilder.holding(sHoldingName)
                .application(sApplicationName)
                .producer(mobjConfiguration.getProducerName())
                .entity(mobjConfiguration.getEntityName())
                .schema(mobjConfiguration.getSchemaDeclaration())
                .retentionClass(!bIsFallback ?
                        ((CAKRetentionClassFallback) objRetentionClass).getPolicy() : ((CAKRetentionClassFallback) objRetentionClass).getFallbackPolicy());
        if (mobjConfiguration.getParameter("set_base_retention_date") != null && "true".equals(mobjConfiguration.getParameter("set_base_retention_date").toLowerCase())) {
            objDssBuilder.baseRetentionDate(objCalendar.getTime());
        }
        return objDssBuilder.end().build();
    }
}
