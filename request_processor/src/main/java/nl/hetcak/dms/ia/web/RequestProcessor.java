package nl.hetcak.dms.ia.web;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by zimmermannj on 10/14/2016.
 */
@ApplicationPath("dms")
public class RequestProcessor extends ResourceConfig {
    public RequestProcessor() {
        packages("nl.hetcak.dms.ia.web");
    }
}
