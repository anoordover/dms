package nl.hetcak.dms.ia.web.infoarchive.searchComposition;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class SearchCompositions {
    private String id;
    private String name;
    private String searchName;
    private int version;
    
    public SearchCompositions(String id, String name, String searchName, int version) {
        this.id = id;
        this.name = name;
        this.searchName = searchName;
        this.version = version;
    }
    
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
    
    public String getSearchName() {
        return searchName;
    }
    
    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
}
