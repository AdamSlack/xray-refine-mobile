package org.sociam.koalahero.appsInspector;

import android.arch.core.util.Function;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import org.sociam.koalahero.csm.CSMAPI;
import org.sociam.koalahero.csm.CSMAppInfo;
import org.sociam.koalahero.koala.KoalaData.InteractionRequestDetails;
import org.sociam.koalahero.trackerMapper.TrackerMapperAPI;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class App implements Comparable<App>,Comparator<App> {

    private XRayAppInfo xRayAppInfo;
    private boolean selectedToDisplay;
    private boolean inTop10;

    // Day, Week, and Monthly Usage times for this app.
    private java.util.Map<Interval,Long> usageTimes;

    // Mapped Company Hostname Information
    public HashMap<String, TrackerMapperCompany> companies;

    // Country Code counts.
    public HashMap<String, Integer> localeCounts;

    // Information scraped from Common Sense Media
    private CSMAppInfo csmAppInfo;

    private String deviceTitle = "";


    public App(){
    }

    public App(XRayAppInfo xRayAppInfo, Context context){
        this.xRayAppInfo = xRayAppInfo;
        this.selectedToDisplay = false;
        this.inTop10 = false;

        usageTimes = new java.util.HashMap<Interval,Long>();

        usageTimes.put(Interval.DAY,AppsInspector.calculateAppTimeUsage(Interval.DAY, this.xRayAppInfo.app, context));
        usageTimes.put(Interval.WEEK,AppsInspector.calculateAppTimeUsage(Interval.WEEK, this.xRayAppInfo.app, context));
        usageTimes.put(Interval.MONTH,AppsInspector.calculateAppTimeUsage(Interval.MONTH, this.xRayAppInfo.app, context));
        this.companies = new HashMap<>();

        this.csmAppInfo = new CSMAppInfo();

        this.localeCounts = new HashMap<>();

    }


    public CSMAppInfo getCsmAppInfo() {
        return csmAppInfo;
    }

    public void setCsmAppInfo(CSMAppInfo info) {
        this.csmAppInfo = info;
    }

    public String getPackageName(){
        return xRayAppInfo.app;
    }

    public boolean isSelectedToDisplay(){
        return selectedToDisplay;
    }

    public void setSelectedToDisplay(boolean display){
        this.selectedToDisplay = display;
    }

    public void setDeviceTitle( String title){
        this.deviceTitle = title;
    }

    public String getDeviceTitle(){
        return deviceTitle;
    }

    public XRayAppInfo getxRayAppInfo() {
        return xRayAppInfo;
    }

    public boolean isInTop10() {
        return inTop10;
    }

    public void setInTop10(boolean inTop10) {
        this.inTop10 = inTop10;
    }

    public long getUsage(Interval inter){
        return usageTimes.get(inter);
    }

    private Interval sortBy = Interval.WEEK;
    public void setSortMode( Interval sort ){
        sortBy = sort;
    }

    // Sort by usage time
    @Override
    public int compareTo(App other){

        if( usageTimes.get(sortBy) < other.getUsage(sortBy)) return -1;
        if( usageTimes.get(sortBy) > other.getUsage(sortBy)) return 1;
        else return 0;

        // USAGE: Collections.sort(list);
    }

    // Sort by title
    @Override
    public int compare(App d, App d1) {

        String title0 = d.xRayAppInfo.title;
        String title1 = d1.xRayAppInfo.title;

        if( title0.equals("Unknown")) title0 = d.getDeviceTitle();
        if( title1.equals("Unknown")) title1 = d1.getDeviceTitle();

        return title0.compareTo(title1);

        // USAGE: Collections.sort(list, new App());
    }

}
