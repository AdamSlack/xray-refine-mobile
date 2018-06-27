package org.sociam.koalahero.trackerMapper;

import java.util.ArrayList;

public class TrackerMapperCompany {
    public String hostName;
    public String companyName;
    public int hostID;
    public int companyID;
    public ArrayList<String> categories;
    public int occurrences; // How many times did a hostname for this company occur?

    public TrackerMapperCompany() {
        this.hostName = "";
        this.companyName = "";

        this.hostID = -1;
        this.companyID = -1;

        this.categories = new ArrayList<>();

        this.occurrences = 0;
    }
}
