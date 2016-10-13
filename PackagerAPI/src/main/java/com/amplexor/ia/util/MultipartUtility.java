package com.amplexor.ia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import static com.amplexor.ia.Logger.error;

/**
 * Created by admjzimmermann on 14-9-2016.
 */
public class MultipartUtility {
    private static final String LINE_FEED = "\r\n";

    //Hides the implicit public constructor
    private MultipartUtility() {
    }

    public static void startMultipart(String sBoundary, HttpURLConnection objConnection) throws IOException {
        objConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + sBoundary);
    }

    public static void addFormField(String sBoundary, HttpURLConnection objConnection, String sName, String sValue) {
        try {
            PrintWriter objWriter = new PrintWriter(objConnection.getOutputStream());
            objWriter.append("--").append(sBoundary).append(LINE_FEED);
            objWriter.append("Content-Disposition: form-data; name=\"").append(sName).append("\"").append(LINE_FEED);
            objWriter.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
            objWriter.append(LINE_FEED);
            objWriter.append(sValue);
            objWriter.append(LINE_FEED);
            objWriter.flush();
        } catch (IOException ex) {
            error(MultipartUtility.class, ex);
        }
    }

    public static void addFilePart(String sBoundary, HttpURLConnection objConnection, String sFile, String sFieldName) throws IOException {
        File objFile = new File(sFile);
        PrintWriter objWriter = new PrintWriter(objConnection.getOutputStream());
        objWriter.append("--").append(sBoundary).append(LINE_FEED);
        objWriter.append("Content-Disposition: form-data; name=\"").append(sFieldName).append("\"; filename=\"").append(objFile.getName()).append("\"").append(LINE_FEED);
        objWriter.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(objFile.getName())).append(LINE_FEED);
        objWriter.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        objWriter.append(LINE_FEED);
        objWriter.flush();

        try (FileInputStream objFileInput = new FileInputStream(objFile)) {
            byte[] objBuffer = new byte[4096];
            int iRead;
            while ((iRead = objFileInput.read(objBuffer)) != -1) {
                objConnection.getOutputStream().write(objBuffer, 0, iRead);
            }
            objConnection.getOutputStream().flush();
        }
        objWriter.append(LINE_FEED);
        objWriter.flush();
    }

    public static void addHeaderField(HttpURLConnection objConnection, String sHeader, String sValue) throws IOException {
        PrintWriter objWriter = new PrintWriter(objConnection.getOutputStream());
        objWriter.append(sHeader).append(": ").append(sValue).append(LINE_FEED).flush();
    }

    public static void finishMultipart(String sBoundary, HttpURLConnection objConnection) throws IOException {
        PrintWriter objWriter = new PrintWriter(objConnection.getOutputStream());
        objWriter.append(LINE_FEED).flush();
        objWriter.append("--").append(sBoundary).append("--").append(LINE_FEED).flush();
        objWriter.close();
    }

}
