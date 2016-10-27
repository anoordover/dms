package nl.hetcak.dms;

import com.amplexor.ia.cache.AMPCacheManager;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.amplexor.ia.Logger.debug;

/**
 * Created by zimmermannj on 9/30/2016.
 */
public class CAKCacheManager extends AMPCacheManager {

    public CAKCacheManager(CacheConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public boolean add(IADocument objDocument, IARetentionClass objRetentionClass) {
        debug(this, "Saving IADocument " + objDocument.getDocumentId());

        boolean bReturn = false;
        update();
        try {
            IACache objCache = getCache(objRetentionClass, objDocument.getMetadataKeys().contains("PersoonBurgerservicenummer"));
            if (objCache != null) {
                objCache.add(new IADocumentReference(objDocument.getDocumentId(), saveDocument(objCache, objDocument)));
                bReturn = true;
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        debug(this, "IADocument " + objDocument.getDocumentId() + " Saved");

        return bReturn;
    }

    private IACache getCache(IARetentionClass objRetentionClass, boolean bIsFallback) throws IOException {
        for (IACache objCache : mcCaches) {
            if (objCache.getRetentionClass().equals(objRetentionClass) &&
                    objCache.getDocumentIdentifier() == (bIsFallback ? "Fallback" : "Standard")) {
                debug(this, "Returning Cache " + objCache.getId() + (bIsFallback ? " [Fallback]" : " [Standard]"));
                return objCache;
            }
        }

        //No Open Cache found, create a new cache for this retention class
        IACache objCreate = new IACache(miNextId++, objRetentionClass);
        Path objCachePath = Paths.get(String.format("%s/%d", mobjBasePath.toString(), objCreate.getId()));
        Files.createDirectories(objCachePath);
        objCreate.setDocumentIdentifier(bIsFallback ? "Fallback" : "Standard");
        mcCaches.add(objCreate);
        saveCache(objCreate);
        debug(this, "Returning Cache " + objCreate.getId());

        return objCreate;
    }
}
