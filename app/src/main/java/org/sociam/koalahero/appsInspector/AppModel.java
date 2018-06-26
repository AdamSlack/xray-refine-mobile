package org.sociam.koalahero.appsInspector;

import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class AppModel {

    private static final AppModel INSTANCE = new AppModel();

    public HashMap<String, XRayAppInfo> installedApps;
    public ArrayList<String> topTenAppIDs;


    private AppModel() {
        installedApps = new HashMap<String, XRayAppInfo>();
        topTenAppIDs = new ArrayList<String>();
    }

    public static AppModel getInstance() {
        if (INSTANCE == null) {
            return new AppModel();
        }
        return INSTANCE;
    }

    // Index Names For Grid View

    public void index(){
        appNames = new String[ installedApps.size() ];
        int i = 0;
        for( String key : installedApps.keySet()){
            appNames[i] = key;
            i++;
        }
    }

    public String[] appNames;

    public XRayAppInfo get( int index ){
        return installedApps.get(appNames[index]);
    }
}
