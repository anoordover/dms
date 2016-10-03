package com.amplexor.ia;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.configuration.PluggableObjectConfiguration;
import com.amplexor.ia.exception.ExceptionHelper;
import com.amplexor.ia.metadata.IADocument;
import com.amplexor.ia.document_source.DocumentSource;
import org.apache.activemq.store.kahadb.disk.page.PageFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static com.amplexor.ia.Logger.info;

/**
 * Created by minkenbergs on 30-9-2016.
 */
public class MetaDataConsumer implements DocumentSource {
    private PluggableObjectConfiguration mobjConfiguration;
    private int iCounter = 0;

    public MetaDataConsumer(PluggableObjectConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }

    @Override
    public String retrieveDocumentData() {
        String sReturn = "";
        Path objSourcePath = Paths.get(mobjConfiguration.getParameter("source_path"));
        Path objToDelete = null;
        if (Files.exists(objSourcePath)) {
            try (DirectoryStream<Path> objDirectoryStream = Files.newDirectoryStream(objSourcePath)) {
                Iterator<Path> objDirectoryIterator = objDirectoryStream.iterator();
                if (objDirectoryIterator.hasNext()) {
                    Path objEntry = objDirectoryIterator.next();
                    if (objEntry != null) {
                        for (String sLine : Files.readAllLines(objEntry)) {
                            sReturn += sLine;
                        }
                        objToDelete = objEntry;
                    }
                }
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);

            }
        } else {
            ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, new Exception("Could not find source path"));
            System.exit(ExceptionHelper.ERROR_OTHER);
        }

        if (objToDelete != null) {
            try {
                iCounter++;
                Files.delete(objToDelete);
            } catch (IOException ex) {
                ExceptionHelper.getExceptionHelper().handleException(ExceptionHelper.ERROR_OTHER, ex);
            }
        } else {
            info(this, "Read " + iCounter + " Files");
        }

        return sReturn;
    }

    @Override
    public void postResult(IADocument objDocument) {

    }

    @Override
    public void postResult(IACache objCache) {

    }
}
