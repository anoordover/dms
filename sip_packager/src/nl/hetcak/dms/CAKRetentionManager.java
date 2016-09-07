package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.configuration.RetentionManagerConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;

import java.util.List;


/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKRetentionManager implements RetentionManager {
    RetentionManagerConfiguration configuration;

    public CAKRetentionManager(RetentionManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public IARetentionClass retrieveRetentionClass(IADocument source) {
        CAKRetentionClass rval = null;
        if (source instanceof CAKDocument) {
            CAKDocument document = (CAKDocument) source;
            String metadataObject = document.getMetadata("Logistiek_Briefnaam");
            if (metadataObject != null) {
                for (IARetentionClass retentionClass : configuration.getRetentionClasses()) {
                    if (retentionClass instanceof CAKRetentionClass) {
                        if (((CAKRetentionClass) retentionClass).getName().equals(metadataObject)) {
                            rval = (CAKRetentionClass) retentionClass;
                        }
                    }
                }
            }
        }
        return rval;
    }
}
