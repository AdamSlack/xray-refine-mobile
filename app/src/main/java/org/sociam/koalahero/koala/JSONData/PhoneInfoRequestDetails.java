package org.sociam.koalahero.koala.JSONData;

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
    public JSONObject toJSONData() {

        return null;
    }
}
