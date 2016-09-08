package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

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
    private CacheConfiguration configuration;
    private List<IARetentionClass> retentionClasses;
    private List<IACache> caches;

    private int nextId;
    private Path base;
    private Path save;

    public CacheManager(CacheConfiguration configuration) {
        this.configuration = configuration;
        this.retentionClasses = new ArrayList<>();
        this.caches = new ArrayList<>();
        this.nextId = 0;
    }

    public void initializeCache(List<IARetentionClass> retentionClasses) {
        try {
            base = Paths.get(
                    String.format("%s/%s/", configuration.getCacheBasePath(), Thread.currentThread().getName()).replace('/', File.separatorChar));
            save = Paths.get(
                    String.format("%s/save", base.toString()).replace('/', File.separatorChar));

            if (!Files.exists(base)) {
                Files.createDirectories(base);
            }

            if (!Files.exists(save)) {
                Files.createDirectories(save);
            }
        } catch (IOException ex) {

        }
    }

    public void add(IADocument document, IARetentionClass retentionClass) {
        saveDocument(document);
        update();
        if (checkGroupPath(retentionClass, true)) {
            IACache cache = getCache(retentionClass);
            if (cache != null) {
                cache.add(document);
                document.extract(String.format("%s/%s/%d", base.toString(), retentionClass.getName(), cache.getId()));
            }
        }
    }

    private IACache getCache(IARetentionClass retentionClass) {
        for (IACache cache : caches) {
            if (cache.getRetentionClass().equals(retentionClass)) {
                if (!cache.isClosed()) {
                    return cache;
                }
            }
        }

        IACache create = new IACache(nextId++, retentionClass);
        try {
            Path cachePath = Paths.get(String.format("%s/%s/%d", base.toString(), retentionClass.getName(), create.getId()).replace('/', File.separatorChar));
            Files.createDirectory(cachePath);
            caches.add(create);
        } catch (IOException ex) {
            nextId--;
            create = null;
        }
        return create;
    }

    private void saveDocument(IADocument document) {
        try {
            Path documentSave = Paths.get(String.format("%s/%s", save.toString(), document.getId()).replace('/', File.separatorChar));
            XStream xstream = new XStream(new StaxDriver());
            xstream.processAnnotations(document.getClass());
            OutputStream outputStream = Files.newOutputStream(documentSave);
            xstream.toXML(document, outputStream);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private boolean checkGroupPath(IARetentionClass retentionClass, boolean create) {
        boolean rval = false;
        try {
            Path groupPath = Paths.get(String.format("%s/%s", base.toString(), retentionClass.getName()));
            rval = Files.exists(groupPath);
            if (!rval && create) {
                Files.createDirectories(groupPath);
                rval = Files.exists(groupPath);
            }
        } catch (IOException ex) {
            rval = false;
            System.err.println(ex.getLocalizedMessage());
        }
        return rval;
    }

    public void update() {
        for (IACache cache : caches) {
            if(!cache.isClosed()) {
                if (cache.getSize() >= configuration.getCacheMessageThreshold()) {
                    System.out.printf("Closing cache %s-%d, Reason: Message Threshold Reached(%d)\n", cache.getRetentionClass().getName(), cache.getId(), configuration.getCacheMessageThreshold());
                    cache.close();
                } else if (cache.getCreated() <= (System.currentTimeMillis() - (configuration.getCacheTimeThreshold() * 1000))) {
                    System.out.printf("Closing cache %s-%d, Reason: Time Threshold Reached(%d)\n", cache.getRetentionClass().getName(), cache.getId(), configuration.getCacheTimeThreshold());
                    cache.close();
                }
            }
        }
    }

    public List<IACache> getClosedCaches() {
        List<IACache> closed = new ArrayList<>();
        for(IACache cache : caches) {
            if(cache.isClosed()) {
                closed.add(cache);
            }
        }
        return Collections.unmodifiableList(closed);
    }

    public void cleanupCache(IACache cache) {
        try {
            if (cache.isClosed()) {
                Files.deleteIfExists(Paths.get(String.format("%s/%s/%d", base.toString(), cache.getRetentionClass().getName(), cache.getId())));
            }
        }
        catch(IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
