package nl.hetcak.dms;

import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;

import static com.amplexor.ia.Logger.info;

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
        String sDocumentTitle = objSource.getMetadata(mobjConfiguration.getParameter("retention_element_name"));
        if (sDocumentTitle != null) {
            for (IARetentionClass objRetentionClass : mobjConfiguration.getRetentionClasses()) {
                if (objRetentionClass instanceof CAKRetentionClass && ((CAKRetentionClass) objRetentionClass).getAssociatedDocumentTitle().contains(sDocumentTitle)) {
                    objReturn = objRetentionClass;
                    info(this, "Found Retention Class: " + objReturn.getName());
                    break;
                }
            }
        }

        if (objReturn == null) {
            throw new IllegalArgumentException("Document Title: " + sDocumentTitle + " Is not tied to a Retention Policy");
        }

        return objReturn;
    }
}
