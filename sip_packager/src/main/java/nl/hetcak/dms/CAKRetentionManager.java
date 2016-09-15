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
        if (objSource instanceof CAKDocument) {
            objDocument = (CAKDocument) objSource;
        }

        if (objDocument != null) {
            for (IARetentionClass retentionClass : mobjConfiguration.getRetentionClasses()) {
                if (retentionClass instanceof CAKRetentionClass && retentionClass.getName().equals(objDocument.getMetadata(mobjConfiguration.getRetentionElementName()))) {
                    objReturn = (CAKRetentionClass) retentionClass;
                }
            }
        }

        return objReturn;
    }
}
