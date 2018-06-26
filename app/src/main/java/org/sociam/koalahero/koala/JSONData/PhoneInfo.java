package org.sociam.koalahero.koala.JSONData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class PhoneInfo extends JSONData {
    public int id;
    public String studyID;
    public Date retrievalDatetime;
    public ArrayList<String> installedApps;
    public ArrayList<String> topTenApps;

    public PhoneInfo() {
        this.id = -1;
        this.studyID = "";
        this.retrievalDatetime = new Date();
        this.installedApps = new ArrayList<String>();
        this.topTenApps = new ArrayList<String>();
    }

    public PhoneInfo(String studyID, Date retrievalDatetime, ArrayList<String> installedApps, ArrayList<String> topTenApps) {
        this.studyID = studyID;
        this.retrievalDatetime = retrievalDatetime;
        this.installedApps = installedApps;
        this.topTenApps = topTenApps;
    }

    @Override
    public JSONObject toJSONData() throws  JSONException{
        JSONObject json = new JSONObject();

        json.put("study_id", this.studyID);
        json.put("retrieval_datetime", this.retrievalDatetime);
        json.put("installed_apps", this.installedApps);
        json.put("top_ten_apps", this.topTenApps);

        return null;
    }
}
