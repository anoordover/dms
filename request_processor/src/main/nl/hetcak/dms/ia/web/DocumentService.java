package nl.hetcak.dms.ia.web;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admjzimmermann on 13-10-2016.
 */
@Path("/rest")
public class DocumentService {
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response listDocuments(String sBody) {
        return Response.ok().build();
    }

    @GET
    @Produces("application/pdf")
    public Response getDocument() {
        return Response.ok().build();
    }

    @GET
    @Path("index")
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        return Response.ok("<html><head><title>DMS</title></head><body>DMS Request Processor</body></html>").build();
    }
}
