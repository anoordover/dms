package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
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
import java.util.stream.Collectors;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

/**
 * The {@link CacheManager} is responsible for keeping track of {@link IACache} objects, the folder structure associated with the caches and saving any data contained in these caches while they exist.
 * Created by admjzimmermann on 6-9-2016.
 */
public class AMPCacheManager implements CacheManager {
    protected CacheConfiguration mobjConfiguration;
    protected List<IACache> mcCaches;

    protected int miNextId;
    protected Path mobjBasePath;
    protected Path mobjSavePath;
    protected Path mobjErrorPath;

    public AMPCacheManager(CacheConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
        mcCaches = new ArrayList<>();
        miNextId = 0;
    }

    /**
     * Initializes the {@link CacheManager} based on the {@link CacheConfiguration}.
     * The following actions will be executed:
     * - Create the cache base folder if it does not exist
     * - Create the cache save folder if it does not exist
     * - Load any saved caches in the cache save folder and delete the files
     *
     * @throws IOException
     */
    @Override
    public boolean initializeCache() throws IOException {
        debug(this, "Initializing CacheManager");
        boolean bReturn = false;
        try {
            mobjBasePath = Paths.get(
                    String.format("%s/%s/", mobjConfiguration.getCacheBasePath(), Thread.currentThread().getName()).replace('/', File.separatorChar));
            mobjSavePath = Paths.get(
                    String.format("%s/save", mobjBasePath.toString()).replace('/', File.separatorChar));
            mobjErrorPath = Paths.get(
                    String.format("%s/error", mobjBasePath.toString()).replace('/', File.separatorChar));

            if (!Files.exists(mobjBasePath)) {
                Files.createDirectories(mobjBasePath);
            }

            if (!Files.exists(mobjErrorPath)) {
                Files.createDirectories(mobjErrorPath);
            }

            if (!Files.exists(mobjSavePath)) {
                Files.createDirectories(mobjSavePath);
            } else {
                loadCaches();
            }
            bReturn = true;
            debug(this, "CacheManager Initialized");
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        } catch (InvalidPathException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_INVALID_BASE_PATH, ex);
        }

