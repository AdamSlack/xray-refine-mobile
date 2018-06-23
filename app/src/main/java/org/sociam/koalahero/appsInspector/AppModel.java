package org.sociam.koalahero.appsInspector;

import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.HashMap;

public class AppModel {

    private static AppModel INSTANCE;

    public HashMap<String, XRayAppInfo> apps;

    private AppModel() {
        apps = new HashMap<String, XRayAppInfo>();
    }

    public static AppModel getInstance() {
        if (INSTANCE == null) {
            return new AppModel();
        }
        return INSTANCE;
    }
}
