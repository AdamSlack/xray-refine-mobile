package org.sociam.koalahero.xray;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;

import org.sociam.koalahero.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 *  Need to swap to classes of AsyncTask Extentions.
 *
 *      private class RetrieveXRayApp extends AsyncTask<String, Void, Function<XRayAppInfo, void>> {
 *
 *          ...
 *          ... doInBackground ... {
 *              // Some stuff
 *          }
 *
 *          ... onPostExecute ... {
 *              Function.apply( ... )
 *          }
 *      }
 */
public class XRayAPI {

    private static XRayAPI INSTANCE;

    private XRayAPI() {

    }

    public static XRayAPI getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new XRayAPI();
        }
        return INSTANCE;
    }

    public static class XRayAppData extends AsyncTask<String, XRayAppInfo, Void> {

        private XRayAppInfo retrievedApp = null;

        private Context context;
        private Function<Void, Void> resultFunction;
        private Function<XRayAppInfo, Void> progressFunction;

        private XRayAppData() {}    // Private Constructor, not to be used.

        // Constructor to be used with a function to be done on a progress update, and a function for
        // when the retrieval is complete. perhaps use one to update splash screens and one to update
        // the app data model.
        public XRayAppData(Function<Void, Void> resultFunction,
                           Function<XRayAppInfo, Void> progressFunction,
                           Context context) {
            this.resultFunction = resultFunction;
            this.progressFunction = progressFunction;
            this.context = context;
        }

        // Intended to be used to add the app to the App Model.
        @Override
        protected void onProgressUpdate(XRayAppInfo...  apps) {
            super.onProgressUpdate(apps);
            this.progressFunction.apply(apps[0]);
        }

        // Requests Apps for each app string passed to the method.
        @Override
        protected Void doInBackground(String... appIDStrings) {
            int numApps = appIDStrings.length;
            try {
                for( int i = 0; i < numApps; ++i) {
                    this.retrievedApp = null;
                    String xrayAPIString = context.getResources().getString(R.string.xray_apps_negi);
                    URL APIEndpoint = new URL(xrayAPIString + "?appId=" + appIDStrings[i] + "&isFull=true&limit=1");

                    HttpsURLConnection conn = (HttpsURLConnection) APIEndpoint.openConnection();
                    conn.setRequestProperty("User-Agent", "org.sociam.koalaHero");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    if (conn.getResponseCode() == 200) {
                        InputStreamReader responseBodyReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        XRayJsonParser xrayReader = new XRayJsonParser();
                        List<XRayAppInfo> apps = xrayReader.readAppArray(responseBodyReader);
                        for (XRayAppInfo app : apps) {
                            this.retrievedApp = app;
                            publishProgress(this.retrievedApp);
                        }
                    }
                       if (this.retrievedApp == null) {
                        publishProgress(new XRayAppInfo(appIDStrings[i], new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
                    }

                    conn.disconnect();
                }
            } catch (IOException exc){
                publishProgress(new XRayAppInfo("CRITICAL FAULT", new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
            }
            return null;
        }

        // Intended to be used to swap intents after the model has been built
        @Override
        protected void onPostExecute(Void thisIsVoid) {
            super.onPostExecute(thisIsVoid);
            this.resultFunction.apply(null);
        }
    }
}
