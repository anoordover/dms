package com.amplexor.ia.cache;

import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by admjzimmermann on 7-9-2016.
 */
public class IACacheGroup {
    private String basePath;
    private IARetentionClass retentionClass;
    private List<IACache> caches;
    private IACache currentCache;

    private int nextId = 0;

    public IACacheGroup(String basePath, IARetentionClass retentionClass) {
        this.basePath = basePath;
        this.retentionClass = retentionClass;
    }

    public void add(IADocument document) {
        if (currentCache == null) {
            createNewCache();
        }

        if (!currentCache.isClosed()) {
            currentCache.add(document);
        } else {
            createNewCache();
            currentCache.add(document);
        }
    }

    private void createNewCache() {
        try {
            currentCache = new IACache(nextId++);
            Files.createDirectory(new File(basePath + "/" + currentCache.getId()).toPath());
            caches.add(currentCache);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public IARetentionClass getRetentionClass() {
        return retentionClass;
    }

    public IACache getCurrentCache() {
        return currentCache;
    }
}
