package org.sociam.koalahero.koala;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;

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

    public static class KoalaRegisterRequest extends AsyncTask<RegistrationDetails, Void, AuthDetails> {
        private Function<AuthDetails, Void> completionFunction;
        private Context context;

        private KoalaRegisterRequest() {}

        public KoalaRegisterRequest(Function<AuthDetails, Void> completionFunction, Context context) {
            this.completionFunction = completionFunction;
            this.context = context;
        }
        @Override
        protected AuthDetails  doInBackground(RegistrationDetails... regDetails) {
            RegistrationDetails registrationDetails = regDetails[0];
            AuthDetails authDetails = new AuthDetails();
            // Request Registration from Koala API.


            return authDetails;
        }

        @Override
        protected void onPostExecute(AuthDetails authDetails) {
            super.onPostExecute(authDetails);
            // Do the func with the deets.
            this.completionFunction.apply(authDetails);
        }
    }

    public static class KoalaLoginRequest extends AsyncTask<RegistrationDetails, Void, AuthDetails> {
        private Function<AuthDetails, Void> completionFunction;
        private Context context;

        private KoalaLoginRequest() {}

        public KoalaLoginRequest(Function<AuthDetails, Void> completionFunction, Context context) {
            this.completionFunction = completionFunction;
            this.context = context;
        }
        @Override
        protected AuthDetails  doInBackground(RegistrationDetails... regDetails) {
            RegistrationDetails registrationDetails = regDetails[0];
            AuthDetails authDetails = new AuthDetails();
            // Request Registration from Koala API.


            return authDetails;
        }

        @Override
        protected void onPostExecute(AuthDetails authDetails) {
            super.onPostExecute(authDetails);
            // Do the func with the deets.
            this.completionFunction.apply(authDetails);
        }
    }


}
