package org.sociam.koalahero.koala.JSONData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class InteractionLog<AdditionalData extends JSONData> extends JSONData{
    public int id;
    public String studyID;
    public String interactionType;
    public Date interactionDatetime;
    public String associatedAppID;
    public String pageName;
    public AdditionalData additionalData;

    public InteractionLog() {
        this.id = -1;
        this.studyID = "";
        this.interactionType = "";
        this.interactionDatetime = new Date();
        this.associatedAppID = "";
        this.pageName = "";
        this.additionalData = null;
    }

    public InteractionLog(String studyID, String interactionType, Date interactionDatetime, String associatedAppID, String pageName, AdditionalData additionalData) {
        this.id = -1;
        this.studyID = studyID;
        this.interactionType = interactionType;
        this.interactionDatetime = interactionDatetime;
        this.associatedAppID = associatedAppID;
        this.pageName = pageName;
        this.additionalData = additionalData;
    }

    @Override
    public JSONObject toJSONData() throws JSONException{
        JSONObject json = new JSONObject();

        json.put("study_id", this.studyID);
        json.put("interaction_type", this.interactionType);
        json.put("interaction_datetime", this.toString());
        json.put("associated_app_id", this.associatedAppID);
        json.put("page_name", this.pageName);
        json.put("additional_data", additionalData.toJSONData());

        return json;
    }
}
