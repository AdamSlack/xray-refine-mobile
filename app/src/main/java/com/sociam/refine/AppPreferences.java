package com.sociam.refine;


import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AppPreferences implements Serializable{

    private static AppPreferences INSTANCE = null;

    public static boolean studyOptedIn = false;
    private static String studyOptedInString = "studyOptedIn";

    private static String packageName = "com.sociam.refine";

    private AppPreferences() {
    }

    private AppPreferences(AppPreferences preferences) {
        INSTANCE = preferences;
    }

    private AppPreferences(Context context) {
        getInstance();
        loadAppPreferences(context);
    }

    public static AppPreferences getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AppPreferences();
        }
        return INSTANCE;
    }

    public static AppPreferences getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new AppPreferences(context);
        }
        return INSTANCE;
    }

    public static AppPreferences getInstance(AppPreferences preferences) {
        if(INSTANCE == null) {
            INSTANCE = new AppPreferences(preferences);
        }
        return INSTANCE;
    }

    public static void saveAppPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(packageName+"."+studyOptedInString, studyOptedIn).apply();
    }

    public static void loadAppPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        studyOptedIn = preferences.getBoolean(packageName+"."+studyOptedInString, false);
    }

}
