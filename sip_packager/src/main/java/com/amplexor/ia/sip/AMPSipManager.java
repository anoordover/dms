package com.amplexor.ia.sip;

import com.amplexor.ia.cache.IACache;
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
import java.util.*;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

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
    public boolean getSIPFile(IACache objCache) {
        if (objCache.getId() > -1) { //Sip is a single document
            debug(this, "Creating SIP file for cache with ID: " + objCache.getId());
        }

        boolean bReturn = false;
        try {
            SipAssembler<IADocument> objSipAssembler = createSipAssembler(getPackageInformation(objCache.getRetentionClass()), getPdiAssembler(), getDigitalObjects());
            FileGenerator<IADocument> objFileGenerator = new FileGenerator<>(objSipAssembler, new File(mobjConfiguration.getSipOutputDirectory()));
            FileGenerationMetrics objMetrics = objFileGenerator.generate(objCache.getContents().iterator());
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
        } catch (IOException ex) {
            long lTotalSize = 0;
            for (IADocument objDocument : objCache.getContents()) {
                lTotalSize += objDocument.getSizeEstimate();
            }

            if (new File(mobjConfiguration.getSipOutputDirectory()).getFreeSpace() < lTotalSize) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SIP_INSUFFICIENT_DISK_SPACE, ex);
            }
        }
        return bReturn;
    }

    @Override
    public boolean getSIPFile(IADocument objDocument, IARetentionClass objRetentionClass) {
        debug(this, "Creating SIP file for Document with ID: " + objDocument.getDocumentId());
        IACache objTempCache = new IACache(-1, objRetentionClass);
        objTempCache.add(objDocument);
        return getSIPFile(objTempCache);
    }

    private SipAssembler createSipAssembler(PackagingInformation objPackagingInformation, PdiAssembler objPdiAssembler, DigitalObjectsExtraction objDigitalObjectsExtraction) {
        return SipAssembler.forPdiAndContent(objPackagingInformation, objPdiAssembler, objDigitalObjectsExtraction);
    }

    private PackagingInformation getPackageInformation(IARetentionClass objRetentionClass) {
        GregorianCalendar objCalendar = new GregorianCalendar();
        objCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        objCalendar.set(Calendar.MONTH, 1);
        objCalendar.set(Calendar.DAY_OF_MONTH, 1);
        objCalendar.set(Calendar.SECOND, 0);
        objCalendar.set(Calendar.MINUTE, 0);
        objCalendar.set(Calendar.HOUR, 0);
        objCalendar.set(Calendar.MILLISECOND, 0);
        objCalendar.setTimeZone(TimeZone.getDefault());

        return PackagingInformation.builder().dss()
                .holding(mobjConfiguration.getHoldingName())
                .application(mobjConfiguration.getApplicationName())
                .producer(mobjConfiguration.getProducerName())
                .entity(mobjConfiguration.getEntityName())
                .schema(mobjConfiguration.getSchemaDeclaration())
                .retentionClass(objRetentionClass.getName().replace(' ', '_'))
                .baseRetentionDate(objCalendar.getTime())
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
