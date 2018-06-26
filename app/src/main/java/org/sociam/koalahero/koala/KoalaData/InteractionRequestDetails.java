package org.sociam.koalahero.koala.KoalaData;

import org.json.JSONException;
import org.json.JSONObject;

public class InteractionRequestDetails extends JSONData{
    public AuthDetails authDetails;
    public InteractionLog interactionLog;

    public InteractionRequestDetails() {
        this.authDetails = new AuthDetails();
        this.interactionLog = new InteractionLog();
    }
    public InteractionRequestDetails(AuthDetails authDetails, InteractionLog interactionLog) {
        this.authDetails = authDetails;
        this.interactionLog = interactionLog;
    }

    @Override
    public JSONObject toJSONData() throws JSONException{
        JSONObject json = new JSONObject();

        json.put("auth_details", authDetails.toJSONData());
        json.put("interaction", interactionLog.toJSONData());

        return json;
    }
}
