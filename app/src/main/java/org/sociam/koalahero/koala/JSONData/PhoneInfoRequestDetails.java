package org.sociam.koalahero.koala.JSONData;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneInfoRequestDetails extends JSONData{
    AuthDetails authDetails;
    PhoneInfo phoneInfo;

    public PhoneInfoRequestDetails() {
        this.authDetails = new AuthDetails();
        this.phoneInfo = new PhoneInfo();
    }

    public PhoneInfoRequestDetails(AuthDetails authDetails, PhoneInfo phoneInfo) {
        this.phoneInfo = phoneInfo;
        this.authDetails = authDetails;
    }

    @Override
    public JSONObject toJSONData() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("auth_details", this.authDetails);
        json.put("phone_info", this.phoneInfo.toJSONData());

        return null;
    }
}
