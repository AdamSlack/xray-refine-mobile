package com.sociam.refine;

import java.util.ArrayList;

public class XRayApp {
    public String title;
    public String app;
    public String iconURI;
    public XRayAppStoreInfo appStoreInfo;
    public ArrayList<String> hosts;

    public XRayApp() {
        title = "";
        app = "";
        appStoreInfo = new XRayAppStoreInfo();
        hosts = new ArrayList<>();
    }

    public XRayApp(String title, String app) {
        this.title = title;
        this.app = app;
        appStoreInfo = new XRayAppStoreInfo(title);
        hosts = new ArrayList<>();
    }

    public XRayApp(String app, XRayAppStoreInfo appStoreInfo) {
        this.app = app;
        this.title = appStoreInfo.title;
        this.appStoreInfo = appStoreInfo;
        hosts = new ArrayList<>();

    }

    public XRayApp(String title, String app, XRayAppStoreInfo appStoreInfo) {
        this.title = title;
        this.app = app;
        this.appStoreInfo = appStoreInfo;
        hosts = new ArrayList<>();
    }
}

