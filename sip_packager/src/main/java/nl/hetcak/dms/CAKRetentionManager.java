package nl.hetcak.dms;

import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;


/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKRetentionManager implements RetentionManager {
    RetentionManagerConfiguration mobjConfiguration;

    public CAKRetentionManager(RetentionManagerConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public IARetentionClass retrieveRetentionClass(IADocument objSource) {
        CAKRetentionClass objReturn = null;
        CAKDocument objDocument = null;
        String sRetentionName = null;
        if (objSource instanceof CAKDocument) {
            objDocument = (CAKDocument) objSource;
            sRetentionName = objDocument.getMetadata(mobjConfiguration.getRetentionElementName());
        }

        if (objDocument != null && sRetentionName != null) {
            for (IARetentionClass retentionClass : mobjConfiguration.getRetentionClasses()) {
                if (retentionClass.getName().equals(sRetentionName)) {
                    objReturn = (CAKRetentionClass) retentionClass;
                    break;
                }
            }
        }

        return objReturn;
    }
}
