package org.sociam.koalahero.appsInspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppModel {

    private static final AppModel INSTANCE = new AppModel();

    private AppModel() {
    }

    public static AppModel getInstance() {
        if (INSTANCE == null) {
            return new AppModel();
        }
        return INSTANCE;
    }


    private AppDisplayMode displayMode = AppDisplayMode.All;
    public AppDisplayMode getDisplayMode(){
        return displayMode;
    }
    public void setDisplayMode(AppDisplayMode displayMode) {
        this.displayMode = displayMode;
        index();
    }


    // List of all apps
    private HashMap<String, App> installedApps = new HashMap<String,App>();
    // List of orderd package names. This can be used to convert the index into the package name
    private String[] appIndex;


    public App getApp( String packageName ){
        return installedApps.get(packageName);
    }

    public App getApp( int index ){
        return installedApps.get(appIndex[index]);
    }

    public String getAppPackageName( int index ){
        return appIndex[index];
    }

    public int getNumberAppsToDisplay(){
        return appIndex.length;
    }

    // === Installed Apps ===
    public void addApp(App app ){
        installedApps.put( app.getPackageName(), app );
    }

    public int getTotalNumberApps(){
        return installedApps.size();
    }

    public ArrayList<String> getInstalledApps(){
        return new ArrayList<>(installedApps.keySet());
    }

    // === Top 10 Apps ===

    public ArrayList<String> getTopTen(){
        ArrayList<String> inTopTen = new ArrayList<String>();
        for( String key : installedApps.keySet() ){
            if( installedApps.get(key).isInTop10() )
                inTopTen.add(key);
        }
        return inTopTen;
    }

    // === Index for Grid ===
    public void index(){

        if( displayMode == AppDisplayMode.All ){
            // Index all Apps
            appIndex = new String[ installedApps.size() ];
            int i = 0;
            for( String key: installedApps.keySet()){
                appIndex[i] = key;
                i++;
            }
        } else {
            // Index selected or apps in top 10
            List<String> appsToIndex = new ArrayList<String>();
            for( String key: installedApps.keySet()){
                if( (displayMode == AppDisplayMode.SELECTED && installedApps.get(key).isSelectedToDisplay())
                        || (displayMode == AppDisplayMode.TOP_TEN && installedApps.get(key).isInTop10())){
                    appsToIndex.add(key);
                }
            }
            appIndex =  appsToIndex.toArray(new String[appsToIndex.size()]);

        }




    }

}
