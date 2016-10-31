package nl.hetcak.dms;

import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import static com.amplexor.ia.Logger.info;

/**
 * Created by zimmermannj on 10/28/2016.
 */
public class CAKRetentionManagerFallback extends CAKRetentionManager {

    public CAKRetentionManagerFallback(RetentionManagerConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public IARetentionClass retrieveRetentionClass(IADocument objSource) {
        info(this, "Retrieving Retention Class for IADocument: " + objSource.getDocumentId());

        IARetentionClass objReturn = null;
        String sMetadata = objSource.getMetadata(mobjConfiguration.getParameter("retention_element_name"));
        if (sMetadata != null) {
            for (IARetentionClass objRetentionClass : mobjConfiguration.getRetentionClasses()) {
                if (objRetentionClass instanceof CAKRetentionClassFallback && ((CAKRetentionClassFallback) objRetentionClass).getAssociatedTitles().contains(sMetadata)) {
                    objReturn = objRetentionClass;
                    objSource.setMetadata(mobjConfiguration.getParameter("retention_element_pdi_name"), ((CAKRetentionClass) objRetentionClass).getHandelingNr());
                    info(this, "Found Retention Class: " + objReturn.getName());
                    break;
                }
            }
        }

        if (objReturn == null) {
            throw new IllegalArgumentException("Metadata Object value: " + sMetadata + " could not be resolved to a retention class");
        }

        return objReturn;
    }

}
