package org.sociam.koalahero.koala.KoalaData;

import org.json.JSONException;
import org.json.JSONObject;

public class AudioLogRequestDetails extends JSONData{
    public AuthDetails authDetails;
    public AudioDetails audioDetails;

    public AudioLogRequestDetails() {
        this.authDetails = new AuthDetails();
        this.audioDetails = new AudioDetails();
    }
    public AudioLogRequestDetails(AuthDetails authDetails, AudioDetails audioDetails) {
        this.authDetails = authDetails;
        this.audioDetails = audioDetails;
    }

    @Override
    public JSONObject toJSONData() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("auth_details", this.authDetails.toJSONData());
        json.put("interaction", this.audioDetails.toJSONData());

        return json;
    }
}
