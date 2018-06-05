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
            String matchString = "";
            String hostName = host.toLowerCase();
            String hostNameShorter = hostName.replaceFirst("^[^.]*.", "");
            String hostNameEvenShorter = hostNameShorter.replaceFirst("^[^.]*.", "");

            if(hostCompanyPairs.containsKey(hostName.toLowerCase())) {
                match = true;
                matchString = hostName;
            }
            else if(hostCompanyPairs.containsKey(hostNameShorter)) {
                match = true;
                matchString = hostNameShorter;
            }
            else if(hostCompanyPairs.containsKey(hostNameEvenShorter)) {
                match =true;
                matchString = hostNameEvenShorter;
            }

            if(match) {
                if(companyCounts.containsKey(matchString)) {
                    companyCounts.put(hostCompanyPairs.get(matchString), companyCounts.get(matchString) + 1);
                }
                else{
                    companyCounts.put(hostCompanyPairs.get(matchString), 1);
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

    static void requestXRayAppData(final String packageName, final Context context, final Function<XRayApp, Void> useXRayAppCallback ) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String xrayAPIString = context.getResources().getString(R.string.xray_apps);
                    URL APIEndpoint = new URL(xrayAPIString + "?appId=" + packageName+ "&isFull=true");
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) APIEndpoint.openConnection();

                    httpsURLConnection.setRequestProperty("User-Agent", "com.refine.sociam");
                    httpsURLConnection.setRequestProperty("Accept", "application/json");

                    if (httpsURLConnection.getResponseCode() == 200) {
                        InputStream responseBody = httpsURLConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        XRayJsonReader xrayReader = new XRayJsonReader();
                        List<XRayApp> apps = xrayReader.readAppArray(responseBodyReader);
                        if (apps.size() == 0) {
                            useXRayAppCallback.apply(new XRayApp(packageName, new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
                        }
                        else {
                            for (XRayApp app : apps) {
                                XRayApp mappedApp = mapHostToDomains(app, context);
                                useXRayAppCallback.apply(mappedApp);
                            }
                        }

                    } else {
                        // Failed to connect
                        useXRayAppCallback.apply(new XRayApp(packageName, new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
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
