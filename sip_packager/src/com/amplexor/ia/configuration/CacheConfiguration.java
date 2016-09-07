package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.Collections;
import java.util.List;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class CacheConfiguration {
    @XStreamAlias("base_path")
    private String cacheBasePath;

    @XStreamAlias("message_threshold")
    private int cacheMessageThreshold;

    @XStreamAlias("time_threshold")
    private int cacheTimeThreshold;

    public String getCacheBasePath() {
        return cacheBasePath;
    }

    public int getCacheMessageThreshold() {
        return cacheMessageThreshold;
    }

    public int getCacheTimeThreshold() {
        return cacheTimeThreshold;
    }
}
