package com.amplexor.ia.ingest;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admjzimmermann on 14-9-2016.
 */
public class IAObject {
    private String msName;
    private String msUUID;
    private Map<String, String> mcLinks;

    public IAObject() {
        msName = "";
        msUUID = "";
        mcLinks = new HashMap<>();
    }

    public String getName() {
        return msName;
    }

    public void setName(String sName) {
        msName = sName;
    }

    public String getUUID() {
        return msUUID;
    }

    public void setUUID(String sUUID) {
        msUUID = sUUID;
    }

    public String getLink(String sLink) {
        return mcLinks.get(sLink);
    }

    public void addLink(String sName, String sLink) {
        mcLinks.put(sName, sLink);
    }

    public Set<String> getAvailableLinks() {
        return mcLinks.keySet();
    }

    public static IAObject fromJSONObject(JSONObject objObject) {
        IAObject objReturn = new IAObject();
        objReturn.setName((String) objObject.get("name"));
        JSONObject oLinks = (JSONObject) objObject.get("_links");

        for (Object objKey : oLinks.keySet()) {
            JSONObject objLinkObject = (JSONObject) oLinks.get(objKey);
            String sKeytype = ((String) objKey).substring(((String) objKey).lastIndexOf('/') + 1);
            objReturn.addLink(sKeytype, (String) objLinkObject.get("href"));
            if ("self".equals(objKey)) {
                String sSelfHref = (String) objLinkObject.get("href");
                objReturn.setUUID(sSelfHref.substring(sSelfHref.lastIndexOf('/') + 1));
            }
        }

        return objReturn;
    }

    @Override
    public String toString() {
        StringBuilder objBuilder = new StringBuilder();
        objBuilder.append("IAObject\n");
        objBuilder.append("Name: ");
        objBuilder.append(msName);
        objBuilder.append("\n");
        objBuilder.append("UIID: ");
        objBuilder.append(msUUID);
        objBuilder.append("\n");
        objBuilder.append("__LINKS__");
        objBuilder.append("\n");
        for (Map.Entry<String, String> objEntry : mcLinks.entrySet()) {
            objBuilder.append(objEntry.getKey());
            objBuilder.append(": ");
            objBuilder.append(objEntry.getValue());
            objBuilder.append("\n");
        }
        return objBuilder.toString();
    }
}
