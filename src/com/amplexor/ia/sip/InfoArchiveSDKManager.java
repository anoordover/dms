package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.metadata.IADocument;
import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.io.EncodedHash;
import com.emc.ia.sdk.support.xml.XmlBuilder;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class InfoArchiveSDKManager {
    public static Path getSIPFile(IACache cache) {
        SipAssembler<IADocument> sipAssembler = createSipAssembler(getPackageInformation(), getPdiAssembler(), getDigitalObjects());
        //TODO: create SIP Package, return path on the filesystem
        return Paths.get("");
    }

    private static SipAssembler createSipAssembler(PackagingInformation packagingInformation, PdiAssembler pdiAssembler, DigitalObjectsExtraction digitalObjectsExtraction) {
        //return new SipAssembler(packagingInformation, pdiAssembler, digitalObjectsExtraction);
    }

    private static PackagingInformation getPackageInformation() {
        return PackagingInformation.builder().dss()
                .holding("CAK")
                .application("DMS")
                .producer("SIP_PKGR")
                .entity("DMS_DEV")
                .schema("urn:cak:dms:document:1.0")
                .end().build();
    }

    private static PdiAssembler getPdiAssembler() {
        PdiAssembler<IADocument> pdiAssembler = new XmlPdiAssembler<IADocument>(URI.create("urn:cak:dms:document:1.0"), "DMS_DEV") {
            @Override
            protected void doAdd(IADocument document, Map<String, ContentInfo> map) {
                XmlBuilder builder = getBuilder();
                for (String key : document.getMetadataKeys()) {
                    builder.element(key, document.getMetadata(key));
                }
            }
        };
        return pdiAssembler;
    }

    private static DigitalObjectsExtraction getDigitalObjects() {
        DigitalObjectsExtraction<IADocument> content = new DigitalObjectsExtraction<IADocument>() {
            @Override
            public Iterator<? extends DigitalObject> apply(IADocument document) {
                List<DigitalObject> objects = new ArrayList<DigitalObject>();
                for (String key : document.getContentKeys()) {
                    DigitalObject object = DigitalObject.fromBytes(key, document.loadContent(key));
                    objects.add(object);
                }
                return objects.iterator();
            }
        };
        return content;
    }
}
