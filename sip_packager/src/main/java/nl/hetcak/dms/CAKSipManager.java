package nl.hetcak.dms;

import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.sip.AMPSipManager;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by zimmermannj on 9/30/2016.
 */
public class CAKSipManager extends AMPSipManager {

    public CAKSipManager(IASipConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public PackagingInformation getPackageInformation(IADocument objDocument, IARetentionClass objRetentionClass) {
        if (objDocument.getMetadataKeys().contains("ArchiefPersoonsnummer")) {
            return super.getPackageInformation(objDocument, objRetentionClass);
        } else {
            GregorianCalendar objCalendar = new GregorianCalendar();
            objCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
            objCalendar.set(Calendar.MONTH, 1);
            objCalendar.set(Calendar.DAY_OF_MONTH, 1);
            objCalendar.set(Calendar.SECOND, 0);
            objCalendar.set(Calendar.MINUTE, 0);
            objCalendar.set(Calendar.HOUR, 0);
            objCalendar.set(Calendar.MILLISECOND, 0);
            objCalendar.setTimeZone(TimeZone.getDefault());

            return PackagingInformation.builder().dss()
                    .holding("CAK_Tijdelijk_Klantarchief")
                    .application("CAK_Tijdelijk_Klantarchief")
                    .producer(mobjConfiguration.getProducerName())
                    .entity(mobjConfiguration.getEntityName())
                    .schema(mobjConfiguration.getSchemaDeclaration())
                    .retentionClass(objRetentionClass.getName().replace(' ', '_'))
                    .baseRetentionDate(objCalendar.getTime())
                    .end().build();
        }
    }


}
