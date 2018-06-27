package org.sociam.koalahero.trackerMapper;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import org.sociam.koalahero.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public void executeTrackerMapperRequest(String hostName, Function<TrackerMapperCompany, Void> onProgressFunc) {
         new TrackerMapperRequest(onProgressFunc).execute(hostName);
    }

    private class TrackerMapperRequest extends AsyncTask<String, TrackerMapperCompany, Void> {
        private Function<TrackerMapperCompany, Void> progressFunction = null;

        private TrackerMapperRequest(){}

        public TrackerMapperRequest(Function<TrackerMapperCompany, Void> progressFunction) {
            this.progressFunction = progressFunction;
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
