package nl.hetcak.dms.ia.web;

import nl.hetcak.dms.ia.web.configuration.Configuration;
import nl.hetcak.dms.ia.web.exceptions.MisconfigurationException;
import nl.hetcak.dms.ia.web.exceptions.MissingConfigurationException;
import nl.hetcak.dms.ia.web.managers.ConfigurationManager;
import nl.hetcak.dms.ia.web.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

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
    public Response checkConfig() {
        LOGGER.info(Version.PROGRAM_NAME+" "+Version.currentVersion());
        LOGGER.info("Running log checker.");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading config file...\n");
        ConfigurationManager configurationManager = new ConfigurationManager();
        try {
            Configuration configuration = configurationManager.loadConfiguration(null);
            if(!configuration.hasBasicInformation()) {
                throw new MisconfigurationException("Some elements of the configuration have not been configured.");
            }
            stringBuilder.append("[OK] Config file found and loaded.\n");
        } catch (MissingConfigurationException missingcexc) {
            LOGGER.error("Config file not found.",missingcexc);
            stringBuilder.append("[ERROR] Config file not found.\n");
        } catch (MisconfigurationException mcexc) {
            LOGGER.error("Config file not correctly configured.",mcexc);
            stringBuilder.append("Config file found.\n");
            stringBuilder.append("[ERROR] Config file is invalid.\n");
        }
        LOGGER.info(stringBuilder.toString());
        return Response.ok(stringBuilder.toString()).build();
    }
    
    @GET
    @Path("/createKey")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getRandomKey() {
        LOGGER.info(Version.PROGRAM_NAME+" "+Version.currentVersion());
        LOGGER.info("Create random key request.");
        StreamingOutput output = outputStream -> {
            LOGGER.info("Creating new random key.");
            outputStream.write(CryptoUtil.createRandomKey());
            outputStream.close();
            LOGGER.info("Returning Random key.");
        };
        return Response.ok(output).build();
    }
    
    public Response getEmptyConfig() {
        //todo
        return Response.ok().build();
    }
}
