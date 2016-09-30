package com.amplexor.ia;

import com.amplexor.ia.cache.IACache;
import com.amplexor.ia.metadata.IADocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by minkenbergs on 30-9-2016.
 */
public class MetaDataConsumer implements DocumentSource {

    private static final String RESULTPATH = "D:\\Projecten\\CAK\\Generated\\";

    @Override
    public String retrieveDocumentData() {
        File f = new File(RESULTPATH);
        String sReturn = null;
        try {
            File[] files = f.listFiles();
            if(files.length > 0) {
                File file = files[0];
                StringBuilder objBuilder = new StringBuilder();
                Files.readAllLines(file.toPath()).forEach(sLine -> objBuilder.append(sLine));
                sReturn = objBuilder.toString();
                file.delete();
            }
        } catch (IOException ex) {

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