        return bReturn;
    }

    /**
     * Adds {@link IADocument} objDocument to the cache for the {@link IARetentionClass} objRetentionClass
     * Note: the update() method will be called before adding the document to ensure the current {@link IACache} does not exceed it's message threshold
     *
     * @param objDocument
     * @param objRetentionClass
     */
    @Override
    public boolean add(IADocument objDocument, IARetentionClass objRetentionClass) {
        debug(this, "Saving IADocument " + objDocument.getDocumentId());

        boolean bReturn = false;
        update();
        try {
            IACache objCache = getCache(objRetentionClass);
            if (objCache != null) {
                objCache.add(new IADocumentReference(objDocument, saveDocument(objCache, objDocument)));
                bReturn = true;
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        debug(this, "IADocument " + objDocument.getDocumentId() + " Saved");

        return bReturn;
    }

    protected String saveDocument(IACache objCache, IADocument objDocument) throws IOException {
        Path objDocumentPath = Paths.get(String.format("%s/%d/%s.xml", mobjBasePath.toString(), objCache.getId(), objDocument.getDocumentId()));
        XStream objXStream = new XStream(new DomDriver("UTF-8"));
        Class<?> objClass = IADocument.class;
        try {
            objClass = Thread.currentThread().getContextClassLoader().loadClass(mobjConfiguration.getParameter("document_class"));
        } catch (ClassNotFoundException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        objXStream.alias(mobjConfiguration.getParameter("document_element_name"), objClass);
        objXStream.processAnnotations(objClass);
        try (OutputStream objOutputStream = Files.newOutputStream(objDocumentPath)) {
            objXStream.toXML(objDocument, objOutputStream);
        }
        return objDocumentPath.toString();
    }

    /**
     * Retrieve the currently open {@link IACache} for {@link IARetentionClass} objRetentionClass, or a new {@link IACache} if the current cache is non-existent or closed
     *
     * @param objRetentionClass The {@link IARetentionClass} to be associated with the {@link IACache}
     * @return
     */
    protected IACache getCache(IARetentionClass objRetentionClass) throws IOException {
        debug(this, "Getting Cache for IARetentionClass " + objRetentionClass.getName());
        for (IACache objCache : mcCaches) {
            if (objCache.getRetentionClass().equals(objRetentionClass) && !objCache.isClosed()) {
                debug(this, "Returning Cache " + objCache.getId());
                return objCache;
            }
        }

        //No Open Cache found, create a new cache for this retention class
        IACache objCreate = new IACache(miNextId++, objRetentionClass);
        Path objCachePath = Paths.get(String.format("%s/%d", mobjBasePath.toString(), objCreate.getId()));
        Files.createDirectories(objCachePath);
        mcCaches.add(objCreate);
        debug(this, "Returning Cache " + objCreate.getId());

        return objCreate;
    }

    /**
     * Updates the {@link CacheManager}, Checks whether any {@link IACache}s reached their time or content thresholds, and closes these caches if they exceed these thresholds
     */
    @Override
    public void update() {
        debug(this, "Updating Caches");
        mcCaches.stream().filter(objCache -> !objCache.isClosed()).forEach(objCache -> {
            if (objCache.getSize() >= mobjConfiguration.getCacheMessageThreshold()) {
                info(this, String.format("Closing cache %s-%d, Reason: Message Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheMessageThreshold()));
                objCache.close();
            } else if (objCache.getCreated() <= (System.currentTimeMillis() - (mobjConfiguration.getCacheTimeThreshold() * 1000))) {
                info(this, String.format("Closing cache %s-%d, Reason: Time Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheTimeThreshold()));
                objCache.close();
            }
        });
        debug(this, "Caches Updated");
    }

    /**
     * Get a {@link List<IACache>} of closed caches
     *
     * @return A (Unmodifiable) {@link List<IACache>} of closed caches
     */
    @Override
    public List<IACache> getClosedCaches() {
        debug(this, "Fetching Closed Caches");
        List<IACache> objClosed = mcCaches.stream().filter(IACache::isClosed).collect(Collectors.toList());
        debug(this, "Found " + objClosed.size() + " Closed Caches");
        return Collections.unmodifiableList(objClosed);
    }

    /**
     * Deletes {@link IACache} objCache from the list of caches, as well as from the filesystem
     *
     * @param objCache The cache that is to be removed
     */
    @Override
    public boolean cleanupCache(IACache objCache) {
        debug(this, "Cleaning Cache " + objCache.getId());
        boolean bReturn = true;

        if (objCache.isClosed()) {
            for (IADocumentReference objReference : objCache.getContents()) {
                try {
                    Files.delete(Paths.get(objReference.getFile()));
                } catch (IOException ex) {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
                    bReturn = false;
                }
            }

            try {
                Files.deleteIfExists(Paths.get(String.format("%s/%d", mobjBasePath.toString(), objCache.getId()).replace('/', File.separatorChar)));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
                bReturn = false;
            }
            try {
                Files.deleteIfExists(Paths.get(String.format("%s/IACache-%d.xml", mobjSavePath.toString(), objCache.getId()).replace('/', File.separatorChar)));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
                bReturn = false;
            }
            try {
                Files.deleteIfExists(Paths.get(String.format("%s", objCache.getSipFile().toString())));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
                bReturn = false;
            }
            mcCaches.remove(objCache);
        }
        info(this, "Cache " + objCache.getId() + " Has Been Deleted");

        return bReturn;
    }

    /**
     * Saves the caches currently held by the cache manager as XML files
     * The output will be saved in the ${caching}/${base_path}
     */
    @Override
    public boolean saveCaches() {
        boolean bReturn = true;
        for (IACache objCache : mcCaches) {
            if (!saveCache(objCache)) {
                bReturn = false;
            }
        }
        return bReturn;
    }

    public boolean saveCache(IACache objCache) {
        boolean bReturn = false;
        Path objSave = Paths.get(mobjSavePath.toString() + File.separatorChar + "IACache-" + objCache.getId() + ".xml");
        try (OutputStream objSaveStream = Files.newOutputStream(objSave)) {
            XStream objXStream = new XStream(new StaxDriver());
            objXStream.alias("IACache", IACache.class);
            objXStream.processAnnotations(IACache.class);
            objXStream.toXML(objCache, objSaveStream);
            bReturn = true;
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return bReturn;
    }


    /**
     * Loads any {@link IACache}s in the current savePath(config.xml->${cachemanager}/${base_path}) and adds them to the {@link CacheManager}
     */
    @Override
    public boolean loadCaches() {
        List<File> cSaveContents = Arrays.asList(new File(mobjSavePath.toString() + File.separatorChar).listFiles());
        cSaveContents.forEach(objCacheSaveFile -> {
            try (InputStream objCacheSaveInput = Files.newInputStream(objCacheSaveFile.toPath())) {
                XStream objXStream = new XStream(new StaxDriver());
                objXStream.alias("IACache", IACache.class);
                objXStream.processAnnotations(IACache.class);
                mcCaches.add((IACache) objXStream.fromXML(objCacheSaveInput));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
            if (!objCacheSaveFile.delete()) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, new IOException("Unable to delete cache save file"));
            }
        });
        return true;
    }

    /**
     * Save the contents of the cache under a error folder to secure the contents for later review.
     *
     * @param objCache The cache in which the error occurred
     * @return
     */
    @Override
    public boolean createErrorCache(IACache objCache) {
        boolean bReturn = true;
        String sSavePath = String.format("%s/%d/", mobjErrorPath.toString(), mobjErrorPath.toFile().listFiles().length);
        try {
            if (!Files.exists(Paths.get(sSavePath))) {
                Files.createDirectories(Paths.get(sSavePath));
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        try {
            try (OutputStream objFileStream = Files.newOutputStream(Paths.get(sSavePath + "Cache.xml"))) {
                XStream objXStream = new XStream(new StaxDriver());
                objXStream.alias("IACache", IACache.class);
                objXStream.processAnnotations(IACache.class);
                objXStream.toXML(objCache, objFileStream);
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            bReturn = false;
        }

        if (objCache.getSipFile() != null) {
            try {
                Files.copy(objCache.getSipFile(), Paths.get(String.format("%s/%s", sSavePath.toString(), objCache.getSipFile().getFileName().toString())));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }

        for (IADocumentReference objDocument : objCache.getContents()) {
            try (OutputStream objFileStream = Files.newOutputStream(Paths.get(sSavePath + objDocument.getDocumentId() + ".xml"))) {
                XStream objXStream = new XStream(new StaxDriver());
                Class objClass = Thread.currentThread().getContextClassLoader().loadClass(mobjConfiguration.getParameter("document_class"));
                objXStream.alias(mobjConfiguration.getParameter("document_element_name"), objClass);
                objXStream.processAnnotations(objClass);
                objXStream.toXML(objDocument.getDocumentData(objClass, mobjConfiguration.getParameter("document_element_name")), objFileStream);
            } catch (IOException | ClassNotFoundException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                bReturn = false;
            }
        }
        return bReturn;
    }
}