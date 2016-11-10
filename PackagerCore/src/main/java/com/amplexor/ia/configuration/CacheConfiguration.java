package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * POJO for holding configuration pertaining to the {@link com.amplexor.ia.cache.CacheManager}
 * Created by admjzimmermann on 6-9-2016.
 */
public class CacheConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("base_path")
    private String msCacheBasePath;

    @XStreamAlias("message_threshold")
    private int miCacheMessageThreshold;

    @XStreamAlias("time_threshold")
    private int miCacheTimeThreshold;

    public String getCacheBasePath() {
        return msCacheBasePath;
    }

    public void setCacheBasePath(String sCacheBasePath) {
        msCacheBasePath = sCacheBasePath;
    }

    public int getCacheMessageThreshold() {
        return miCacheMessageThreshold;
    }

    public void setCacheMessageThreshold(int iCacheMessageThreshold) {
        miCacheMessageThreshold = iCacheMessageThreshold;
    }

    public int getCacheTimeThreshold() {
        return miCacheTimeThreshold;
    }

    public void setCacheTimeThreshold(int iCacheTimeThreshold) {
        miCacheTimeThreshold = iCacheTimeThreshold;
    }
}

