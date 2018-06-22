package org.sociam.koalahero.csm;

public class CSMAppInfo {
    public CSMAppInfo() {
        this.id = -1;
        this.name = "";
        this.ageRating = "";
        this.CSMRating = -1;
        this.oneLiner = "";
        this.CSMURI = "";
        this.appPackageName = "";
        this.parentalGuidances = new CSMParentalGuidance();
    }
    public int id;
    public String name;
    public String ageRating;
    public int CSMRating;
    public String oneLiner;
    public String CSMURI;
    public String playStoreURL;
    public String appPackageName;
    public CSMParentalGuidance parentalGuidances;
}
