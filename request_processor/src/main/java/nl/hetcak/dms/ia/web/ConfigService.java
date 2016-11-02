package nl.hetcak.dms.ia.web;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.RequestResponseException;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
@Path("/config")
public class ConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkConfig(@Context HttpServletRequest httpRequest) {
        LOGGER.info(Version.PROGRAM_NAME + " " + Version.currentVersion());
        Calendar calendar = Calendar.getInstance();
        LOGGER.info("Got Request from "+httpRequest.getRemoteAddr()+" @ "+calendar.getTime().toString());
        LOGGER.info("Running log checker.");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading config file...\n");
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        try {
            Configuration configuration = configurationManager.loadConfiguration(false);
            if (!configuration.hasBasicInformation()) {
                throw new MisconfigurationException("Some elements of the configuration have not been configured.");
            }
            stringBuilder.append("[OK] Config file found and loaded.\n");
        } catch (RequestResponseException rrExc) {
            LOGGER.error("There is a problem with the config file.", rrExc);
            stringBuilder.append("[ERROR] Config file not found.\n");
        }
        LOGGER.info(stringBuilder.toString());
        return Response.ok(stringBuilder.toString()).build();
    }
}
