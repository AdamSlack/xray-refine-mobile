package org.sociam.koalahero.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import org.sociam.koalahero.MainActivity;

public class PreferenceManager {
    private static PreferenceManager INSTANCE;
    private SharedPreferences preferences;

    private final String KOALA_TOKEN_STRING = ".koala_token";

    private final String KOALA_LOGIN_STUDY_ID = ".koala_email";

    private PreferenceManager() {

    }

    public PreferenceManager(Context context) {
        this.preferences = context.getSharedPreferences(MainActivity.PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (INSTANCE == null) {
            return new PreferenceManager(context);
        }
        return INSTANCE;
    }


    public void saveKoalaToken(String token) {
        this.preferences.edit().putString(MainActivity.PACKAGE_NAME + this.KOALA_TOKEN_STRING, token).apply();
    }
    public String getKoalaToken() {
        return this.preferences.getString(MainActivity.PACKAGE_NAME + this.KOALA_TOKEN_STRING, "");
    }


    public void saveKoalaStudyID(String studyID) {
        this.preferences.edit().putString(MainActivity.PACKAGE_NAME + this.KOALA_LOGIN_STUDY_ID, studyID).apply();
    }
    public String getKoalaStudyID() {
        return this.preferences.getString(MainActivity.PACKAGE_NAME + this.KOALA_LOGIN_STUDY_ID, "");
    }



}
