package org.sociam.koalahero.koala;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.sociam.koalahero.R;
import org.sociam.koalahero.koala.KoalaData.InteractionRequestDetails;
import org.sociam.koalahero.koala.KoalaData.PhoneInfoRequestDetails;
import org.sociam.koalahero.koala.KoalaData.RegistrationDetails;
import org.sociam.koalahero.koala.KoalaData.SuccessResponse;
import org.sociam.koalahero.koala.KoalaData.TokenResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

//
//    Not In Use.
//
//    public static class KoalaRegisterRequest extends AsyncTask<RegistrationDetails, Void, TokenResponse> {
//        private Function<TokenResponse, Void> completionFunction;
//        private Context context;
//
//        private KoalaRegisterRequest() {}
//
//        public KoalaRegisterRequest(Function<TokenResponse, Void> completionFunction, Context context) {
//            this.completionFunction = completionFunction;
//            this.context = context;
//        }
//        @Override
//        protected TokenResponse  doInBackground(RegistrationDetails... regDetails) {
//            RegistrationDetails registrationDetails = regDetails[0];
//            TokenResponse tokenResponse = new TokenResponse();
//            // Request Registration from Koala API.
//
//
//            return tokenResponse;
//        }
//
//        @Override
//        protected void onPostExecute(TokenResponse tokenResponse) {
//            super.onPostExecute(tokenResponse);
//            // Do the func with the deets.
//            this.completionFunction.apply(tokenResponse);
//        }
//    }

    /**
     * Creates an instance of the PhoneInformationRequest AsyncTask and executes it.
     * @param context
     * @param phoneInfoRequestDetails
     */
    public void executePhoneInformationRequest(Context context, PhoneInfoRequestDetails phoneInfoRequestDetails) {
    new PhoneInformationRequest(context).execute(phoneInfoRequestDetails);
}

    private class PhoneInformationRequest extends AsyncTask<PhoneInfoRequestDetails, Void, Void> {

        private Context context;

        private PhoneInformationRequest() {}

        public PhoneInformationRequest(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(PhoneInfoRequestDetails... phoneInfoRequestDetails) {
            PhoneInfoRequestDetails deets = phoneInfoRequestDetails[0];
            SuccessResponse res = new SuccessResponse();
            try {
                URL endpoint = new URL(context.getString(R.string.xray_koala_phone_info));

                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setDoInput (true);
                conn.setDoOutput (true);
                conn.setRequestProperty("User-Agent", "org.sociam.koalahero");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream ());
                outputStream.writeBytes(deets.toJSONData().toString());
                outputStream.flush ();
                outputStream.close ();

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    KoalaJsonParser parser = new KoalaJsonParser();
                    res = parser.parseSuccessResponse(jr);
                    isr.close();

                    System.out.println(res.success);
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
            catch(JSONException exc) {
                System.out.println("JSON Exception:" + exc.toString());
            }
            return null;
        }

    }

    /**
     * Creates an instance of the InteractionLogRequest AsyncTask and executes it.
     * @param context
     * @param interactionRequestDetails
     */
    public void executeInteractionLogRequest(Context context, InteractionRequestDetails interactionRequestDetails) {
        new InteractionLogRequest(context).execute(interactionRequestDetails);
    }

    private class InteractionLogRequest extends AsyncTask<InteractionRequestDetails, Void, Void> {

        private Context context;

        private InteractionLogRequest() {}

        public InteractionLogRequest(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(InteractionRequestDetails... interactionRequestDetails) {
            InteractionRequestDetails deets = interactionRequestDetails[0];
            SuccessResponse res = new SuccessResponse();

            try {
                URL endpoint = new URL(context.getString(R.string.xray_koala_interaction));

                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setDoInput (true);
                conn.setDoOutput (true);
                conn.setRequestProperty("User-Agent", "org.sociam.koalahero");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream ());
                outputStream.writeBytes(deets.toJSONData().toString());
                outputStream.flush ();
                outputStream.close ();

                if(conn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    JsonReader jr = new JsonReader(isr);
                    KoalaJsonParser parser = new KoalaJsonParser();
                    res = parser.parseSuccessResponse(jr);
                    isr.close();

                    System.out.println(res);
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
            catch(JSONException exc) {
                System.out.println("JSON Exception:" + exc);
            }
            return null;
        }

    }


    /**
     * Creates an instance of the KoalaLoginRequest AsyncTask and executes it.
     * @param completionFunction
     * @param context
     * @param registrationDetails
     */
    public void executeKoalaLoginRequest(Function<TokenResponse, Void> completionFunction, Context context, RegistrationDetails registrationDetails) {
        new KoalaLoginRequest(completionFunction, context).execute(registrationDetails);
    }

    private class KoalaLoginRequest extends AsyncTask<RegistrationDetails, Void, TokenResponse> {
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

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream ());
                outputStream .writeBytes(registrationDetails.toJSONData().toString());
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
