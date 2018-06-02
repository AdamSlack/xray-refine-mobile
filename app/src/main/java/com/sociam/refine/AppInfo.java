package com.sociam.refine;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

public class AppInfo implements Serializable {

    private String appName;
    private Drawable appIcon;
    private boolean hasIcon = false;
    private String appPackageName;

    /**
     *  Class Constructors
     */
    public AppInfo() {
        appName = "";
        appPackageName = "";
    }

    public AppInfo(String name){
        appName = name;
    }

    public AppInfo(String name, String packageName){
        appName = name;
        appPackageName = packageName;
    }

    public AppInfo(String name, String packageName, Drawable icon) {
        appName = name;
        appPackageName = packageName;
        appIcon = icon;
    }

    public AppInfo(String packageName, PackageManager packageManager) {
        appPackageName = packageName;
        try {
            appName = getAppName(packageName, packageManager);
        }
        catch (PackageManager.NameNotFoundException exc) {
            appName = "UKNOWN APP NAME";
        }
        try {
            appIcon = getAppIcon(packageName, packageManager);
            hasIcon = true;
        }
        catch (PackageManager.NameNotFoundException exc) {
            hasIcon = false;
        }
    }

    public AppInfo(ResolveInfo appResolveInfo, PackageManager packageManager) {
        appPackageName = getAppPackageName(appResolveInfo);
        try {
            appName = getAppName(appPackageName, packageManager);
        }
        catch (PackageManager.NameNotFoundException exc){
            appName = "UKNOWN APP NAME";
        }
        try {
            appIcon = getAppIcon(appPackageName, packageManager);
            hasIcon = true;
        }
        catch (PackageManager.NameNotFoundException exc) {
            hasIcon = false;
        }
    }

    /**
     * App Icon retrievers
     */
    private Drawable getAppIcon(String packageName, PackageManager packageManager) throws PackageManager.NameNotFoundException{
        return packageManager.getApplicationIcon(packageName);
    }

    private Drawable getAppIcon(ResolveInfo appResolveInfo, PackageManager packageManager) throws PackageManager.NameNotFoundException{
        String packageName = getAppPackageName(appResolveInfo);
        return getAppIcon(packageName, packageManager);
    }

    /**
     * App Name Retrievers
     */
    private String getAppName(ResolveInfo appResolveInfo, PackageManager packageManager) throws PackageManager.NameNotFoundException{
        String packageName = getAppPackageName(appResolveInfo);
        ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
        String label = (String) packageManager.getApplicationLabel(appInfo);
        return label;
    }

    private String getAppName(String packageName, PackageManager packageManager) throws PackageManager.NameNotFoundException {
        ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
        String label = (String) packageManager.getApplicationLabel(appInfo);
        return label;
    }

    /**
     * App Package Name Retriever
     */
    private String getAppPackageName(ResolveInfo appResolveInfo) {
        return appResolveInfo.activityInfo.packageName;
    }

    /**
     *  App Observatory Information Requests
     */
    private void GetAppInfo(){
        /*
            Make a HTTP Request for information about this application
            using the X-Ray App Observatory API.
         */
    }

    /**
     * Getters
     */
    public String getAppName() {
        return appName;
    }
    public String getAppPackageName() {
        return appPackageName;
    }
    public Drawable getAppIcon() {
        return  appIcon;
    }


}
