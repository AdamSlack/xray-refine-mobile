package org.sociam.koalahero.appsInspector;

import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.HashMap;

public class AppModel {

//    private static AppModel INSTANCE;
//
//    public HashMap<String, XRayAppInfo> apps;
//
//    private AppModel() {
//        apps = new HashMap<String, XRayAppInfo>();
//    }
//
//    public static AppModel getInstance() {
//        if (INSTANCE == null) {
//            return new AppModel();
//        }
//        return INSTANCE;
//    }


    private static final AppModel INSTANCE = new AppModel();

    private AppModel() {
        apps = new HashMap<String, XRayAppInfo>();
    }

    public static AppModel getInstance() {
        if (INSTANCE == null) {
            return new AppModel();
        }
        return INSTANCE;
    }

    public HashMap<String, XRayAppInfo> apps;


    // Index Names For Grid View

    public void index(){
        appNames = new String[ apps.size() ];
        int i = 0;
        for( String key : apps.keySet()){
            appNames[i] = key;
            i++;
        }
    }

    public String[] appNames;

    public XRayAppInfo get( int index ){
        return apps.get(appNames[index]);
    }
}
