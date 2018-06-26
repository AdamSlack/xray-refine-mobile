package org.sociam.koalahero.koala.KoalaData;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONData {

    public abstract JSONObject toJSONData() throws JSONException;
}
