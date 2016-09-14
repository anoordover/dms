package com.amplexor.ia.ingest;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admjzimmermann on 14-9-2016.
 */
public class IAObject {
    private String mName;
    private String mUUID;
    private Map<String, String> mLinks;

    public IAObject() {
        mName = "";
        mUUID = "";
        mLinks = new HashMap<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String aName) {
        mName = aName;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String aUUID) {
        mUUID = aUUID;
    }

    public String getLink(String aLink) {
        return mLinks.get(aLink);
    }

    public void addLink(String aName, String aLink) {
        mLinks.put(aName, aLink);
    }

    public Set<String> getAvailableLinks() {
        return mLinks.keySet();
    }

    public static IAObject fromJSONObject(JSONObject aObject) {
        IAObject oReturn = new IAObject();
        oReturn.setName((String) aObject.get("name"));
        JSONObject oLinks = (JSONObject) aObject.get("_links");
        for (Object key : oLinks.keySet()) {
            JSONObject oLinkObject = (JSONObject) oLinks.get(key);
            String sKeytype = ((String) key).substring(((String) key).lastIndexOf('/') + 1);
            oReturn.addLink((String) sKeytype, (String) oLinkObject.get("href"));

            if (key.equals("self")) {
                String sSelfHref = (String) oLinkObject.get("href");
                oReturn.setUUID(sSelfHref.substring(sSelfHref.lastIndexOf('/') + 1));
            }
        }
        return oReturn;
    }

    @Override
    public String toString() {
        StringBuilder oBuilder = new StringBuilder();
        oBuilder.append("Name: ");
        oBuilder.append(mName);
        oBuilder.append("UIID: ");
        oBuilder.append(mUUID);
        oBuilder.append("__LINKS__");
        for(String sKey : mLinks.keySet()) {
            oBuilder.append(sKey);
            oBuilder.append(": ");
            oBuilder.append(mLinks.get(sKey));
        }
        oBuilder.append("_________");
        return oBuilder.toString();
    }
}
