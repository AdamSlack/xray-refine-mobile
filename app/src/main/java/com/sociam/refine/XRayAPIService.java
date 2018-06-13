package com.sociam.refine;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 *  Need to swap to classes of AsyncTask Extentions.
 *
 *      private class RetrieveXRayApp extends AsyncTask<String, Void, Function<XRayApp, void>> {
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
public class XRayAPIService {

    private static XRayAPIService INSTANCE;

    private XRayAPIService() {

    }

    public static XRayAPIService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new XRayAPIService();
        }
        return INSTANCE;
    }


    // This is absolute Trash.
    // please don't judge me for this, or anything else i've rushed.
    // i promise i will get round to fixing this eventually!
    private static XRayApp mapHostToDomains(XRayApp app, Context context) {
        ArrayList<String> hosts = app.hosts;
        HashMap<String, Integer> companyCounts = new HashMap<>();
        Map<String, String> hostCompanyPairs = AppDataModel.getInstance(context.getPackageManager()).domainCompanyPairs;
        for(String host : hosts) {
            boolean match = false;
            String hostName = host.toLowerCase();
            String previousHostName = "";
            while(!previousHostName.equals(hostName) && !hostName.equals("")) {
                if(hostCompanyPairs.containsKey(hostName)) {
                    match = true;
                    break;
                }
                previousHostName = hostName;
                hostName = hostName.replaceFirst("^[^.]*.", "");
            }

            if(match) {
                if(companyCounts.containsKey(hostName)) {
                    companyCounts.put(hostCompanyPairs.get(hostName), companyCounts.get(hostName) + 1);
                }
                else{
                    companyCounts.put(hostCompanyPairs.get(hostName), 1);
                }
            }
            else{
                if(companyCounts.containsKey("unknown")) {
                    companyCounts.put("unknown", companyCounts.get("unknown") + 1);
                }
                else {
                    companyCounts.put("unknown", 1);
                }
            }
        }
        app.companies = companyCounts;
        return app;
    }

    public static class XRayAppData extends AsyncTask<String, XRayApp, Void>{

        private XRayApp retrievedApp = null;

        private Context context;
        private Function<Void, Void> resultFunction;
        private Function<XRayApp, Void> progressFunction;

        private XRayAppData() {}    // Private Constructor, not to be used.

        // Constructor to be used with a function to be done on a progress update, and a function for
        // when the retrieval is complete. perhaps use one to update splash screens and one to update
        // the app data model.
        public XRayAppData(Function<Void, Void> resultFunction, Function<XRayApp, Void> progressFunction, Context context) {
            this.resultFunction = resultFunction;
            this.progressFunction = progressFunction;
            this.context = context;
        }

        // Intended to be used to add the app to the App Model.
        @Override
        protected void onProgressUpdate(XRayApp...  apps) {
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
                    String xrayAPIString = context.getResources().getString(R.string.xray_apps);
                    URL APIEndpoint = new URL(xrayAPIString + "?appId=" + appIDStrings[i] + "&isFull=true");

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) APIEndpoint.openConnection();
                    httpsURLConnection.setRequestProperty("User-Agent", "com.refine.sociam");
                    httpsURLConnection.setRequestProperty("Accept", "application/json");

                    if (httpsURLConnection.getResponseCode() == 200) {
                        InputStreamReader responseBodyReader = new InputStreamReader(httpsURLConnection.getInputStream(), "UTF-8");
                        XRayJsonReader xrayReader = new XRayJsonReader();
                        List<XRayApp> apps = xrayReader.readAppArray(responseBodyReader);
                        for (XRayApp app : apps) {
                            this.retrievedApp = mapHostToDomains(app, context);
                            publishProgress(this.retrievedApp);
                        }
                    }
                       if (this.retrievedApp == null) {
                        publishProgress(new XRayApp(appIDStrings[i], new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
                    }

                    httpsURLConnection.disconnect();
                }
            } catch (IOException exc){
                publishProgress(new XRayApp("PLEASE RESTART THE APP AND CONTACT THE DEVELOPERS.", new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
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

    static void requestXRayAltApps(final String packageName, final Context context, final Function<String, Void> callback ) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String xrayAPIString = context.getResources().getString(R.string.alt_xray_app);
                    URL APIEndpoint = new URL(xrayAPIString + "/" + packageName);
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) APIEndpoint.openConnection();

                    httpsURLConnection.setRequestProperty("User-Agent", "com.refine.sociam");
                    httpsURLConnection.setRequestProperty("Accept", "application/json");

                    if (httpsURLConnection.getResponseCode() == 200) {
                        InputStream responseBody = httpsURLConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                        XRayJsonReader xrayReader = new XRayJsonReader();
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        List<String> apps = xrayReader.readStringArray(jsonReader);
                        for (String app : apps) {
                            callback.apply(app);
                        }
                    }
                    httpsURLConnection.disconnect();
                } catch (MalformedURLException exc) {
                    // URL was Dodge

                } catch (IOException exc) {
                    // IO Failed here.
                }
            }
        });
    }

}
