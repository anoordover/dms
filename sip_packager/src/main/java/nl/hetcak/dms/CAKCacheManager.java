package nl.hetcak.dms;

import com.amplexor.ia.cache.AMPCacheManager;
import com.amplexor.ia.configuration.CacheConfiguration;

/**
 * Created by zimmermannj on 9/30/2016.
 */
public class CAKCacheManager extends AMPCacheManager {

    public CAKCacheManager(CacheConfiguration objConfiguration) {
        super(objConfiguration);
    }
}
