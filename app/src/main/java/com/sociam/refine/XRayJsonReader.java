package com.sociam.refine;

import android.util.JsonReader;
import android.util.JsonToken;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class XRayJsonReader {

    public List<XRayApp> readAppArray(InputStreamReader ISR) {
        JsonReader jsonReader = new JsonReader(ISR);
        List<XRayApp> apps = new ArrayList<XRayApp>();

        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                apps.add(readApp(jsonReader));
            }
            jsonReader.endArray();
        }
        catch (IOException exc) {
            // Failed To Read.
        }
        return apps;
    }

    private XRayApp readApp(JsonReader jsonReader) {
        XRayApp xRayApp = new XRayApp();

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {

                // Check next JSON token is a Name...
                String name = "";
                if (jsonReader.peek() == JsonToken.NAME) {
                    name = jsonReader.nextName();
                }

                if(name.equals("title")) {
                    xRayApp.title = jsonReader.nextString();
                }
                else if (name.equals("app")) {
                    xRayApp.app = jsonReader.nextString();
                }
                else if (name.equals("storeinfo")){
                    xRayApp.appStoreInfo = readAppStoreInfo(jsonReader);
                }
                else if(name.equals("hosts")) {
                    xRayApp.hosts = readStringArray(jsonReader);
                }
                else if(name.equals("icon")) {
                    xRayApp.iconURI = jsonReader.nextString();
                }
                else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return xRayApp;
    }

    public ArrayList<String> readStringArray(JsonReader jsonReader) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            if(jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                while(jsonReader.hasNext()) {
                    strings.add(jsonReader.nextString());
                }
                jsonReader.endArray();
            }
        }
        catch (IOException exc) {

        }
        return strings;
    }

    private XRayAppStoreInfo readAppStoreInfo(JsonReader jsonReader) {
        XRayAppStoreInfo appStoreInfo = new XRayAppStoreInfo();
        try{
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name =jsonReader.nextName();
                if(name.equals("title")) {
                    appStoreInfo.title = jsonReader.nextString();
                }
                else if(name.equals("summary")) {
                    appStoreInfo.summary = jsonReader.nextString();
                }
                else if(name.equals("storeURL")) {
                    appStoreInfo.storeURL = jsonReader.nextString();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return appStoreInfo;
    }

    public ArrayList<CompanyDetails> readCompanyDetails(InputStreamReader ISR) {
        JsonReader jsonReader = new JsonReader(ISR);
        ArrayList<CompanyDetails> details = new ArrayList<CompanyDetails>();

        try {
            if(jsonReader.peek().equals(JsonToken.BEGIN_ARRAY)) {
                jsonReader.beginArray();
                while(jsonReader.hasNext()) {
                    details.add(readCompanyDetails(jsonReader));
                }
                jsonReader.endArray();
            }
        }
        catch (IOException exc) {

        }
        return details;
    }

    private CompanyDetails readCompanyDetails(JsonReader jsonReader) {
        CompanyDetails details = new CompanyDetails();

        try {
            if (jsonReader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                jsonReader.beginObject();
                while(jsonReader.hasNext()) {
                   if (jsonReader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                       while(jsonReader.hasNext()) {
                           if(jsonReader.nextName().equals("id")) {
                               details.companyID = jsonReader.nextString();
                           }
                           else if(jsonReader.nextName().equals("company")) {
                               details.companyName = jsonReader.nextString();
                           }
                           else if(jsonReader.nextName().equals("domains")) {
                               details.companyDomains = readStringArray(jsonReader);
                           }
                           else if (jsonReader.nextName().equals("description")) {
                               details.companyDescription = jsonReader.nextString();
                           }
                       }
                   }
                }
            }
        }
        catch (IOException exc) {

        }
        return details;
    }
}
