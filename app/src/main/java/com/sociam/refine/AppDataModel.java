package com.sociam.refine;
/**
 * AppDataModel - Singleton
 *
 * Stores App Data describing the Apps found on the user's Phone.
 *
 * xRayApps stores XRayApp information that has been requested from the App Observatory API. In cases
 * In cases where there is no result in the App Observatory, the XRayApp will contain only the App
 * Title and the App Package Name, which were extracted from the phone.
 *
 * Tracked Phone Apps is used to allow users to pick and choose which apps are being shown in charts,
 * whilst the 'allPhoneAppInfos' is the original set of phone app data extracted from the phone and
 * is used in comparisons with the altered tracked apps map.
 *
 */

import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AppDataModel {
    private static AppDataModel INSTANCE = null;

    private HashMap<String, XRayApp> xRayApps;
    private HashMap<String, AppInfo> allPhoneAppInfos;
    public HashMap<String, AppInfo> trackedPhoneAppInfos;

    public HashMap<String, String> domainCompanyPairs;
    public HashMap<String, CompanyDetails> companyDetails;

    private AppDataModel(PackageManager packageManager, Context context) {
        xRayApps = new HashMap<String, XRayApp>();
        allPhoneAppInfos = getAppInfos(packageManager);
        trackedPhoneAppInfos = new HashMap<>(allPhoneAppInfos);

        for(String key : allPhoneAppInfos.keySet()) {
            XRayAPIService.getInstance().requestXRayAppData(allPhoneAppInfos.get(key).getAppPackageName(), context, new Function<XRayApp, Void>() {
                @Override
                public Void apply(XRayApp app) {
                    xRayApps.put(app.app, app);
                    return null;
                }
            });
        }
    }

    public static AppDataModel getInstance(PackageManager packageManager, Context context) {
        if(INSTANCE == null) {
            INSTANCE = new AppDataModel(packageManager, context);
        }
        return INSTANCE;
    }

    private List<ResolveInfo> getAppList(PackageManager packageManager) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return packageManager.queryIntentActivities( mainIntent, 0);
    }

    private HashMap<String, AppInfo> getAppInfos(PackageManager packageManager) {
        List<ResolveInfo> appList = getAppList(packageManager);
        HashMap<String, AppInfo> phoneAppInfos = new HashMap<>();
        for(ResolveInfo app : appList) {
            AppInfo appInfo = new AppInfo(app, packageManager);
            phoneAppInfos.put(appInfo.getAppPackageName(), appInfo);
        }
        return phoneAppInfos;
    }



    /**
     *
     * Getters
     *
     */
    public HashMap<String, AppInfo> getAllPhoneAppInfos() {
        return allPhoneAppInfos;
    }

    public HashMap<String, AppInfo> getTrackedPhoneAppInfos() {
        return trackedPhoneAppInfos;
    }

    public HashMap<String, XRayApp> getxRayApps() {
        return xRayApps;
    }
}
