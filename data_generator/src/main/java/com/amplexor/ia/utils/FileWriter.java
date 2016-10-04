package com.amplexor.ia.utils;

import com.amplexor.ia.XmlDocument;
import com.amplexor.ia.exception.ExceptionHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by minkenbergs on 30-9-2016.
 */
public class FileWriter {

    public static void toXml(String sLocation, String sName, XmlDocument objDocument) {
        try {
            Files.write(Paths.get(sLocation + File.separatorChar + sName), objDocument.getXml().getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
    }
}
