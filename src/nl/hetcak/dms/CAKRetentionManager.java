package nl.hetcak.dms;

import com.amplexor.ia.DocumentSource;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.retention.RetentionManager;


/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CAKRetentionManager implements RetentionManager {

    public CAKRetentionManager(PluggableObjectConfiguration configuration) {

    }

    @Override
    public IARetentionClass retrieveRetentionClass(DocumentSource source) {
        return null;
    }
}
