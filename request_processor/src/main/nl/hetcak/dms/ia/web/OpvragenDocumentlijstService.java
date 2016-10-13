package nl.hetcak.dms.ia.web;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by admjzimmermann on 13-10-2016.
 */
@Path("/document")
public class DocumentService {
    @POST
    @Path("list")
    @Produces(MediaType.APPLICATION_XML)
    public List<IADocument> listDocuments() {

    }

    @GET
    @Path("document")
    @Produces("application/pdf")
    public Response getDocument() {

    }
}
