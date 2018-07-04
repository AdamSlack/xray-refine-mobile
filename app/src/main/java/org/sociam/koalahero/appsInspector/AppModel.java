package org.sociam.koalahero.appsInspector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import org.sociam.koalahero.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
        saveDisplayMode();
    }


    private boolean appModelReady = false;
    public boolean isReady() { return appModelReady; }
    public void setReady(){ appModelReady = true; }

    // List of all apps
    private HashMap<String, App> installedApps = new HashMap<String,App>();

    // Current Selected App
    public String selectedAppPackageName;

    // List of orderd package names. This can be used to convert the index into the package name
    private String[] appIndex;

    public Set<String> getInstalledAppsKeys() {
        return this.installedApps.keySet();
    }

    public List<App> getAllInstalledApps(){
        return new ArrayList<App>(installedApps.values());
    }

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

    // === Misc ===

    public int countApps( AppDisplayMode adm ){
        if( adm == AppDisplayMode.All){
            return installedApps.size();
        } else if ( adm == AppDisplayMode.TOP_TEN || adm == AppDisplayMode.SELECTED ){

            int n = 0;
            for( String key: installedApps.keySet() ){

                App a = installedApps.get(key);
                if( (adm == AppDisplayMode.TOP_TEN && a.isInTop10()) || (adm == AppDisplayMode.SELECTED && a.isSelectedToDisplay()) ){
                    n++;
                }

            }
            return n;
        }
        return 0;
    }

    public void fixData(){
        for( String key: installedApps.keySet() ){

            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(key, 0);
                String deviceTitle = (String) context.getPackageManager().getApplicationLabel(appInfo);

                installedApps.get(key).setDeviceTitle( deviceTitle );
            } catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }

        }
    }

    public void unselectAll(){

        for( String key: installedApps.keySet()){
            installedApps.get(key).setSelectedToDisplay(false);
        }

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

    private Interval sortMode = Interval.WEEK;

    public void setSortMode(Interval sortMode) {
        this.sortMode = sortMode;
        sortTopTen();
        saveSortMode();
    }
    public Interval getSortMode(){
        return sortMode;
    }

    private void sortTopTen(){

        for( String key: installedApps.keySet()) {
            installedApps.get(key).setInTop10(false);
            installedApps.get(key).setSortMode(sortMode);
        }

        List<App> apps = new ArrayList<App>(installedApps.values());
        Collections.sort(apps);
        Collections.reverse(apps);

        for( int i = 0 ; i < 10 && i < apps.size(); i++ )
            installedApps.get(apps.get(i).getPackageName()).setInTop10(true);

        index();

    }

    private String[] alphabeticalIndex;
    public void createAlphabeticalIndex(){
        List<App> apps = new ArrayList<App>(installedApps.values());
        Collections.sort(apps, new App());

        alphabeticalIndex = new String[ apps.size() ];
        for( int i = 0 ; i < alphabeticalIndex.length; i++ ){
            alphabeticalIndex[i] = apps.get(i).getxRayAppInfo().app;
        }
    }

    public String[] getAlphabeticalIndex(){
        return alphabeticalIndex;
    }

    // === Index for Grid ===
    public void index(){

        List<App> apps = new ArrayList<App>(installedApps.values());
        Collections.sort(apps);
        Collections.reverse(apps);

        if( displayMode == AppDisplayMode.All ){
            // Index all Apps
            appIndex = new String[ installedApps.size() ];

            for( int i = 0 ; i < apps.size(); i++ ){
                appIndex[i] = apps.get(i).getPackageName();
            }
        } else {
            // Index selected or apps in top 10

            List<String> appsToIndex = new ArrayList<String>();
            for( int i = 0 ; i < apps.size(); i++ ){
                String key = apps.get(i).getPackageName();

                if( (displayMode == AppDisplayMode.SELECTED && installedApps.get(key).isSelectedToDisplay())
                        || (displayMode == AppDisplayMode.TOP_TEN && installedApps.get(key).isInTop10())){
                    appsToIndex.add(key);
                }
            }

            appIndex = appsToIndex.toArray(new String[appsToIndex.size()]);

        }

    }


    public static final String APP_DISPLAY_MODE_FILENAME = "displayMode.dat";
    public static final String APP_SORT_MODE_FILENAME = "sortMode.dat";
    public static final String SELECTED_APPS_FILENAME = "selectedApps.dat";
    Context context;

    public void loadData(Context context){
        this.context = context;

        // =======
        File file = new File(context.getFilesDir(), APP_DISPLAY_MODE_FILENAME);
        if( file.exists()){

            try {
                FileInputStream in = new FileInputStream(file);
                int disByte = in.read();
                this.setDisplayMode(AppDisplayMode.values()[disByte]);
                in.close();
            } catch (IOException e){
                e.printStackTrace();
            }

        } else {
            this.setDisplayMode(AppDisplayMode.TOP_TEN);
            saveDisplayMode();
        }

        // =======
        file = new File(context.getFilesDir(), APP_SORT_MODE_FILENAME);
        if( file.exists()){

            try {
                FileInputStream in = new FileInputStream(file);
                int disByte = in.read();
                this.setSortMode(Interval.values()[disByte]);
                in.close();
            } catch (IOException e){
                e.printStackTrace();
            }

        } else {
            this.setSortMode(Interval.WEEK);
            saveSortMode();
        }

        // =======

        file = new File(context.getFilesDir(), SELECTED_APPS_FILENAME);
        if( file.exists()){

            try {
                Scanner s = new Scanner(file);
                while( s.hasNext() ){
                    String name = s.nextLine();
                    if( installedApps.get(name) != null)
                        installedApps.get(name).setSelectedToDisplay(true);
                }

                s.close();
            } catch (IOException e ){
                e.printStackTrace();
            }

        } else {
            // Set default Apps
            for( String key : installedApps.keySet()){

                App app = installedApps.get(key);
                app.setSelectedToDisplay(app.isInTop10());

            }
            saveSelectedApps();
        }
    }

    private void saveDisplayMode() {
        File file = new File(context.getFilesDir(), APP_DISPLAY_MODE_FILENAME);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(getDisplayMode().ordinal());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSortMode(){
        File file = new File(context.getFilesDir(), APP_SORT_MODE_FILENAME);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(getSortMode().ordinal());
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void saveSelectedApps(){
        File file = new File(context.getFilesDir(), SELECTED_APPS_FILENAME);
        try {
            PrintWriter pw = new PrintWriter(file);
            int n = 0;
            for( String key : installedApps.keySet() ){
                if( installedApps.get( key ).isSelectedToDisplay() ){
                    if( n > 0 ) pw.write("\n");
                    pw.write(key);
                }
                n++;
            }
            pw.close();
        } catch (IOException e) {

        }
    }

}
