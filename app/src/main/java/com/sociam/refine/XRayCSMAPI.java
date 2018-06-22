package com.sociam.refine;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class XRayCSMAPI {
    private static XRayCSMAPI INSTANCE;

    private XRayCSMAPI() {

    }

    public static XRayCSMAPI getInstance() {
        if (INSTANCE == null) {
            return new XRayCSMAPI();
        }
        return INSTANCE;
    }

    public static class CSMRequest extends AsyncTask<String, XRayCSMApp, Void> {
        private Function<XRayCSMApp, Void> progressFunction = null;
        private Context context = null;

        private CSMRequest(){}

        public CSMRequest(Function<XRayCSMApp, Void> progressFunction, Context context) {
            this.progressFunction = progressFunction;
            this.context = context;
        }

        private XRayCSMApp requestCSMApp(String hostName) {
            XRayCSMApp csmApp = new XRayCSMApp();
            try{
                URL APIEndpoint = new URL(context.getString(R.string.xray_tracker_mapper_api) + hostName);

                HttpsURLConnection conn = (HttpsURLConnection) APIEndpoint.openConnection();
                conn.setRequestProperty("User-Agent", "com.refine.sociam");
                conn.setRequestProperty("Accept", "application/json");

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    XRayCSMParser parser = new XRayCSMParser();
                    csmApp = parser.parseCSMApp(jr);
                }
            }
            catch(IOException exc) {

            }
            return csmApp;
        }

        @Override
        protected void onProgressUpdate(XRayCSMApp... apps) {
            super.onProgressUpdate(apps);
            this.progressFunction.apply(apps[0]);
        }

        @Override
        protected Void doInBackground(String... appPackageNames) {
            int numHosts = appPackageNames.length;
            for(int i=0; i < numHosts; i++) {
                XRayCSMApp csmApp = requestCSMApp(appPackageNames[i]);
                publishProgress(csmApp);
            }
            return null;
        }
    }
}
