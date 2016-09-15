package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CacheManager {
    private static Logger logger = Logger.getLogger("CacheManager");

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

    public void initializeCache() {
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
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void add(IADocument objDocument, IARetentionClass objRetentionClass) {
        saveDocument(objDocument);
        update();
        if (checkGroupPath(objRetentionClass, true)) {
            IACache cache = getCache(objRetentionClass);
            if (cache != null) {
                cache.add(objDocument);
            }
        }
    }

    private IACache getCache(IARetentionClass objRetentionClass) {
        for (IACache objCache : mcCaches) {
            if (objCache.getRetentionClass().equals(objRetentionClass) && !objCache.isClosed()) {
                return objCache;
            }
        }

        IACache objCreate = new IACache(miNextId++, objRetentionClass);
        try {
            Path oCachePath = Paths.get(String.format("%s/%s/%d", mobjBasePath.toString(), objRetentionClass.getName(), objCreate.getId()).replace('/', File.separatorChar));
            Files.createDirectory(oCachePath);
            mcCaches.add(objCreate);
        } catch (IOException ex) {
            miNextId--;
            objCreate = null;
            logger.error(ex);
        }
        return objCreate;
    }

    private void saveDocument(IADocument objDocument) {
        try {
            Path objDocumentSave = Paths.get(String.format("%s/%s", mobjSavePath.toString(), objDocument.getDocumentId()).replace('/', File.separatorChar));
            XStream objXStream = new XStream(new StaxDriver());
            objXStream.processAnnotations(objDocument.getClass());
            OutputStream objOutputStream = Files.newOutputStream(objDocumentSave);
            objXStream.toXML(objDocument, objOutputStream);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private boolean checkGroupPath(IARetentionClass objRetentionClass, boolean bCreate) {
        boolean bReturn;
        try {
            Path groupPath = Paths.get(String.format("%s/%s", mobjBasePath.toString(), objRetentionClass.getName()));
            bReturn = Files.exists(groupPath);
            if (!bReturn && bCreate) {
                Files.createDirectories(groupPath);
                bReturn = Files.exists(groupPath);
            }
        } catch (IOException ex) {
            bReturn = false;
            logger.error(ex);
        }
        return bReturn;
    }

    public void update() {
        for (IACache objCache : mcCaches) {
            if (!objCache.isClosed()) {
                if (objCache.getSize() >= mobjConfiguration.getCacheMessageThreshold()) {
                    logger.info(String.format("Closing cache %s-%d, Reason: Message Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheMessageThreshold()));
                    objCache.close();
                } else if (objCache.getCreated() <= (System.currentTimeMillis() - (mobjConfiguration.getCacheTimeThreshold() * 1000))) {
                    logger.info(String.format("Closing cache %s-%d, Reason: Time Threshold Reached(%d)%n", objCache.getRetentionClass().getName(), objCache.getId(), mobjConfiguration.getCacheTimeThreshold()));
                    objCache.close();
                }
            }
        }
    }

    public List<IACache> getClosedCaches() {
        List<IACache> objClosed = new ArrayList<>();
        for (IACache objCache : mcCaches) {
            if (objCache.isClosed()) {
                objClosed.add(objCache);
            }
        }
        return Collections.unmodifiableList(objClosed);
    }

    public void cleanupCache(IACache objCache) {
        try {
            if (objCache.isClosed()) {
                Files.deleteIfExists(Paths.get(String.format("%s/%s/%d", mobjBasePath.toString(), objCache.getRetentionClass().getName(), objCache.getId())));
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
