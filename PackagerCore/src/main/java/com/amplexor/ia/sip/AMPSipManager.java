package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.xml.XmlBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class AMPSipManager implements SipManager {

    protected IASipConfiguration mobjConfiguration;

    public AMPSipManager(IASipConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public boolean getSIPFile(IACache objCache) {
        if (objCache.getId() > -1) { //Sip is a single document
            debug(this, "Creating SIP file for cache with ID: " + objCache.getId());
        }

        boolean bReturn = false;
        debug(this, "Retrieving document data");
        List<IADocument> cDocument = retrieveDocuments(objCache);
        info(this, "Found " + cDocument.size() + " Documents in IACache-" + objCache.getId());
        try {
            if (!cDocument.isEmpty()) {
                SipAssembler<IADocument> objSipAssembler = createSipAssembler(getPackageInformation(objCache.getRetentionClass()), getPdiAssembler(), getDigitalObjects());
                FileGenerator<IADocument> objFileGenerator = new FileGenerator<>(objSipAssembler, new File(mobjConfiguration.getSipOutputDirectory()));
                FileGenerationMetrics objMetrics = objFileGenerator.generate(cDocument.iterator());
                if (objMetrics.getFile() != null) {
                    Path objTempPath = objMetrics.getFile().toPath();
                    Path objSipFile = Files.copy(objTempPath, Paths.get(objTempPath.toString() + ".zip"));
                    info(this, "SIP File created: " + objSipFile.toString() + " For Cache with ID " + objCache.getId());
                    Files.delete(objTempPath);
                    debug(this, "Deleted temp file: " + objTempPath);
                    objCache.setSipFile(objSipFile.toString());
                    backupSipFile(objSipFile, objTempPath);
                    bReturn = true;
                } else {
                    ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, objCache, new Exception("Error generating SIP file"));
                }
            } else {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, objCache, new Exception("Cache did not contain any data"));
            }
        } catch (IOException ex) {
            long lTotalSize = 0;
            for (IADocument objDocument : cDocument) {
                lTotalSize += objDocument.getSizeEstimate();
            }

            if (new File(mobjConfiguration.getSipOutputDirectory()).getFreeSpace() < lTotalSize) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SIP_INSUFFICIENT_DISK_SPACE, ex);
            }
        }
        return bReturn;
    }

    protected void backupSipFile(Path objSipFile, Path objTempPath) throws IOException {
        if (mobjConfiguration.getSipBackupDirectory() != null) {
            Files.copy(objSipFile, Paths.get(mobjConfiguration.getSipBackupDirectory() + "/" + objTempPath.toString() + ".zip"));
        }
    }

    protected synchronized List<IADocument> retrieveDocuments(IACache objCache) {
        List<IADocument> cDocuments = new ArrayList<>();
        Class<?> objClass = IADocument.class;
        try {
            objClass = Thread.currentThread().getContextClassLoader().loadClass(mobjConfiguration.getParameter("document_class"));
        } catch (ClassNotFoundException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        for (IADocumentReference objReference : objCache.getContents()) {
            cDocuments.add(objReference.getDocumentData(objClass, mobjConfiguration.getParameter("document_element_name")));
        }
        return cDocuments;
    }

    @Override
    public IACache getSIPFile(IADocumentReference objDocumentReference, IARetentionClass objRetentionClass) {
        debug(this, "Creating SIP file for Document with ID: " + objDocumentReference.getDocumentId());
        IACache objTempCache = new IACache(-1, objRetentionClass);
        objTempCache.add(objDocumentReference);
        if (getSIPFile(objTempCache)) {
            return objTempCache;
        }
        return null;
    }

    protected SipAssembler createSipAssembler(PackagingInformation objPackagingInformation, PdiAssembler objPdiAssembler, DigitalObjectsExtraction objDigitalObjectsExtraction) {
        return SipAssembler.forPdiAndContent(objPackagingInformation, objPdiAssembler, objDigitalObjectsExtraction);
    }

    protected PackagingInformation getPackageInformation(IARetentionClass objRetentionClass) {
        return PackagingInformation.builder().dss()
                .holding(mobjConfiguration.getHoldingName())
                .application(mobjConfiguration.getApplicationName())
                .producer(mobjConfiguration.getProducerName())
                .entity(mobjConfiguration.getEntityName())
                .schema(mobjConfiguration.getSchemaDeclaration())
                .retentionClass(objRetentionClass.getName().replace(' ', '_'))
                .end().build();
    }

    protected PdiAssembler getPdiAssembler() {
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

    protected DigitalObjectsExtraction<IADocument> getDigitalObjects() {
        return objDocument -> {
            List<DigitalObject> cObjects = new ArrayList<>();
            for (String sKey : objDocument.getContentKeys()) {
                objDocument.loadContent(sKey);
                DigitalObject objObject = DigitalObject.fromBytes(String.format("%s_%s.pdf", sKey, objDocument.getDocumentId()), objDocument.getContent(sKey));
                objDocument.setContent(sKey, new byte[0]);
                cObjects.add(objObject);
            }
            return cObjects.iterator();
        };
    }
}
