package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import java.io.IOException;
import java.util.List;

/**
 * Created by zimmermannj on 9/30/2016.
 */
public interface CacheManager {
    /**
     * Initializes the {@link CacheManager} based on the {@link CacheConfiguration}.
     * The following actions will be executed:
     * - Create the cache base folder if it does not exist
     * - Create the cache save folder if it does not exist
     * - Load any saved caches in the cache save folder and delete the files
     *
     * @throws IOException
     */
    boolean initializeCache() throws IOException;

    /**
     * Adds {@link IADocument} objDocument to the cache for the {@link IARetentionClass} objRetentionClass
     * Note: the update() method will be called before adding the document to ensure the current {@link IACache} does not exceed it's message threshold
     *
     * @param objDocument
     * @param objRetentionClass
     */
    boolean add(IADocument objDocument, IARetentionClass objRetentionClass);

    /**
     * Updates the {@link CacheManager}, Checks whether any {@link IACache}s reached their time or content thresholds, and closes these caches if they exceed these thresholds
     */
    void update();

    /**
     * Get a {@link List<IACache>} of closed caches
     *
     * @return A (Unmodifiable) {@link List<IACache>} of closed caches
     */
    List<IACache> getClosedCaches();

    /**
     * Get the number of caches that are currently closed
     */
    int getClosedCacheCount();

    /**
     * Deletes {@link IACache} objCache from the list of caches, as well as from the filesystem
     *
     * @param objCache The cache that is to be removed
     */
    boolean cleanupCache(IACache objCache);

    /**
     * Loads any {@link IACache}s in the current savePath(config.xml->${cachemanager}/${base_path}) and adds them to the {@link CacheManager}
     */
    boolean loadCaches();

    /**
     * Saves the cache as an "Error" cache
     */
    boolean createErrorCache(IACache objCache);

    /**
     * Saves the cache to the filesystem
     */
    boolean saveCache(IACache objCache) throws IOException;
}
