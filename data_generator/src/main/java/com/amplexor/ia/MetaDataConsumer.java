package com.amplexor.ia;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.cache.IADocumentReference;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.document_source.DocumentSource;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Created by minkenbergs on 30-9-2016.
 */
public class MetaDataConsumer implements DocumentSource {
    private PluggableObjectConfiguration mobjConfiguration;
    private static final int BUFFER_SIZE = 4096;

    public MetaDataConsumer(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public String retrieveDocumentData() {
        String sReturn = "";
        try {
            Path objSourcePath = Paths.get(mobjConfiguration.getParameter("source_path"));
            Optional<Path> objEntry = Files.list(objSourcePath).findFirst();
            if (objEntry.isPresent()) {
                sReturn = readEntry(objEntry.get());
            }
        } catch (IOException ex) {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
        }
        return sReturn;
    }

    @Override
    public void postResult(List<IADocumentReference> objCache) {
        //Cannot post back to filesystem
    }

    private String readEntry(Path objEntryPath) {
        if (objEntryPath != null) {
            StringBuilder objBuilder = new StringBuilder();
            FileLock objLock = null;
            try (FileInputStream objInputStream = new FileInputStream(objEntryPath.toFile())) {
                objLock = objInputStream.getChannel().tryLock();
                if (objLock != null) {
                    int iRead;
                    byte[] cBytesRead = new byte[BUFFER_SIZE];
                    while ((iRead = objInputStream.read(cBytesRead, 0, BUFFER_SIZE)) != -1) {
                        objBuilder.append(new String(cBytesRead, 0, iRead, StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            } finally {
                if (objLock != null) {
                    try {
                        objLock.release();
                    } catch (IOException ex) {
                        ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
                    }
                }
            }
            return objBuilder.toString();
        }

        return "";
    }


    @Override
    public void initialize() {
        //Nothing to initialize
    }

    @Override
    public void shutdown() {
        //Nothing to clean
    }
}
