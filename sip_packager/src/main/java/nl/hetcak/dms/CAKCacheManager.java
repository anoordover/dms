package nl.hetcak.dms;

import com.amplexor.ia.cache.AMPCacheManager;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import static com.amplexor.ia.Logger.debug;

/**
 * Created by zimmermannj on 9/30/2016.
 */
public class CAKCacheManager extends AMPCacheManager {

    public CAKCacheManager(CacheConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public void add(IADocument objDocument, IARetentionClass objRetentionClass) {
        debug(this, "Saving IADocument " + objDocument.getDocumentId());
        update();
        IACache objCache = getCache(objRetentionClass, objDocument.getMetadataKeys().contains("PersoonBurgerservicenummer"));
        if (objCache != null) {
            objCache.add(objDocument);
        }
        debug(this, "IADocument " + objDocument.getDocumentId() + " Saved");
    }

    private IACache getCache(IARetentionClass objRetentionClass, boolean bIsFallback) {
        for (IACache objCache : mcCaches) {
            if (objCache.getRetentionClass().equals(objRetentionClass) &&
                    objCache.getDocumentIdentifier() == (bIsFallback ? "Fallback" : "Standard")) {
                debug(this, "Returning Cache " + objCache.getId() + (bIsFallback ? " [Fallback]" : " [Standard]"));
                return objCache;
            }
        }

        IACache objCreate = new IACache(miNextId++, objRetentionClass);
        objCreate.setDocumentIdentifier(bIsFallback ? "Fallback" : "Standard");
        mcCaches.add(objCreate);
        debug(this, "Returning Cache " + objCreate.getId() + (bIsFallback ? " [Fallback]" : " [Standard]"));
        return objCreate;
    }
}
