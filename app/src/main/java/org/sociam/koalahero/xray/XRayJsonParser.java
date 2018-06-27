package org.sociam.koalahero.xray;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class XRayJsonParser {

    public List<XRayAppInfo> readAppArray(InputStreamReader ISR) {
        JsonReader jsonReader = new JsonReader(ISR);
        List<XRayAppInfo> apps = new ArrayList<XRayAppInfo>();

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

    private XRayAppInfo readApp(JsonReader jsonReader) {
        XRayAppInfo xRayAppInfo = new XRayAppInfo();

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {

                // Check next JSON token is a Name...
                String name = "";
                if (jsonReader.peek() == JsonToken.NAME) {
                    name = jsonReader.nextName();
                }

                if(name.equals("title")) {
                    xRayAppInfo.title = jsonReader.nextString();
                }
                else if (name.equals("app")) {
                    xRayAppInfo.app = jsonReader.nextString();
                }
                else if (name.equals("storeinfo")){
                    xRayAppInfo.appStoreInfo = readAppStoreInfo(jsonReader);
                }
                else if(name.equals("hosts")) {
                    xRayAppInfo.hosts = readStringArray(jsonReader);
                }
                else if(name.equals("icon")) {
                    xRayAppInfo.iconURI = jsonReader.nextString();
                }
                else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return xRayAppInfo;
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
}
