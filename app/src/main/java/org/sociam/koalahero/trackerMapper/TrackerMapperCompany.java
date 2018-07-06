package org.sociam.koalahero.trackerMapper;

import java.util.ArrayList;

public class TrackerMapperCompany {
    public String hostName;
    public String companyName;
    public int hostID;
    public int companyID;
    public ArrayList<String> categories;
    public int occurrences; // How many times did a hostname for this company occur?
    public String locale; // iso_6391 country code. e.g. US, UK, JA, ...
    public String appPackageName; // The associated App Package Name.

    public TrackerMapperCompany() {
        this.hostName = "";
        this.companyName = "";

        this.hostID = -1;
        this.companyID = -1;

        this.categories = new ArrayList<>();

        this.occurrences = 0;

        this.appPackageName = "";
    }
}
