package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

/**
 * The {@link CacheManager} is responsible for keeping track of {@link IACache} objects, the folder structure associated with the caches and saving any data contained in these caches while they exist.
 * Created by admjzimmermann on 6-9-2016.
 */
public class AMPCacheManager implements CacheManager {
    private static final String IACACHE_ELEMENT_ALIAS = "IACache";
    private static final String PROPERTY_DOCUMENT_ELEMENT_NAME = "document_element_name";
    private static final String PROPERTY_DOCUMENT_CLASS = "document_class";

    protected CacheConfiguration mobjConfiguration;
    protected List<IACache> mcCaches;

    protected int miNextId;
    protected Path mobjBasePath;
    protected Path mobjSavePath;
    protected Path mobjErrorPath;
    protected Path mobjInvalidDocumentsPath;

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
            mobjInvalidDocumentsPath = Paths.get(
                    String.format("%s/invalid", mobjConfiguration.getCacheBasePath()).replace('/', File.separatorChar));

            if (!Files.exists(mobjBasePath)) {
                Files.createDirectories(mobjBasePath);
            }

            if (!Files.exists(mobjErrorPath)) {
                Files.createDirectories(mobjErrorPath);
            }

            if (!Files.exists(mobjInvalidDocumentsPath)) {
                Files.createDirectories(mobjInvalidDocumentsPath);
            }

