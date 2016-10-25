package nl.hetcak.dms;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.IASipConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.retention.IARetentionClass;
import com.amplexor.ia.sip.AMPSipManager;
import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.io.RuntimeIoException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static com.amplexor.ia.Logger.debug;
import static com.amplexor.ia.Logger.info;

/**
 * Created by zimmermannj on 9/30/2016.
 */
public class CAKSipManager extends AMPSipManager {

    public CAKSipManager(IASipConfiguration objConfiguration) {
        super(objConfiguration);
    }

    @Override
    public boolean getSIPFile(IACache objCache) {
        if (objCache.getId() > -1) {
            debug(this, "Creating SIP file for cache with ID: " + objCache.getId());
        }

        boolean bReturn = false;
        debug(this, "Retrieving document data");
        List<IADocument> cDocuments = retrieveDocuments(objCache);
        try {
            if (!cDocuments.isEmpty()) {
                boolean bIsFallback = "Fallback".equals(objCache.getDocumentIdentifier());
                objCache.setTargetApplication(bIsFallback ? mobjConfiguration.getParameter("fallback_application_name") : mobjConfiguration.getApplicationName());
                SipAssembler<IADocument> objSipAssembler = createSipAssembler(getCAKPackageInformation(objCache.getRetentionClass(), bIsFallback), getPdiAssembler(), getDigitalObjects());
                FileGenerator<IADocument> objFileGenerator = new FileGenerator<>(objSipAssembler, new File(mobjConfiguration.getSipOutputDirectory()));
                FileGenerationMetrics objMetrics = objFileGenerator.generate(cDocuments.iterator());
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
            for (IADocument objDocument : cDocuments) {
                lTotalSize += objDocument.getSizeEstimate();
            }

            if (new File(mobjConfiguration.getSipOutputDirectory()).getFreeSpace() < lTotalSize) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_SIP_INSUFFICIENT_DISK_SPACE, objCache, ex);
            }
        } catch (RuntimeIoException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        return bReturn;
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

    public PackagingInformation getCAKPackageInformation(IARetentionClass objRetentionClass, boolean bIsFallback) {
        GregorianCalendar objCalendar = new GregorianCalendar();
        objCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        objCalendar.set(Calendar.MONTH, 0);
        objCalendar.set(Calendar.DAY_OF_MONTH, 1);
        objCalendar.set(Calendar.SECOND, 0);
        objCalendar.set(Calendar.MINUTE, 0);
        objCalendar.set(Calendar.HOUR, 0);
        objCalendar.set(Calendar.MILLISECOND, 0);
        objCalendar.setTimeZone(TimeZone.getDefault());


        String sApplicationName = bIsFallback ? mobjConfiguration.getParameter("fallback_application_name") : mobjConfiguration.getApplicationName();
        String sHoldingName = bIsFallback ? mobjConfiguration.getParameter("fallback_holding_name") : mobjConfiguration.getHoldingName();
        PackagingInformation.PackagingInformationBuilder objBuilder = PackagingInformation.builder();
        DataSubmissionSession.DataSubmissionSessionBuilder objDssBuilder = objBuilder.dss();
        objDssBuilder.holding(sHoldingName)
                .application(sApplicationName)
                .producer(mobjConfiguration.getProducerName())
                .entity(mobjConfiguration.getEntityName())
                .schema(mobjConfiguration.getSchemaDeclaration())
                .retentionClass(objRetentionClass.getName().replace(' ', '_'));
        if (mobjConfiguration.getParameter("set_base_retention_date") != null && "true".equals(mobjConfiguration.getParameter("set_base_retention_date").toLowerCase())) {
            objDssBuilder.baseRetentionDate(objCalendar.getTime());
        }
        return objDssBuilder.end().build();
    }
}

