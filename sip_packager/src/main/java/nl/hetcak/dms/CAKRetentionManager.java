package nl.hetcak.dms;

import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;

import static com.amplexor.ia.Logger.*;

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
        info(this, "Retrieving Retention Class for IADocument: " + objSource.getDocumentId());

        IARetentionClass objReturn = null;
        String sRetentionName;
        sRetentionName = objSource.getMetadata(mobjConfiguration.getRetentionElementName());


        if (sRetentionName != null) {
            for (IARetentionClass retentionClass : mobjConfiguration.getRetentionClasses()) {
                if (retentionClass.getName().equals(sRetentionName)) {
                    objReturn = retentionClass;
                    info(this, "Found Retention Class: " + objReturn.getName());
                    break;
                }
            }
        }

        return objReturn;
    }
}