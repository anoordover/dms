package com.amplexor.ia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Created by admjzimmermann on 14-9-2016.
 */
public class MultipartUtility {
    public static void startMultipart(String aBoundary, HttpURLConnection aConnection) throws IOException {
        aConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + aBoundary);
    }

    public static void addFormField(String aBoundary, HttpURLConnection aConnection, String aName, String aValue) {
        try {
            PrintWriter oWriter = new PrintWriter(aConnection.getOutputStream());
            oWriter.append("--" + aBoundary).append("\r\n");
            oWriter.append("Content-Disposition: form-data; name=\"" + aName + "\"\r\n");
            oWriter.append("Content-Type: text/plain; charset=UTF-8\r\n");
            oWriter.append("\r\n");
            oWriter.append(aValue);
            oWriter.append("\r\n");
            oWriter.flush();
        } catch (IOException ex) {

        }
    }

    public static void addFilePart(String aBoundary, HttpURLConnection aConnection, String aFile, String aFieldName) {
        File oFile = new File(aFile);
        try {
            PrintWriter oWriter = new PrintWriter(aConnection.getOutputStream());
            oWriter.append("--" + aBoundary + "\r\n");
            oWriter.append("Content-Disposition: form-data; name=\"" + aFieldName + "\"; filename=\"" + oFile.getName() + "\"\r\n");
            oWriter.append("Content-Type: " + URLConnection.guessContentTypeFromName(oFile.getName()) + "\r\n");
            oWriter.append("Content-Transfer-Encoding: binary\r\n");
            oWriter.append("\r\n");
            oWriter.flush();

            try (FileInputStream oFileInput = new FileInputStream(oFile)) {
                byte[] oBuffer = new byte[4096];
                int iRead = -1;
                while ((iRead = oFileInput.read(oBuffer)) != -1) {
                    aConnection.getOutputStream().write(oBuffer, 0, iRead);
                }
                aConnection.getOutputStream().flush();
            }
            oWriter.append("\r\n");
            oWriter.flush();

        } catch (IOException ex) {

        }
    }

    public static void addHeaderField(String aBoundary, HttpURLConnection aConnection, String aHeader, String aValue) {
        try {
            PrintWriter oWriter = new PrintWriter(aConnection.getOutputStream());
            oWriter.append(aHeader + ": " + aValue + "\r\n").flush();
        } catch (IOException ex) {

        }
    }

    public static void finishMultipart(String aBoundary, HttpURLConnection aConnection) {
        try {
            PrintWriter oWriter = new PrintWriter(aConnection.getOutputStream());
            oWriter.append("\r\n").flush();
            oWriter.append("--" + aBoundary + "--\r\n").flush();
            oWriter.close();
        } catch (IOException ex) {

        }
    }

}
