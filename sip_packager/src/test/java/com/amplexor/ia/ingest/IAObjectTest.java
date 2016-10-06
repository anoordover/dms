package com.amplexor.ia.ingest;

import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by minkenbergs on 5-10-2016.
 */
public class IAObjectTest {
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

    @Ignore
    public void fromJSONObject() throws Exception {
        //// TODO: 5-10-2016 need a valid JSONObject to test
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