package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.xml.XmlBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amplexor.ia.Logger.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class AMPSipManager implements SipManager {

    private IASipConfiguration mobjConfiguration;

    //Hide implicit public constructor
    public AMPSipManager(IASipConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public Path getSIPFile(IACache objCache) {
        debug(this, "Creating SIP file for cache with ID: " + objCache.getId());
        Path objReturn = null;
        try {
            SipAssembler<IADocument> objSipAssembler = createSipAssembler(getPackageInformation(objCache.getRetentionClass()), getPdiAssembler(), getDigitalObjects());
            FileGenerator<IADocument> objFileGenerator = new FileGenerator<>(objSipAssembler);
            FileGenerationMetrics objMetrics = objFileGenerator.generate(objCache.getContents().iterator());
            if (objMetrics.getFile() != null) {
                Path objTempPath = objMetrics.getFile().toPath();
                objReturn = Files.copy(objTempPath, Paths.get(objTempPath.toString() + ".zip"));
                info(this, "SIP File created: " + objReturn.toString() + " For Cache with ID " + objCache.getId());
                Files.delete(objTempPath);
                debug(this, "Deleted temp file: " + objTempPath);
            } else {
                error(this, "Error generating SIP");
            }
        } catch (IOException ex) {
            error(this, ex);
        }
        return objReturn;
    }

    @Override
    public Path getSIPFile(IADocument objDocument, IARetentionClass objRetentionClass) {
        debug(this, "Creating SIP file for Document with ID: " + objDocument.getDocumentId());
        IACache objTempCache = new IACache(-1, objRetentionClass);
        objTempCache.add(objDocument);
        return getSIPFile(objTempCache);
    }

    private SipAssembler createSipAssembler(PackagingInformation objPackagingInformation, PdiAssembler objPdiAssembler, DigitalObjectsExtraction objDigitalObjectsExtraction) {
        return SipAssembler.forPdiAndContent(objPackagingInformation, objPdiAssembler, objDigitalObjectsExtraction);
    }

    private PackagingInformation getPackageInformation(IARetentionClass objRetentionClass) {
        return PackagingInformation.builder().dss()
                .holding(mobjConfiguration.getHoldingName())
                .application(mobjConfiguration.getApplicationName())
                .producer(mobjConfiguration.getProducerName())
                .entity(mobjConfiguration.getEntityName())
                .schema(mobjConfiguration.getSchemaDeclaration())
                .retentionClass(objRetentionClass.getName().replace(' ', '_'))
                .end().build();
    }

    private PdiAssembler getPdiAssembler() {
        return new XmlPdiAssembler<IADocument>(URI.create(mobjConfiguration.getSchemaDeclaration()), mobjConfiguration.getDocumentElementName(), mobjConfiguration.getEntityName()) {
            @Override
            protected void doAdd(IADocument objDocument, Map<String, ContentInfo> cMap) {
                XmlBuilder builder = getBuilder();
                for (String sKey : objDocument.getMetadataKeys()) {
                    builder.element(sKey, objDocument.getMetadata(sKey));
                }
                for (String sKey : objDocument.getContentKeys()) {
                    builder.element(sKey, String.format("%s_%s.pdf", sKey, objDocument.getDocumentId()));
                }
            }
        };
    }

    private DigitalObjectsExtraction<IADocument> getDigitalObjects() {
        return objDocument -> {
            List<DigitalObject> cObjects = new ArrayList<>();
            for (String sKey : objDocument.getContentKeys()) {
                DigitalObject objObject = DigitalObject.fromBytes(String.format("%s_%s.pdf", sKey, objDocument.getDocumentId()), objDocument.loadContent(sKey));
                cObjects.add(objObject);
            }
            return cObjects.iterator();
        };
    }
}
