package org.sociam.koalahero.csm;


import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;

public class CSMJsonParser {

    public CSMJsonParser() {

    }

    public CSMAppInfo parseCSMApp(JsonReader jsonReader) {
        CSMAppInfo csmAppInfo = new CSMAppInfo();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {

                // Check next JSON token is a Name...
                String name = "";
                if (jsonReader.peek() == JsonToken.NAME) {
                    name = jsonReader.nextName();
                }

                /*
                    "id": 2023,
                    "name": "Rudi Rainbow – Children's Book",
                    "age_rating": "age 5+",
                    "csm_rating": 4,
                    "one_liner": "Fun is in the forecast with cute, playful weather adventure.",
                    "csm_uri": "/app-reviews/rudi-rainbow-childrens-book",
                    "play_store_url": "https://play.google.com/store/apps/details?id=com.hello_november.rudirainbow",
                    "app_package_name": "com.hello_november.rudirainbow",
                    "parental_guidances": { ... }
                */

                if(name.equals("id")) {
                    csmAppInfo.id = jsonReader.nextInt();
                }
                else if (name.equals("name")) {
                    csmAppInfo.name = jsonReader.nextString();
                }
                else if (name.equals("age_rating")){
                    csmAppInfo.ageRating = jsonReader.nextString();
                }
                else if(name.equals("csm_rating")) {
                    csmAppInfo.CSMRating = jsonReader.nextInt();
                }
                else if(name.equals("one_liner")) {
                    csmAppInfo.oneLiner = jsonReader.nextString();
                }
                else if(name.equals("csm_uri")) {
                    csmAppInfo.CSMURI = jsonReader.nextString();
                }
                else if(name.equals("play_store_url")) {
                    csmAppInfo.playStoreURL = jsonReader.nextString();
                }
                else if(name.equals("app_package_name")) {
                    csmAppInfo.appPackageName= jsonReader.nextString();
                }
                else if(name.equals("parental_guidances")) {
                    csmAppInfo.parentalGuidances = readParentalGuidance(jsonReader);
                }
                else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return csmAppInfo;
    }

    private CSMParentalGuidance readParentalGuidance(JsonReader jsonReader) {
        CSMParentalGuidance pg = new CSMParentalGuidance();
        /*
        "playability":  { ... },
        "violence":     { ... },
        "sex":          { ... },
        "language":     { ... },
        "consumerism":  { ... },
        "drugs":        { ... },
        "educational":  { ... }
        */

        try{
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if(name.equals("playability")) {
                    pg.setPlayability(readGuideValues(jsonReader));
                }
                else if(name.equals("violence")) {
                    pg.setViolence(readGuideValues(jsonReader));
                }
                else if(name.equals("sex")) {
                    pg.setSex(readGuideValues(jsonReader));
                }
                else if(name.equals("language")) {
                    pg.setLanguage(readGuideValues(jsonReader));
                }
                else if(name.equals("consumerism")) {
                    pg.setConsumerism(readGuideValues(jsonReader));
                }
                else if(name.equals("drugs")) {
                    pg.setDrugs(readGuideValues(jsonReader));
                }
                else if(name.equals("educational")) {
                    pg.setEducational(readGuideValues(jsonReader));
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return pg;
    }

    private CSMParentalGuidance.Guidance readGuideValues(JsonReader jsonReader) {
        CSMParentalGuidance.Guidance guidance = new CSMParentalGuidance.Guidance();
        try {
            /*
                "rating": 0,
                "description": "No Rating"
             */
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if(name.equals("rating")) {
                    guidance.rating = jsonReader.nextInt();
                }
                else if(name.equals("description")) {
                    guidance.description = jsonReader.nextString();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch(IOException exc) {

        }
        return guidance;
    }

}

