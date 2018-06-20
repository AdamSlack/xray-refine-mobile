package com.sociam.refine;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;

public class XRayTrackerMapperParser {

    public XRayTrackerMapperCompany parseCompany(JsonReader jsonReader) {
        XRayTrackerMapperCompany company = new XRayTrackerMapperCompany();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {

                // Check next JSON token is a Name...
                String name = "";
                if (jsonReader.peek() == JsonToken.NAME) {
                    name = jsonReader.nextName();
                }

                //{"hostName":"facebook.com","hostID":4696,"companyName":"Facebook","companyID":2846}

                if(name.equals("hostName")) {
                    company.hostName = jsonReader.nextString();
                }
                else if (name.equals("hostID")) {
                    company.hostID = jsonReader.nextInt();
                }
                else if (name.equals("companyName")){
                    company.companyName = jsonReader.nextString();
                }
                else if(name.equals("hosts")) {
                    company.companyID = jsonReader.nextInt();
                }
                else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        catch (IOException exc) {

        }
        return company;
    }

}
