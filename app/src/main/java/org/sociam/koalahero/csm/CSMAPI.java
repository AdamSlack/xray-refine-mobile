package org.sociam.koalahero.csm;

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

public class CSMAPI {
    private static CSMAPI INSTANCE;

    private CSMAPI() {

    }

    public static CSMAPI getInstance() {
        if (INSTANCE == null) {
            return new CSMAPI();
        }
        return INSTANCE;
    }

    public static class CSMRequest extends AsyncTask<String, CSMAppInfo, Void> {
        private Function<CSMAppInfo, Void> progressFunction = null;
        private Context context = null;

        private CSMRequest(){}

        public CSMRequest(Function<CSMAppInfo, Void> progressFunction, Context context) {
            this.progressFunction = progressFunction;
            this.context = context;
        }

        private CSMAppInfo requestCSMApp(String appPackageName) {
            CSMAppInfo csmAppInfo = new CSMAppInfo();
            try{
                URL APIEndpoint = new URL(context.getString(R.string.xray_csm_api) + appPackageName);

                // make us
                HttpURLConnection conn = (HttpURLConnection) APIEndpoint.openConnection();
                conn.setRequestProperty("User-Agent", "org.sociam.koalahero");
                conn.setRequestProperty("Accept", "application/json");

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    CSMParser parser = new CSMParser();
                    csmAppInfo = parser.parseCSMApp(jr);
                }
            }
            catch(IOException exc) {
                System.out.println(exc);
            }
            return csmAppInfo;
        }

        @Override
        protected void onProgressUpdate(CSMAppInfo... apps) {
            super.onProgressUpdate(apps);
            this.progressFunction.apply(apps[0]);
        }

        @Override
        protected Void doInBackground(String... appPackageNames) {
            int numHosts = appPackageNames.length;
            for(int i=0; i < numHosts; i++) {
                CSMAppInfo csmAppInfo = requestCSMApp(appPackageNames[i]);
                publishProgress(csmAppInfo);
            }
            return null;
        }
    }
}
