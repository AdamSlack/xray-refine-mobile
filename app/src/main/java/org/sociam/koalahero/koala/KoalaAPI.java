package org.sociam.koalahero.koala;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.sociam.koalahero.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class KoalaAPI {
    private static KoalaAPI INSTANCE;

    private KoalaAPI() {

    }

    public static KoalaAPI getInstance() {
        if (INSTANCE == null) {
            return new KoalaAPI();
        }
        return INSTANCE;
    }

    public static class KoalaRegisterRequest extends AsyncTask<RegistrationDetails, Void, TokenResponse> {
        private Function<TokenResponse, Void> completionFunction;
        private Context context;

        private KoalaRegisterRequest() {}

        public KoalaRegisterRequest(Function<TokenResponse, Void> completionFunction, Context context) {
            this.completionFunction = completionFunction;
            this.context = context;
        }
        @Override
        protected TokenResponse  doInBackground(RegistrationDetails... regDetails) {
            RegistrationDetails registrationDetails = regDetails[0];
            TokenResponse tokenResponse = new TokenResponse();
            // Request Registration from Koala API.


            return tokenResponse;
        }

        @Override
        protected void onPostExecute(TokenResponse tokenResponse) {
            super.onPostExecute(tokenResponse);
            // Do the func with the deets.
            this.completionFunction.apply(tokenResponse);
        }
    }

    public static class KoalaLoginRequest extends AsyncTask<RegistrationDetails, Void, TokenResponse> {
        private Function<TokenResponse, Void> completionFunction;
        private Context context;

        private KoalaLoginRequest() {}

        public KoalaLoginRequest(Function<TokenResponse, Void> completionFunction, Context context) {
            this.completionFunction = completionFunction;
            this.context = context;
        }
        @Override
        protected TokenResponse  doInBackground(RegistrationDetails... regDetails) {
            RegistrationDetails registrationDetails = regDetails[0];
            TokenResponse tokenResponse = new TokenResponse();
            // Request Registration from Koala API.
            try {
                URL endpoint = new URL(context.getString(R.string.xray_koala_auth));

                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setDoInput (true);
                conn.setDoOutput (true);
                conn.setRequestProperty("User-Agent", "org.sociam.koalahero");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                JSONObject jsonAuth = new JSONObject();
                jsonAuth.put("study_id", registrationDetails.study_id);
                jsonAuth.put("password", registrationDetails.password);

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream ());
                outputStream .writeBytes(jsonAuth.toString());
                outputStream .flush ();
                outputStream .close ();

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    KoalaJsonParser parser = new KoalaJsonParser();
                    tokenResponse = parser.parseTokenResponse(jr);
                    isr.close();
                }
                conn.disconnect();
            }
            catch(MalformedURLException exc) {

            }
            catch (IOException exc) {

            }
            catch(JSONException exc) {

            }
            return tokenResponse;
        }

        @Override
        protected void onPostExecute(TokenResponse tokenResponse) {
            super.onPostExecute(tokenResponse);
            // Do the func with the deets.
            this.completionFunction.apply(tokenResponse);
        }
    }


}
