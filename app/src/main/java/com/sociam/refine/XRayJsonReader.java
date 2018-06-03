package com.sociam.refine;

import android.util.JsonReader;
import android.util.JsonToken;

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

    public XRayApp readApp(JsonReader jsonReader) {
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

    public XRayAppStoreInfo readAppStoreInfo(JsonReader jsonReader) {
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
}
