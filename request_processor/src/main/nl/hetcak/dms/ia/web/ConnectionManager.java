package nl.hetcak.dms.ia.web;

import com.amplexor.ia.ingest.IAObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zimmermannj on 10/14/2016.
 */
public class ConnectionManager {
    private RequestProcessorConfiguration mobjConfiguration;

    public ConnectionManager(RequestProcessorConfiguration objConfiguration) {
        mobjConfiguration = objConfiguration;
    }


    /**
     * Search for documents with the criteria defined in the cCriteria Map
     *
     * @param cCriteria a Map containing criteria for the search
     * @return a List of IADocuments resulting from seach.
     */
    public List<IADocument> searchDocuments(Map<String, String> cCriteria) {
        List<IADocument> cDocuments = new ArrayList<>();
        return cDocuments;
    }

    public byte[] getDocumentData(String msDocumentId) {
        return new byte[0];
    }

    public IAObject restCall() {
        return new IAObject();
    }
}
