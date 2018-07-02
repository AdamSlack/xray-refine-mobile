package org.sociam.koalahero.xray;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;

public class XRayAppInfo {
    public String title;
    public String app;
    public String iconURI;
    public Drawable icon;
    public XRayAppStoreInfo appStoreInfo;
    public ArrayList<String> hosts;
    public DeveloperInfo developerInfo;

    public XRayAppInfo() {
        title = "";
        app = "";
        appStoreInfo = new XRayAppStoreInfo();
        hosts = new ArrayList<>();
    }

    public XRayAppInfo(String title, String app) {
        this.title = title;
        this.app = app;
        this.appStoreInfo = new XRayAppStoreInfo(title);
        this.hosts = new ArrayList<>();
        this.developerInfo = new DeveloperInfo();
    }

    public XRayAppInfo(String app, XRayAppStoreInfo appStoreInfo, ArrayList<String> hosts) {
        this.app = app;
        this.title = appStoreInfo.title;
        this.appStoreInfo = appStoreInfo;
        this.hosts = hosts;
        this.developerInfo = new DeveloperInfo();
    }

    public XRayAppInfo(String title, String app, XRayAppStoreInfo appStoreInfo) {
        this.title = title;
        this.app = app;
        this.appStoreInfo = appStoreInfo;
        this.hosts = new ArrayList<>();
        this.developerInfo = new DeveloperInfo();
    }
}

