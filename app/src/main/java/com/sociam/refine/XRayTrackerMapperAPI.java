package com.sociam.refine;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class XRayTrackerMapperAPI {

    private static XRayTrackerMapperAPI INSTANCE;

    private XRayTrackerMapperAPI() {

    }

    public static XRayTrackerMapperAPI getInstance() {
       if (INSTANCE == null) {
           return new XRayTrackerMapperAPI();
       }
       return INSTANCE;
    }

    public static class TrackerMapperRequest extends AsyncTask<String, XRayTrackerMapperCompany, Void> {
        private Function<XRayTrackerMapperCompany, Void> progressFunction = null;
        private Context context = null;

        private TrackerMapperRequest(){}

        public TrackerMapperRequest(Function<XRayTrackerMapperCompany, Void> progressFunction, Context context) {
            this.progressFunction = progressFunction;
            this.context = context;
        }

        private XRayTrackerMapperCompany requestHostsCompany(String hostName) {
            XRayTrackerMapperCompany company = new XRayTrackerMapperCompany();
            try{
                URL APIEndpoint = new URL(context.getString(R.string.xray_tracker_mapper_api) + hostName);

                HttpsURLConnection conn = (HttpsURLConnection) APIEndpoint.openConnection();
                conn.setRequestProperty("User-Agent", "com.refine.sociam");
                conn.setRequestProperty("Accept", "application/json");

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    XRayTrackerMapperParser parser = new XRayTrackerMapperParser();
                    company = parser.parseCompany(jr);
                }
            }
            catch(IOException exc) {

            }
            return company;
        }

        @Override
        protected void onProgressUpdate(XRayTrackerMapperCompany... companyNames) {
            super.onProgressUpdate(companyNames);
            this.progressFunction.apply(companyNames[0]);
        }

        @Override
        protected Void doInBackground(String... hostNames) {
            int numHosts = hostNames.length;
            for(int i=0; i < numHosts; i++) {
                XRayTrackerMapperCompany company = requestHostsCompany(hostNames[i]);
                publishProgress(company);
            }
            return null;
        }
    }
}
