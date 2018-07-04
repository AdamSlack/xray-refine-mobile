package org.sociam.koalahero.koala.KoalaData;

import org.json.JSONException;
import org.json.JSONObject;

public class NoJSONData extends JSONData {

    @Override
    public JSONObject toJSONData() {
        return new JSONObject();
    }
}
