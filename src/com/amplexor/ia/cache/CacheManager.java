package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CacheManager {
    private CacheConfiguration configuration;
    private List<IACacheGroup> groups;

    public CacheManager(CacheConfiguration configuration) {
        this.configuration = configuration;
        groups = new ArrayList<>();
    }

    public void initializeCache(List<IARetentionClass> retentionClasses) {
        try {
            File basePath = new File(
                    String.format("%s/%s/", configuration.getCacheBasePath(), Thread.currentThread().getName()).replace('/', File.separatorChar));
            File cacheSave = new File(
                    String.format("%s/save", basePath.getAbsolutePath()).replace('/', File.separatorChar));

            if (!basePath.exists()) {
                Files.createDirectories(basePath.toPath());
            }

            if (!cacheSave.exists()) {
                Files.createDirectories(cacheSave.toPath());
            }

            for (IARetentionClass retentionClass : retentionClasses) {
                IACacheGroup group = new IACacheGroup(configuration.getCacheBasePath(), retentionClass);
                File groupDirectory = new File(String.format("%s/%s", basePath.getAbsolutePath(), group.getRetentionClass().getName()).replace('/', File.separatorChar));
                Files.createDirectories(groupDirectory.toPath());
                groups.add(group);
            }

        } catch (IOException ex) {

        }
    }

    public void add(IADocument document, IARetentionClass retentionClass) {
        update();
        for (IACacheGroup group : groups) {
            if (group.getRetentionClass().equals(retentionClass)) {
                group.add(document);
                document.extract(String.format("%s/%s/%d", configuration.getCacheBasePath(), retentionClass.getName(), group.getCurrentCache().getId()).replace('/', File.separatorChar));
            }
        }
    }

    public void update() {
        for (IACacheGroup group : groups) {
            if (group.getCurrentCache().getCreated() < (System.currentTimeMillis() - (configuration.getCacheTimeThreshold() * 1000))) {
                System.out.printf("Cache %d reached time threshold of %d seconds, current: %s)", group.getCurrentCache().getId(), configuration.getCacheTimeThreshold(), ((System.currentTimeMillis() - (configuration.getCacheTimeThreshold() * 1000)) / 1000));
                group.getCurrentCache().close();
            } else if (group.getCurrentCache().getSize() < configuration.getCacheMessageThreshold()) {
                System.out.printf("Cache %d reached size threshold of %d, current: %s", group.getCurrentCache().getId(), configuration.getCacheMessageThreshold(), group.getCurrentCache().getSize());
            }
        }
    }
}
