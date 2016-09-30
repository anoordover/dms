package com.amplexor.ia.utils;

import com.amplexor.ia.xmlDocument;

import java.io.*;

/**
 * Created by minkenbergs on 30-9-2016.
 */
public class FileWriter {

    public static void writeTestDataFile(String sLocation, String sFileName, xmlDocument document){
        try{
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sLocation+ File.separator+sFileName+".xml"), "utf-8"));
            writer.write("<urn:ArchiefDocument xmlns:urn=\"urn:hetcak:dms:uitingarchief:2016:08\">\n");
            writer.write("\t<urn:MetaData>\n");
            writer.write("\t\t<urn:ArchiefDocumentId>"); writer.write(String.valueOf(document.getArchiefDocumentId()));  writer.write("</urn:ArchiefDocumentId>\n");
            writer.write("\t\t<urn:ArchiefPersoonsnummer>"); writer.write(String.valueOf(document.getArchiefPersoonsnummer()));  writer.write("</urn:ArchiefPersoonsnummer>\n");
            writer.write("\t\t<urn:PersoonBurgerservicenummer>"); writer.write(String.valueOf(document.getPersoonBurgerservericenummer()));  writer.write("</urn:PersoonBurgerservicenummer>\n");
            writer.write("\t\t<urn:ArchiefDocumenttitel>"); writer.write(String.valueOf(document.getArchiefDocumenttitel()));  writer.write("</urn:ArchiefDocumenttitel>\n");
            writer.write("\t\t<urn:ArchiefDocumentsoort>"); writer.write(String.valueOf(document.getArchiefDocumentsoort()));  writer.write("</urn:ArchiefDocumentsoort>\n");
            writer.write("\t\t<urn:ArchiefRegeling>"); writer.write(String.valueOf(document.getArchiefRegeling()));  writer.write("</urn:ArchiefRegeling>\n");
            writer.write("\t\t<urn:ArchiefDocumentkenmerk>"); writer.write(String.valueOf(document.getArchiefDocumentKenmerk())+"-"+String.valueOf(document.getArchiefRegeling()));  writer.write("</urn:ArchiefDocumentkenmerk>\n");
            writer.write("\t\t<urn:ArchiefVerzenddag>"); writer.write(String.valueOf(document.getArchiefVerzenddag().getYear())+"-"+String.format("%02d", document.getArchiefVerzenddag().getMonthValue())+"-"+document.getArchiefVerzenddag().getDayOfMonth());  writer.write("</urn:ArchiefVerzenddag>\n");
            writer.write("\t\t<urn:ArchiefDocumenttype>"); writer.write(String.valueOf(document.getArchiefDocumenttype()));  writer.write("</urn:ArchiefDocumenttype>\n");
            writer.write("\t\t<urn:ArchiefDocumentstatus>"); writer.write(String.valueOf(document.getArchiefDocumentstatus()));  writer.write("</urn:ArchiefDocumentstatus>\n");
            writer.write("\t\t<urn:ArchiefRegelingsjaar>"); writer.write(String.valueOf(document.getArchiefRegelingsjaar()));  writer.write("</urn:ArchiefRegelingsjaar>\n");
            writer.write("\t</urn:MetaData>\n");
            writer.write("\t</urn:PayloadPdf>"); writer.write(String.valueOf(document.getPayloadPdf()));  writer.write("t</urn:PayloadPdf>\n");
            writer.write("</urn:ArchiefDocument>");
            writer.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
