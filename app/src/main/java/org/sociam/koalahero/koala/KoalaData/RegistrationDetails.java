package org.sociam.koalahero.koala.KoalaData;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationDetails extends JSONData{
    public String study_id;
    public String password;

    public RegistrationDetails() {
        this.study_id = "";
        this.password = "";
    }

    public RegistrationDetails(String email, String password) {
        this.study_id = email;
        this.password = password;
    }

    @Override
    public JSONObject toJSONData() throws JSONException{
        JSONObject json = new JSONObject();

        json.put("study_id", this.study_id);
        json.put("password", this.password);

        return json;
    }
}
