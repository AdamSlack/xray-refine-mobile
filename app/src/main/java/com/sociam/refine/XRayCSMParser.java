package com.sociam.refine;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

import java.io.IOException;

public class XRayCSMParser {

    public XRayCSMApp parseCSMApp(JsonReader jsonReader) {
        XRayCSMApp csmApp = new XRayCSMApp();
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
                    csmApp.id = jsonReader.nextInt();
                }
                else if (name.equals("name")) {
                    csmApp.name = jsonReader.nextString();
                }
                else if (name.equals("age_rating")){
                    csmApp.ageRating = jsonReader.nextString();
                }
                else if(name.equals("csm_rating")) {
                    csmApp.CSMRating = jsonReader.nextInt();
                }
                else if(name.equals("one_liner")) {
                    csmApp.oneLiner = jsonReader.nextString();
                }
                else if(name.equals("csm_uri")) {
                    csmApp.CSMURI = jsonReader.nextString();
                }
                else if(name.equals("play_store_url")) {
                    csmApp.playStoreURL = jsonReader.nextString();
                }
                else if(name.equals("app_package_name")) {
                    csmApp.appPackageName= jsonReader.nextString();
                }
                else if(name.equals("parental_guidances")) {
                    csmApp.parental_guidances = readParentalGuidance(jsonReader);
                }
                else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return csmApp;
    }

    private XRayCSMParentalGuidance readParentalGuidance(JsonReader jsonReader) {
        XRayCSMParentalGuidance pg = new XRayCSMParentalGuidance();
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
                    pg.playability = readGuideValues(jsonReader);
                }
                else if(name.equals("violence")) {
                    pg.violence = readGuideValues(jsonReader);
                }
                else if(name.equals("sex")) {
                    pg.sex = readGuideValues(jsonReader);
                }
                else if(name.equals("language")) {
                    pg.language= readGuideValues(jsonReader);
                }
                else if(name.equals("consumerism")) {
                    pg.consumerism= readGuideValues(jsonReader);
                }
                else if(name.equals("drugs")) {
                    pg.drugs = readGuideValues(jsonReader);
                }
                else if(name.equals("educational")) {
                    pg.educational= readGuideValues(jsonReader);
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

    private XRayCSMParentalGuidance.Guidance readGuideValues(JsonReader jsonReader) {
        XRayCSMParentalGuidance.Guidance guidance = new XRayCSMParentalGuidance.Guidance();
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
            }
        }
        catch(IOException exc) {

        }
        return guidance;
    }

}

