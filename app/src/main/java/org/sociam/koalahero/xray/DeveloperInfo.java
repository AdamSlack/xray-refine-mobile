package org.sociam.koalahero.xray;

import java.util.ArrayList;

public class DeveloperInfo {
    public ArrayList<String> emails;
    public String devName;
    public String playStoreSiteURL;
    public String siteURL;

    public DeveloperInfo() {
        this.emails = new ArrayList<>();
        this.devName = "";
        this.playStoreSiteURL = "";
        this.siteURL = "";
    }
}
