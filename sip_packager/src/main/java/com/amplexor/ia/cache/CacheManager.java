package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.amplexor.ia.Logger.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CacheManager {
    private CacheConfiguration mobjConfiguration;
    private List<IACache> mcCaches;

    private int miNextId;
    private Path mobjBasePath;
    private Path mobjSavePath;

    public CacheManager(CacheConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
        mcCaches = new ArrayList<>();
        miNextId = 0;
    }

    public void initializeCache() throws IOException {
        debug(this, "Initializing CacheManager");
        try {
            mobjBasePath = Paths.get(
                    String.format("%s/%s/", mobjConfiguration.getCacheBasePath(), Thread.currentThread().getName()).replace('/', File.separatorChar));
            mobjSavePath = Paths.get(
                    String.format("%s/save", mobjBasePath.toString()).replace('/', File.separatorChar));

            if (!Files.exists(mobjBasePath)) {
                Files.createDirectories(mobjBasePath);
            }

            if (!Files.exists(mobjSavePath)) {
                Files.createDirectories(mobjSavePath);
            } else {
                loadCaches();
            }
            debug(this, "CacheManager Initialized");
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } catch (InvalidPathException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_INVALID_BASE_PATH, ex);
        }
    }

    public void add(IADocument objDocument, IARetentionClass objRetentionClass) {
        debug(this, "Saving IADocument " + objDocument.getDocumentId());
        update();
        if (checkGroupPath(objRetentionClass, true)) {
            IACache cache = getCache(objRetentionClass);
            if (cache != null) {
                cache.add(objDocument);
            }
        }
        debug(this, "IADocument " + objDocument.getDocumentId() + " Saved");
    }

    /*private void saveDocument(IADocument objDocument,IACache objCache) {
        try {
            try(OutputStream objDocumentSaveStream = Files.newOutputStream(Paths.get(String.format("%s/%s/%s", mobjBasePath.toString(), ))))
        } catch (IOException ex) {

        }
    }*/

    private IACache getCache(IARetentionClass objRetentionClass) {
        debug(this, "Getting Cache for IARetentionClass " + objRetentionClass.getName());
        for (IACache objCache : mcCaches) {
            if (objCache.getRetentionClass().equals(objRetentionClass) && !objCache.isClosed()) {
                debug(this, "Returning Cache " + objCache.getId());
                return objCache;
            }
        }

        IACache objCreate = new IACache(miNextId++, objRetentionClass);
        try {
            Path oCachePath = Paths.get(String.format("%s/%s/%d", mobjBasePath.toString(), objRetentionClass.getName(), objCreate.getId()).replace('/', File.separatorChar));
            Files.createDirectories(oCachePath);
            mcCaches.add(objCreate);
            debug(this, "Returning Cache " + objCreate.getId());
        } catch (IOException ex) {
            miNextId--;
            objCreate = null;
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } catch (InvalidPathException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_INVALID_BASE_PATH, ex);

        }
        return objCreate;
    }

    private boolean checkGroupPath(IARetentionClass objRetentionClass, boolean bCreate) {
        debug(this, "Checking group file path for IARetentionClass " + objRetentionClass.getName());
        boolean bReturn;
        try {
            Path objGroupPath = Paths.get(String.format("%s/%s", mobjBasePath.toString(), objRetentionClass.getName()));
            bReturn = Files.exists(objGroupPath);
            if (!bReturn && bCreate) {
                Files.createDirectories(objGroupPath);
                bReturn = Files.exists(objGroupPath);
            }
            debug(this, "Found file path for IARetentionClass " + objRetentionClass.getName());
        } catch (IOException ex) {
            bReturn = false;
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        return bReturn;
    }

    public void update() {
        debug(this, "Updating Caches");
        for (IACache objCache : mcCaches) {
            if (!objCache.isClosed()) {
                if (objCache.getSize() >= mobjConfiguration.getCacheMessageThreshold()) {
                    info(this, String.format("Closing cache %s-%d, Reason: Message Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheMessageThreshold()));
                    objCache.close();
                } else if (objCache.getCreated() <= (System.currentTimeMillis() - (mobjConfiguration.getCacheTimeThreshold() * 1000))) {
                    info(this, String.format("Closing cache %s-%d, Reason: Time Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheTimeThreshold()));
                    objCache.close();
                }
            }
        }
        debug(this, "Caches Updated");
    }

    public List<IACache> getClosedCaches() {
        debug(this, "Fetching Closed Caches");
        List<IACache> objClosed = new ArrayList<>();
        for (IACache objCache : mcCaches) {
            if (objCache.isClosed()) {
                objClosed.add(objCache);
            }
        }
        debug(this, "Found " + objClosed.size() + " Closed Caches");
        return Collections.unmodifiableList(objClosed);
    }

    public void cleanupCache(IACache objCache) {
        debug(this, "Cleaning Cache " + objCache.getId());
        try {
            if (objCache.isClosed()) {
                Files.deleteIfExists(Paths.get(String.format("%s/%s/%d", mobjBasePath.toString(), objCache.getRetentionClass().getName(), objCache.getId())));
                mcCaches.remove(objCache);
            }
            info(this, "Cache " + objCache.getId() + " Has Been Deleted");
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
        }
    }

    public void saveCaches() {
        for (IACache objCache : mcCaches) {
            try (OutputStream objSaveStream = Files.newOutputStream(Paths.get(mobjSavePath.toString() + File.separatorChar + "IACache-" + objCache.getId() + ".xml"))) {
                XStream objXStream = new XStream(new StaxDriver());
                objXStream.alias("IACache", IACache.class);
                objXStream.processAnnotations(IACache.class);
                objXStream.toXML(objCache, objSaveStream);
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }
    }

    public void loadCaches() {
        List<File> cSaveContents = Arrays.asList(new File(mobjSavePath.toString() + File.separatorChar).listFiles());
        cSaveContents.forEach( objCacheSaveFile -> {
            try (InputStream objCacheSaveInput = Files.newInputStream(objCacheSaveFile.toPath())) {
                XStream objXStream = new XStream(new StaxDriver());
                objXStream.alias("IACache", IACache.class);
                objXStream.processAnnotations(IACache.class);
                mcCaches.add((IACache)objXStream.fromXML(objCacheSaveInput));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
            objCacheSaveFile.delete();
        });
    }
}
