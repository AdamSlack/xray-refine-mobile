package com.sociam.refine;

import android.util.JsonReader;
import android.util.JsonToken;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

    public ArrayList<CompanyDetails> readCompanyDetails(BufferedReader BR) throws IOException {
        ArrayList<CompanyDetails> companyDetails = new ArrayList<CompanyDetails>();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = BR.readLine()) != null) {
            sb.append(line);
        }
        try {
            JSONObject json = new JSONObject(sb.toString());
            Iterator<String> keyIter = json.keys();
            while(keyIter.hasNext()){
                companyDetails.add(readCompanyDetails(json.getJSONObject(keyIter.next())));
            }
        } catch (JSONException exc) {

        }
        return companyDetails;
    }

    private CompanyDetails readCompanyDetails(JSONObject json) {
        CompanyDetails details = new CompanyDetails();
        try {
            if (json.has("id")) {
                details.companyID = json.getString("id");
            }
            if(json.has("company")) {
                details.companyName = json.getString("company");
            }
            if(json.has("description")) {
                details.companyDescription = json.getString("description");
            }
            if(json.has("domains")) {
                JSONArray domainsJson = json.getJSONArray("domains");
                String[] domains = new String[domainsJson.length()];
                for (int i = 0; i < domainsJson.length(); i++) {
                    domains[i] = domainsJson.getString(i);
                }
                details.companyDomains = new ArrayList<String>(Arrays.asList(domains));
            }
        }
        catch (JSONException exc) {

        }
        return details;
    }
}
