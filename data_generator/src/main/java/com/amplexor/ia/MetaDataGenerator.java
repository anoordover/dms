package com.amplexor.ia;

import com.amplexor.ia.enums.*;
import com.amplexor.ia.utils.FileWriter;
import com.amplexor.ia.utils.RandomGenerator;
import com.amplexor.ia.utils.TransformPDF;

import java.io.File;

/**
 * Created by minkenbergs on 28-9-2016.
 */
public class MetaDataGenerator {
    private static final String PDFPATH= "D:\\Projecten\\CAK\\Data\\";
    private static final String RESULTPATH= "D:\\Projecten\\CAK\\Generated\\";
    private static final int GENERATEAMOUNT= 100;


    public static void main(String[] cArgs) {
        RandomGenerator rg = new RandomGenerator();
        for(int i = 0; i < GENERATEAMOUNT ; i++){
            xmlDocument doc = new xmlDocument(
                    rg.generateArchiefDocumentId(),
                    rg.generateArchiefPersoonNummer(),
                    rg.generatePersoonBurgersservicenummer(),
                    rg.generateRandomEnum(ArchiefDocumenttitel.class),
                    rg.generateRandomEnum(ArchiefDocumentsoort.class),
                    rg.generateRandomEnum(ArchiefRegeling.class),
                    rg.generateDocumentKenmerkNr(),
                    rg.generateVerzendDag(),
                    rg.generateRandomEnum(ArchiefDocumenttype.class),
                    rg.generateRandomEnum(ArchiefDocumentstatus.class),
                    rg.generateVerzendDag().getYear(), TransformPDF.encodeBase64(PDFPATH+rg.generateIntOneToSix()));

            FileWriter.writeTestDataFile(RESULTPATH, String.valueOf(i), doc);
        }
    }

}
