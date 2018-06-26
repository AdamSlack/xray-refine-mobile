package org.sociam.koalahero.koala.KoalaData;

import org.json.JSONException;
import org.json.JSONObject;
import org.sociam.koalahero.PreferenceManager.PreferenceManager;

public class AuthDetails extends JSONData {
    public String studyID;
    public String token;

    public AuthDetails() {
        this.studyID = "";
        this.token = "";
    }

    public AuthDetails(PreferenceManager pm) {
        this.studyID = pm.getKoalaStudyID();
        this.token = pm.getKoalaToken();
    }

    public AuthDetails(String studyID, String token) {
        this.studyID = studyID;
        this.token = token;
    }

    @Override
    public JSONObject toJSONData() throws JSONException{
        JSONObject json= new JSONObject();
        json.put("study_id", this.studyID);
        json.put("token", this.token);
        return json;
    }
}
