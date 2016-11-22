package nl.hetcak.dms.ia.web.infoarchive.tenant;

import org.apache.commons.lang3.StringUtils;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class Tenant {
    private String id;
    private String name;
    private int version;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public boolean isNotBlank() {
        if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(name)) {
            return true;
        }
        return false;
    }
}
