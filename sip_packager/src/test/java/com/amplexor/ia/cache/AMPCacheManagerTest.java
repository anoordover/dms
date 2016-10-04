package com.amplexor.ia.cache;

import com.amplexor.ia.configuration.CacheConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by minkenbergs on 4-10-2016.
 */
public class AMPCacheManagerTest {

    @Test
    public void initializeCache() throws Exception {
        CacheConfiguration cc = mock(CacheConfiguration.class);
        when(cc.getCacheBasePath()).thenReturn("target/Cache");
        AMPCacheManager acm = new AMPCacheManager(cc);
        acm.initializeCache();
        Path p = Paths.get("target/Cache/main/save");
        assertTrue(Files.exists(p));
    }

    @Test
    public void add() throws Exception {

    }

    @Test
    public void getCache() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void getClosedCaches() throws Exception {

    }

    @Test
    public void cleanupCache() throws Exception {

    }

    @Test
    public void saveCaches() throws Exception {

    }

    @Test
    public void loadCaches() throws Exception {

    }

    @AfterClass
    public static void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File("target"+File.separator+"Cache"));
    }


}