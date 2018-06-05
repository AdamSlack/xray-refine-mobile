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
import java.util.List;

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
                            useXRayAppCallback.apply(new XRayApp(packageName, new XRayAppStoreInfo("Unknown", "Unknown")));
                        }
                        else {
                            for (XRayApp app : apps) {
                                useXRayAppCallback.apply(app);
                            }
                        }

                    } else {
                        // Failed to connect
                        useXRayAppCallback.apply(new XRayApp(packageName, new XRayAppStoreInfo("Unknown", "Unknown")));
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
