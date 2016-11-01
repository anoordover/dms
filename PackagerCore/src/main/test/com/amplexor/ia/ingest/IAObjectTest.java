package com.amplexor.ia.ingest;

import com.amplexor.ia.configuration.ConfigManager;
import com.amplexor.ia.ingest.IAObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by minkenbergs on 5-10-2016.
 */
public class IAObjectTest {
    ConfigManager mobjConfigManager;

    @Before
    public void setup() {
        System.out.println(System.getProperty("user.dir"));
        mobjConfigManager = new ConfigManager("IAArchiver.xml");
        mobjConfigManager.loadConfiguration();
    }

    @Test
    public void setgetName() throws Exception {
        IAObject iaotest = new IAObject();
        iaotest.setName("testuser");
        assertEquals(iaotest.getName(), "testuser");
    }

    @Test
    public void setgetUUID() throws Exception {
        IAObject iaotest = new IAObject();
        iaotest.setUUID("testUUID");
        assertEquals(iaotest.getUUID(), "testUUID");
    }

    @Test
    public void addgetLink() throws Exception {
        IAObject iaotest = new IAObject();
        iaotest.addLink("testLink", "testLink");
        assertEquals(iaotest.getLink("testLink"), "testLink");
    }

    @Test
    public void getAvailableLinks() throws Exception {
        Set<String> copareMap = new HashSet<>();
        copareMap.add("testLink");

        IAObject iaotest = new IAObject();
        iaotest.addLink("testLink", "testLink");

        assertEquals(iaotest.getAvailableLinks(), copareMap);
    }

    @Test
    public void fromJSONObject() throws Exception {
        String s = new StringBuilder("{\n" +
                "\"createdDate\" : \"2016-10-11T12:00:00,1234+02:00\"\n" +
                "\"createdBy\" : \"system\"\n" +
                "\"lastModifiedDate\" : \"2016-10-10T12:00:00,1234+02:00\"\n" +
                "\"_links\" : {\n" +
                "\"http://identifiers.emc.com/applications\" : {\"href\" : \"http://cwno0427:8775/systemdata/tenants/6ec37bc7-6ce9-4c14-8422-eff057646627/applications\"},\n" +
                "\"http:\" : {\"href\" : \"http://cwno0427:8775/systemdata/tenants/6ec37bc7-6ce9-4c14-8422-eff057646627/applications\"}\n" +
                "}\n" +
                "\"lastModifiedBy\" : \"system\"\n" +
                "\"name\": \"INFOARCHIVE\"\n" +
                "\"permission\" : [\n" +
                "{ \"groups\" : [] }\n" +
                "]\n" +
                "\"version\": \"1\"\n" +
                "}").toString();
        JSONObject jsonobject = (JSONObject) new JSONParser().parse(s);
        IAObject parsedObject = IAObject.fromJSONObject(jsonobject);

        assertEquals(parsedObject.getName(), "INFOARCHIVE");
    }

    @Test
    public void toStringTest() throws Exception {
        Map<String, String> copareMap = new HashMap<>();
        copareMap.put("testLink", "testLink");
        StringBuilder objBuilder = new StringBuilder();
        objBuilder.append("IAObject\n");
        objBuilder.append("Name: ");
        objBuilder.append("testuser");
        objBuilder.append("\n");
        objBuilder.append("UIID: ");
        objBuilder.append("testUUID");
        objBuilder.append("\n");
        objBuilder.append("__LINKS__");
        objBuilder.append("\n");
        for (Map.Entry<String, String> objEntry : copareMap.entrySet()) {
            objBuilder.append(objEntry.getKey());
            objBuilder.append(": ");
            objBuilder.append(objEntry.getValue());
            objBuilder.append("\n");
        }


        IAObject iaotest = new IAObject();
        iaotest.setName("testuser");
        iaotest.setUUID("testUUID");
        iaotest.addLink("testLink", "testLink");

        assertEquals(iaotest.toString(), objBuilder.toString());
    }

}