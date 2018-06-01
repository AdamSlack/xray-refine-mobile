package com.sociam.refine;

public class XRayApp {
    public String title;
    public String app;
    public XRayAppStoreInfo appStoreInfo;

    public XRayApp() {
        title = "";
        app = "";
        appStoreInfo = new XRayAppStoreInfo();
    }

    public XRayApp(String title, String app) {
        this.title = title;
        this.app = app;
        appStoreInfo = new XRayAppStoreInfo(title);
    }

    public XRayApp(String app, XRayAppStoreInfo appStoreInfo) {
        this.app = app;
        this.title = appStoreInfo.title;
        this.appStoreInfo = appStoreInfo;
    }

    public XRayApp(String title, String app, XRayAppStoreInfo appStoreInfo) {
        this.title = title;
        this.app = app;
        this.appStoreInfo = appStoreInfo;
    }
}

