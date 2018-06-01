package com.sociam.refine;

import android.util.JsonReader;

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
        String title = "";
        String app = "";
        XRayAppStoreInfo appStoreInfo = new XRayAppStoreInfo();

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if(name.equals("title")) {
                    title = jsonReader.nextString();
                }
                else if (name.equals("app")) {
                    app = jsonReader.nextString();
                }
                else if (name.equals("storeinfo")){
                    appStoreInfo = readAppStoreInfo(jsonReader);
                }
                else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }

        if(!title.equals("")) {
            return new XRayApp(title, app);
        }
        else if (!appStoreInfo.title.equals("")) {
            return new XRayApp(app, appStoreInfo);
        }
        else return new XRayApp();
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
