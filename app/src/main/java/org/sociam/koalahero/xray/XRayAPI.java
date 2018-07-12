package org.sociam.koalahero.xray;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONException;
import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.koala.KoalaAPI;
import org.sociam.koalahero.koala.KoalaData.AudioLogRequestDetails;
import org.sociam.koalahero.koala.KoalaData.SuccessResponse;
import org.sociam.koalahero.koala.KoalaJsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    public void executeAppSearchRequest(
            String searchTerm,
            Context c,
            Function<Void, Void> resultFunction,
            Function<ArrayList<App>, Void> progressFunction
    ) {
        new AppSearchRequest(c, resultFunction, progressFunction).execute(searchTerm);
    }

    private class AppSearchRequest extends AsyncTask<String, ArrayList<App>, Void> {

        private Context context;
        private Function<Void, Void> resultFunction;
        private Function<ArrayList<App>, Void> progressFunction;

        public AppSearchRequest(
                Context c,
                Function<Void, Void> resultFunction,
                Function<ArrayList<App>, Void> progressFunction
        ) {
            this.context = c;
            this.resultFunction = resultFunction;
            this.progressFunction = progressFunction;
        }

        private AppSearchRequest(){}

        // Intended to be used to add the app to the App Model.
        @Override
        protected void onProgressUpdate(ArrayList<App>...  apps) {
            super.onProgressUpdate(apps);
            this.progressFunction.apply(apps[0]);
        }

        // Intended to be used to swap intents after the model has been built
        @Override
        protected void onPostExecute(Void thisIsVoid) {
            super.onPostExecute(thisIsVoid);
            this.resultFunction.apply(null);
        }

        @Override
        protected Void doInBackground(String... searchTerms) {
            String searchTerm = searchTerms[0];
            try {
                ArrayList<App> searchResults = new ArrayList<>();
                String xrayAPIString = context.getResources().getString(R.string.xray_app_search);
                URL APIEndpoint = new URL(xrayAPIString + searchTerm);

                HttpsURLConnection conn = (HttpsURLConnection) APIEndpoint.openConnection();
                conn.setRequestProperty("User-Agent", "org.sociam.koalaHero");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    InputStreamReader responseBodyReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    XRayJsonParser xrayReader = new XRayJsonParser();
                    List<XRayAppInfo> apps = xrayReader.readAppArray(responseBodyReader);
                    for (XRayAppInfo app : apps) {
                        App searchResult = new App(app);
                        searchResult.getxRayAppInfo().title = app.appStoreInfo.title;
                        searchResults.add(searchResult);
                    }
                    publishProgress(searchResults);
                    responseBodyReader.close();
                }
                conn.disconnect();

            }
            catch(MalformedURLException exc) {
                // Handle Malformed
                System.out.println("Malformed URL Exception:" + exc.toString());
            }
            catch (IOException exc) {
                System.out.println("IO Exception:" + exc.toString());
            }

            return null;
        }


    }


    public HashMap<AppGenre, AppGenreHostInfo> readGenreHostInfo(Context context) {
        HashMap<AppGenre, AppGenreHostInfo> genreHostInfos = new HashMap<AppGenre, AppGenreHostInfo>();
        try {
            InputStream is = context.getResources().openRawResource(
                    context.getResources().getIdentifier("genre_host_averages",
                            "raw", context.getPackageName()));
            InputStreamReader isr = new InputStreamReader(is);
            JsonReader jr = new JsonReader(isr);
            genreHostInfos = XRayJsonParser.parseGenreHostAverages(jr);
            is.close();
            isr.close();
            jr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return genreHostInfos;
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

        // Intended to be used to swap intents after the model has been built
        @Override
        protected void onPostExecute(Void thisIsVoid) {
            super.onPostExecute(thisIsVoid);
            this.resultFunction.apply(null);
        }

        // Requests Apps for each app string passed to the method.
        @Override
        protected Void doInBackground(String... appIDStrings) {
            int numApps = appIDStrings.length;
            try {
                for( int i = 0; i < numApps; ++i) {
                    this.retrievedApp = new XRayAppInfo();
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
                            this.retrievedApp.title = this.retrievedApp.appStoreInfo.title;
                            publishProgress(this.retrievedApp);
                        }
                        responseBodyReader.close();
                    }
                    conn.disconnect();
                    if (this.retrievedApp.title.equals("")) {
                        publishProgress(new XRayAppInfo(appIDStrings[i], new XRayAppStoreInfo("Unknown", "Unknown"), new ArrayList<String>()));
                    }
                }
            } catch (IOException exc){
                publishProgress(new XRayAppInfo("CRITICAL FAULT", new XRayAppStoreInfo("CRITICAL FAULT", "CRITICAL FAULT"), new ArrayList<String>()));
            }
            return null;
        }


    }
}
