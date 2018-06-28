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

public class CSMAPI {
    private static CSMAPI INSTANCE;
    private Context context;

    private CSMAPI() {

    }

    private CSMAPI(Context context) {
        this.context = context;
    }

    public static CSMAPI getInstance(Context context) {
        if (INSTANCE == null) {
            return new CSMAPI(context);
        }
        return INSTANCE;
    }


    public void exectuteCSMRequest(Function<Void, Void> completionFunction, Function<CSMAppInfo, Void> onProgressFunction, String... packageNames) {
        new CSMRequest(completionFunction, onProgressFunction, context).execute(packageNames);
    }


    private class CSMRequest extends AsyncTask<String, CSMAppInfo, Void> {
        private Function<CSMAppInfo, Void> progressFunction = null;
        private Function<Void, Void> completionFunction = null;

        private Context context = null;

        private CSMRequest(){}

        public CSMRequest(Function<Void, Void> completionFunction, Function<CSMAppInfo, Void> progressFunction, Context context) {
            this.progressFunction = progressFunction;
            this.completionFunction = completionFunction;
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
                    CSMJsonParser parser = new CSMJsonParser();
                    csmAppInfo = parser.parseCSMApp(jr);
                    isr.close();
                }
                conn.disconnect();
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
        protected void onPostExecute(Void thisIsVoid) {
            super.onPostExecute(thisIsVoid);
            this.completionFunction.apply(null);
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
