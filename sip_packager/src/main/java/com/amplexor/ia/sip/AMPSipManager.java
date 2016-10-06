package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.xml.XmlBuilder;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
        debug(this, "Retrieving Documents from disk");
        List<IADocument> cDocument = retrieveDocuments(objCache);
        try {
            if (!cDocument.isEmpty()) {
                SipAssembler<IADocument> objSipAssembler = createSipAssembler(getPackageInformation(cDocument.get(0), objCache.getRetentionClass()), getPdiAssembler(), getDigitalObjects());
                FileGenerator<IADocument> objFileGenerator = new FileGenerator<>(objSipAssembler, new File(mobjConfiguration.getSipOutputDirectory()));
                FileGenerationMetrics objMetrics = objFileGenerator.generate(cDocument.iterator());
                if (objMetrics.getFile() != null) {
                    Path objTempPath = objMetrics.getFile().toPath();
                    Path objSipFile = Files.copy(objTempPath, Paths.get(objTempPath.toString() + ".zip"));
                    info(this, "SIP File created: " + objSipFile.toString() + " For Cache with ID " + objCache.getId());
                    Files.delete(objTempPath);
                    debug(this, "Deleted temp file: " + objTempPath);
                    objCache.setSipFile(objSipFile.toString());
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

    protected List<IADocument> retrieveDocuments(IACache objCache) {
        List<IADocument> cDocuments = new ArrayList<>();
        Class<?> objClass = IADocument.class;
        try {
            objClass = Thread.currentThread().getContextClassLoader().loadClass(mobjConfiguration.getParameter("document_class"));
        } catch (ClassNotFoundException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        XStream objXStream = new XStream(new StaxDriver());
        objXStream.alias(mobjConfiguration.getParameter("document_element_name"), objClass);
        objXStream.processAnnotations(objClass);
        for (IADocumentReference objReference : objCache.getContents()) {
            try (FileReader objReader = new FileReader(objReference.getFile())) {
                IADocument objDocument = (IADocument)objClass.cast(objXStream.fromXML(objReader));
                objDocument.setDocumentId(objReference.getDocumentId());
                cDocuments.add((IADocument) objDocument);
            } catch (ClassCastException | IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }

        return cDocuments;
    }

    @Override
    public boolean getSIPFile(IADocumentReference objDocumentReference, IARetentionClass objRetentionClass) {
        debug(this, "Creating SIP file for Document with ID: " + objDocumentReference.getDocumentId());
        IACache objTempCache = new IACache(-1, objRetentionClass);
        objTempCache.add(objDocumentReference);
        return getSIPFile(objTempCache);
    }

    protected SipAssembler createSipAssembler(PackagingInformation objPackagingInformation, PdiAssembler objPdiAssembler, DigitalObjectsExtraction objDigitalObjectsExtraction) {
        return SipAssembler.forPdiAndContent(objPackagingInformation, objPdiAssembler, objDigitalObjectsExtraction);
    }

    protected PackagingInformation getPackageInformation(IADocument objDocument, IARetentionClass objRetentionClass) {

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
                DigitalObject objObject = DigitalObject.fromBytes(String.format("%s_%s.pdf", sKey, objDocument.getDocumentId()), objDocument.loadContent(sKey));
                cObjects.add(objObject);
            }
            return cObjects.iterator();
        };
    }
}
