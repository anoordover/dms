package nl.hetcak.dms.ia.web;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admjzimmermann on 13-10-2016.
 */
@Path("/document")
public class DocumentService {
    @POST
    @Path("list")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response listDocuments(String sBody) {
        List<IADocument> cDocuments = new ArrayList<>();
        IADocument objDocument = new IADocument();
        objDocument.setArchiefDocumentId("1000010000");

        cDocuments.add(objDocument);
        return Response.ok(
                new GenericEntity<List<IADocument>>(
                        new ArrayList<IADocument>(cDocuments)) {
                }).build();
    }

    @GET
    @Path("document")
    @Produces("application/pdf")
    public Response getDocument() {
        return Response.ok().build();
    }
}
