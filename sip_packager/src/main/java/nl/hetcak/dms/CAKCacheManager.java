package nl.hetcak.dms;

import com.amplexor.ia.cache.AMPCacheManager;
import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.amplexor.ia.Logger.*;

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
        if (checkGroupPath(objRetentionClass, true, objDocument.getMetadataKeys().contains("PersoonBurgerservicenummer"))) {
            IACache objCache = getCache(objRetentionClass, objDocument.getMetadataKeys().contains("PersoonBurgerservicenummer"));
            if (objCache != null) {
                objCache.add(objDocument);
            }
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
        try {
            Path objCachePath = Paths.get(String.format("%s/%s/%s/%d", mobjBasePath.toString(), objRetentionClass.getName(), (bIsFallback ? "Fallback" : "Standard"), objCreate.getId()).replace('/', File.separatorChar));
            Files.createDirectories(objCachePath);
            objCreate.setDocumentIdentifier(bIsFallback ? "Fallback" : "Standard");
            mcCaches.add(objCreate);
            debug(this, "Returning Cache " + objCreate.getId() + (bIsFallback ? " [Fallback]" : " [Standard]"));
        } catch (IOException ex) {
            miNextId--;
            objCreate = null;
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } catch (InvalidPathException ex) {
            miNextId--;
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_INVALID_BASE_PATH, ex);
        }

        return objCreate;
    }

    private boolean checkGroupPath(IARetentionClass objRetentionClass, boolean bCreate, boolean bIsFallback) {
        debug(this, "Checking group file path for IARetentionClass " + objRetentionClass.getName());
        boolean bReturn;
        try {
            Path objGroupPath = Paths.get(String.format("%s/%s/%s", mobjBasePath.toString(), objRetentionClass.getName(), bIsFallback ? "Fallback" : "Standard"));
            bReturn = Files.exists(objGroupPath);

            if (!bReturn && bCreate) {
                Files.createDirectories(objGroupPath);
                bReturn = Files.exists(objGroupPath);
            }
            debug(this, "Found file path for IARetentionClass " + objRetentionClass.getName() + (bIsFallback ? " [Standard]" : "[Fallback]"));
        } catch (IOException ex) {
            bReturn = false;
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return bReturn;
    }
}
