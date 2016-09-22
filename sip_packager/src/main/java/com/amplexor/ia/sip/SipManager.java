package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;

import java.nio.file.Path;

/**
 * Created by admjzimmermann on 22-9-2016.
 */
public interface SipManager {
    Path getSIPFile(IACache objCache);
    Path getSIPFile(IADocument objDocument, IARetentionClass objRetentionClass);
}
