package com.amplexor.ia;

import com.amplexor.ia.configuration.ExceptionConfiguration;
import com.amplexor.ia.enums.*;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.utils.FileWriter;
import com.amplexor.ia.utils.RandomGenerator;
import com.amplexor.ia.utils.TransformPDF;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by minkenbergs on 28-9-2016.
 */
public class MetaDataGenerator {
    private static final String PDFPATH = "C:\\Users\\zimmermannj\\Desktop\\CAK\\pdf\\";
    private static final String RESULTPATH = "C:\\Users\\zimmermannj\\Desktop\\CAK\\data\\";
    private static final int GENERATEAMOUNT = 2000;


    public static void main(String[] cArgs) {
        ExceptionHelper.getExceptionHelper().setExceptionConfiguration(new ExceptionConfiguration());
        Path objResultPath = Paths.get(RESULTPATH);
        Path objPdfPath = Paths.get(PDFPATH);
        if (!Files.exists(objResultPath)) {
            try {
                Files.createDirectories(objResultPath);
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        }

        if (!Files.exists(objPdfPath)) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, new Exception("PDF Path does not exist, Exiting"));
            System.exit(ExceptionHelper.ERROR_OTHER);
        }

        RandomGenerator rg = new RandomGenerator();
        for (int i = 0; i < GENERATEAMOUNT; i++) {
            XmlDocument objDocument = new XmlDocument();
            objDocument.setArchiefDocumentId(rg.generateArchiefDocumentId());
            objDocument.setArchiefPersoonsnummer(rg.generateArchiefPersoonNummer());
            objDocument.setPersoonBurgerservicenummer(rg.generatePersoonBurgersservicenummer());
            objDocument.setArchiefDocumenttitel(rg.generateRandomEnum(ArchiefDocumenttitel.class));
            objDocument.setArchiefDocumentsoort(rg.generateRandomEnum(ArchiefDocumentsoort.class));
            objDocument.setArchiefRegeling(rg.generateRandomEnum(ArchiefRegeling.class));
            objDocument.setArchiefDocumentkenmerk(rg.generateDocumentKenmerkNr());
            objDocument.setVerzenddag(rg.generateVerzendDag());
            objDocument.setArchiefDocumenttype(rg.generateRandomEnum(ArchiefDocumenttype.class));
            objDocument.setArchiefDocumentstatus(rg.generateRandomEnum(ArchiefDocumentstatus.class));
            objDocument.setRegelingjaar(rg.generateVerzendDag().getYear());
            objDocument.setPayloadPdf(TransformPDF.encodeBase64(PDFPATH + rg.generateIntOneToSix()));

            FileWriter.toXml(RESULTPATH, String.valueOf(i), objDocument);
        }
    }

}
