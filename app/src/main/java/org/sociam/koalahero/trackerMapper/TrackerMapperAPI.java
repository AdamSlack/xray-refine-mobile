package org.sociam.koalahero.trackerMapper;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.koala.KoalaJsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;

public class TrackerMapperAPI {

    private static TrackerMapperAPI INSTANCE;
    private Context context;

    private TrackerMapperAPI(Context context) {
        this.context = context;
    }

    public static TrackerMapperAPI getInstance(Context context) {
       if (INSTANCE == null) {
           return new TrackerMapperAPI(context);
       }
       return INSTANCE;
    }


    public void executeTrackerMapperBulkRequest(Function<Void, Void> onCompletionFunc,Function<ArrayList<TrackerMapperCompany>, Void> onProgressFunc, String... hostNames) {
        new TrackerMapperBulkRequest(onCompletionFunc, onProgressFunc).execute(hostNames);
    }

    public class TrackerMapperBulkRequest extends AsyncTask<String, ArrayList<TrackerMapperCompany>, Void> {

        private Function<ArrayList<TrackerMapperCompany>, Void> progressFunction = null;
        private Function<Void, Void> completionFunction = null;

        private TrackerMapperBulkRequest(){}

        public TrackerMapperBulkRequest(Function<Void, Void> completionFunction, Function<ArrayList<TrackerMapperCompany>, Void> progressFunction) {
            this.progressFunction = progressFunction;
            this.completionFunction = completionFunction;
        }

        @Override
        protected void onProgressUpdate(ArrayList<TrackerMapperCompany>... companies) {
            super.onProgressUpdate(companies);
            this.progressFunction.apply(companies[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.completionFunction.apply(null);
        }

        @Override
        protected Void doInBackground(String... appPackageNames) {
            ArrayList<TrackerMapperCompany> companies = new ArrayList<>();
            for(String appPackageName : appPackageNames) {
                try {
                    App app = AppModel.getInstance().getApp(appPackageName);
                    URL APIEndpoint = new URL(context.getString(R.string.xray_tracker_mapper_bulk_api));

                    HttpURLConnection conn = (HttpURLConnection) APIEndpoint.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("User-Agent", "org.sociam.koalahero");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.connect();

                    JSONArray jsonArray = new JSONArray(app.getxRayAppInfo().hosts);
                    JSONObject requestJSON = new JSONObject();
                    requestJSON.put("host_names", jsonArray);
                    DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                    outputStream.writeBytes(requestJSON.toString());
                    outputStream.flush();
                    outputStream.close();

                    if (conn.getResponseCode() == 200) {
                        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                        JsonReader jr = new JsonReader(isr);
                        TrackerMapperJsonParser tmjp = new TrackerMapperJsonParser();
                        companies = tmjp.parseCompanies(jr);

                        if(companies.size() == 0) {
                            companies.add(new TrackerMapperCompany());
                            TrackerMapperCompany c = companies.get(0);
                            c.locale = "-99";
                            c.categories.add("Unknown");
                            c.companyName = "No Known Companies in this app.";
                        }

                        for(TrackerMapperCompany c : companies) {
                            c.appPackageName = appPackageName;
                        }

                        isr.close();
                        jr.close();
                        publishProgress(companies);
                    }
                    conn.disconnect();
                } catch (MalformedURLException exc) {
                    // Handle Malformed
                    System.out.println("Malformed URL Exception:" + exc.toString());
                } catch (IOException exc) {
                    System.out.println("IO Exception:" + exc.toString());
                } catch (JSONException exc) {
                    System.out.println("JSON Exception:" + exc.toString());
                }
            }

            return null;
        }

    }

    public void executeTrackerMapperRequest(Function<Void, Void> onCompletionFunc,Function<TrackerMapperCompany, Void> onProgressFunc, String... hostNames) {
         new TrackerMapperRequest(onCompletionFunc, onProgressFunc).execute(hostNames);
    }

    private class TrackerMapperRequest extends AsyncTask<String, TrackerMapperCompany, Void> {
        private Function<TrackerMapperCompany, Void> progressFunction = null;
        private Function<Void, Void> completionFunction = null;

        private TrackerMapperRequest(){}

        public TrackerMapperRequest(Function<Void, Void> completionFunction, Function<TrackerMapperCompany, Void> progressFunction) {
            this.progressFunction = progressFunction;
            this.completionFunction = completionFunction;
        }

        private TrackerMapperCompany requestHostsCompany(String hostName) {
            TrackerMapperCompany company = new TrackerMapperCompany();
            try{
                URL APIEndpoint = new URL(context.getString(R.string.xray_tracker_mapper_api) + hostName);

                HttpURLConnection conn = (HttpURLConnection) APIEndpoint.openConnection();
                conn.setRequestProperty("User-Agent", "com.refine.sociam");
                conn.setRequestProperty("Accept", "application/json");

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    TrackerMapperJsonParser parser = new TrackerMapperJsonParser();
                    company = parser.parseCompany(jr);
                    isr.close();
                    jr.close();
                }
                conn.disconnect();
            }
            catch(IOException exc) {

            }
            return company;
        }

        @Override
        protected void onProgressUpdate(TrackerMapperCompany... companyNames) {
            super.onProgressUpdate(companyNames);
            this.progressFunction.apply(companyNames[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(String... hostNames) {
            int numHosts = hostNames.length;
            for(int i=0; i < numHosts; i++) {
                TrackerMapperCompany company = requestHostsCompany(hostNames[i]);
                publishProgress(company);
            }
            return null;
        }
    }
}
