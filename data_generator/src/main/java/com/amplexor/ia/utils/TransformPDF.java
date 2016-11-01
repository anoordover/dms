package com.amplexor.ia.utils;

import com.amplexor.ia.exception.ExceptionHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Created by minkenbergs on 29-9-2016.
 */
public class TransformPDF {
    private TransformPDF() {
    }

    public static String encodeBase64(String sFile) {
        try {
            Path objPath = Paths.get(sFile);
            byte[] cData = Files.readAllBytes(objPath);
            return Base64.getEncoder().encodeToString(cData);
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }

        return null;
    }
}