            if (!Files.exists(mobjSavePath)) {
                Files.createDirectories(mobjSavePath);
            } else {
                loadCaches();
                update();
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
        Path objDocumentPath = Paths.get(String.format("%s/%d/%s", mobjBasePath.toString(), objCache.getId(), objDocument.getDocumentId()));
        XStream objXStream = new XStream(new DomDriver());
        Class<?> objClass = getDocumentClass();
        objXStream.alias(mobjConfiguration.getParameter(PROPERTY_DOCUMENT_ELEMENT_NAME), objClass);
        objXStream.processAnnotations(objClass);
        String sXmlData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + objXStream.toXML(objDocument);
        try (FileOutputStream objFileOut = new FileOutputStream(objDocumentPath.toFile())) {
            objFileOut.write(sXmlData.getBytes(Charset.forName("UTF-8")));
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
        saveCache(objCreate);
        debug(this, "Returning Cache " + objCreate.getId());

        return objCreate;
    }

    /**
     * Updates the {@link CacheManager}, Checks whether any {@link IACache}s reached their time or content thresholds, and closes these caches if they exceed these thresholds
     */
    @Override
    public void update() {
        debug(this, "Updating Caches");
        for (Iterator<IACache> objIter = mcCaches.iterator(); objIter.hasNext(); ) {
            IACache objCache = objIter.next();
            if (objCache != null && !objCache.isClosed() && objCache.getSize() >= mobjConfiguration.getCacheMessageThreshold()) {
                info(this, String.format("Closing cache %s-%d, Reason: Message Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheMessageThreshold()));
                objCache.close();
            } else if (objCache != null && !objCache.isClosed() && objCache.getCreated() <= (System.currentTimeMillis() - mobjConfiguration.getCacheTimeThreshold() * 1000)) {
                info(this, String.format("Closing cache %s-%d, Reason: Time Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheTimeThreshold()));
                objCache.close();
            }

            if (objCache.isClosed()) {
                try {
                    saveCache(objCache);
                } catch (IOException ex) {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                }
            }
        }
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
        List<IACache> cClosed = new ArrayList<>();
        for (IACache objCache : mcCaches) {
            if (objCache.isClosed()) {
                cClosed.add(objCache);
            }
        }
        mcCaches.removeAll(cClosed);
        debug(this, "Found " + cClosed.size() + " Closed Caches");
        return Collections.unmodifiableList(cClosed);
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
            try { //Delete Cache folder contents and folder
                Path objCachePath = Paths.get(String.format("%s/%d", mobjBasePath.toString(), objCache.getId()).replace('/', File.separatorChar));
                File[] cFilesInCache;
                if ((cFilesInCache = objCachePath.toFile().listFiles()) != null) {
                    cleanupCacheFiles(cFilesInCache);
                }
                Files.deleteIfExists(objCachePath);
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
                bReturn = false;
            }

            try { //Delete Cache descriptor XML and SIP file
                if (objCache.getSipFile() != null) {
                    Files.deleteIfExists(objCache.getSipFile());
                }
                Files.deleteIfExists(Paths.get(String.format("%s/IACache-%d.xml", mobjSavePath.toString(), objCache.getId()).replace('/', File.separatorChar)));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_CACHE_DELETION_FAILURE, ex);
                bReturn = false;
            }
        }
        info(this, "Cache " + objCache.getId() + " Has Been Deleted");

        return bReturn;
    }

    private void cleanupCacheFiles(File[] cFilesInCache) throws IOException {
        List<File> cFilesToClean = Arrays.asList(cFilesInCache);
        for (File objFile : cFilesToClean) {
            Files.deleteIfExists(objFile.toPath());
        }
    }

    public boolean saveCache(IACache objCache) throws IOException {
        info(this, "Saving IACache-" + objCache.getId());

        boolean bReturn = false;
        Path objSave = Paths.get(mobjSavePath.toString() + File.separatorChar + "IACache-" + objCache.getId() + ".xml");
        XStream objXStream = new XStream(new DomDriver());
        objXStream.alias(IACACHE_ELEMENT_ALIAS, IACache.class);
        objXStream.processAnnotations(IACache.class);
        String sCacheData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + objXStream.toXML(objCache);
        try (FileOutputStream objSaveStream = new FileOutputStream(objSave.toFile())) {
            objSaveStream.write(sCacheData.getBytes(Charset.forName("UTF-8")));
            bReturn = true;
        } catch (StreamException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            bReturn = false;
        }
        return bReturn;
    }

    @Override
    public boolean saveInvalidDocument(String sData, String sFilename) {
        boolean bReturn = false;
        try {
            Files.write(Paths.get(String.format("%s/%s.xml", mobjInvalidDocumentsPath.toString(), sFilename)), sData.getBytes());
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
        info(this, "Loading Saved caches");
        List<File> cSaveContents = Arrays.asList(new File(mobjSavePath.toString() + File.separatorChar).listFiles());
        cSaveContents.forEach(objCacheSaveFile -> {
            info(this, "Loading IACache from file " + objCacheSaveFile.getAbsolutePath());
            try (InputStream objCacheLoadInput = new FileInputStream(objCacheSaveFile)) {
                loadCache(objCacheLoadInput);
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        });

        for (IACache objCache : mcCaches) {
            if (objCache.getId() >= miNextId) {
                miNextId = objCache.getId() + 1;
            }
        }

        return true;
    }

    private void loadCache(InputStream objInput) throws IOException {
        //Load Cache Definition
        IACache objCache = null;
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias(IACACHE_ELEMENT_ALIAS, IACache.class);
        objXStream.processAnnotations(IACache.class);
        Object objCacheObj = objXStream.fromXML(objInput);
        if (objCacheObj != null && objCacheObj instanceof IACache) {
            objCache = (IACache) objCacheObj;
        }

        if (objCache != null) {
            //Load the documents belonging to the cache
            Path objCachePath = Paths.get(String.format("%s/%d/", mobjBasePath.toString(), objCache.getId()));
            File objCachePathFile = objCachePath.toFile();
            if (objCachePathFile != null && objCachePathFile.listFiles() != null) {
                List<File> cCacheContents = Arrays.asList(objCachePathFile.listFiles());
                for (File objDocumentFile : cCacheContents) {
                    addLoadedDocument(objCache, objDocumentFile);
                }
            }
            objCache.getContents().forEach(objReference -> objReference.getDocumentData(getDocumentClass(), mobjConfiguration.getParameter(PROPERTY_DOCUMENT_ELEMENT_NAME)));
            mcCaches.add(objCache);
        }
    }

    private void addLoadedDocument(IACache objCache, File objDocumentFile) {
        debug(this, "Loading document " + objDocumentFile.getName() + " into IACache-" + objCache.getId());
        if (!objDocumentFile.getName().contains(".")) {
            objCache.add(new IADocumentReference(objDocumentFile.getName(), objDocumentFile.getAbsolutePath()));
        }
    }

    private Class<?> getDocumentClass() {
        Class<?> objClass = IADocument.class;
        try {
            objClass = Thread.currentThread().getContextClassLoader().loadClass(mobjConfiguration.getParameter(PROPERTY_DOCUMENT_CLASS));
        } catch (ClassNotFoundException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        return objClass;
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
            try (OutputStream objFileStream = new FileOutputStream(Paths.get(sSavePath + "Cache.xml").toFile())) {
                XStream objXStream = new XStream(new StaxDriver());
                objXStream.alias(IACACHE_ELEMENT_ALIAS, IACache.class);
                objXStream.processAnnotations(IACache.class);
                objXStream.toXML(objCache, objFileStream);
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            bReturn = false;
        }

        if (objCache.getSipFile() != null) {
            try {
                Files.copy(objCache.getSipFile(), Paths.get(String.format("%s/%s", sSavePath, objCache.getSipFile().getFileName().toString())));
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }

        for (IADocumentReference objDocument : objCache.getContents()) {
            try (OutputStream objFileStream = new FileOutputStream(Paths.get(sSavePath + objDocument.getDocumentId() + ".xml").toFile())) {
                XStream objXStream = new XStream(new StaxDriver());
                Class objClass = Thread.currentThread().getContextClassLoader().loadClass(mobjConfiguration.getParameter(PROPERTY_DOCUMENT_CLASS));
                objXStream.alias(mobjConfiguration.getParameter(PROPERTY_DOCUMENT_ELEMENT_NAME), objClass);
                objXStream.processAnnotations(objClass);
                objXStream.toXML(objDocument.getDocumentData(objClass, mobjConfiguration.getParameter(PROPERTY_DOCUMENT_ELEMENT_NAME)), objFileStream);
            } catch (IOException | ClassNotFoundException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                bReturn = false;
            }
        }
        return bReturn;
    }

    public int getClosedCacheCount() {
        List<IACache> objClosed = mcCaches.stream().filter(IACache::isClosed).collect(Collectors.toList());
        return objClosed.size();
    }
}
