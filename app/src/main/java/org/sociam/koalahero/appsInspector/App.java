package org.sociam.koalahero.appsInspector;

import android.arch.core.util.Function;
import android.content.Context;

import org.sociam.koalahero.koala.KoalaData.InteractionRequestDetails;
import org.sociam.koalahero.trackerMapper.TrackerMapperAPI;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.HashMap;

public class App {

    private XRayAppInfo xRayAppInfo;
    private boolean selectedToDisplay;
    private boolean inTop10;

    // App Usage Information
    private long dayUsage;
    private long weekUsage;
    private long monthUsage;

    // Mapped Company Hostname Information
    public HashMap<String, TrackerMapperCompany> companies;

    public App(XRayAppInfo xRayAppInfo, Context context){
        this.xRayAppInfo = xRayAppInfo;
        this.selectedToDisplay = false;
        this.inTop10 = false;

        this.dayUsage = AppsInspector.calculateAppTimeUsage(Interval.DAY, this.xRayAppInfo.app, context);
        this.weekUsage = AppsInspector.calculateAppTimeUsage(Interval.WEEK, this.xRayAppInfo.app, context);
        this.monthUsage = AppsInspector.calculateAppTimeUsage(Interval.MONTH, this.xRayAppInfo.app, context);

        this.companies = new HashMap<>();
        this.mapXRayHostNames(context);
    }

    public void mapXRayHostNames(final Context context) {
        TrackerMapperAPI TMAPI = TrackerMapperAPI.getInstance(context);
        TMAPI.executeTrackerMapperRequest(
                new Function<TrackerMapperCompany, Void>() {
                    @Override
                    public Void apply(TrackerMapperCompany input) {
                        if(!companies.containsKey(input.companyName)) {
                            companies.put(input.companyName, input);
                        }

                        companies.get(input.companyName).occurrences += 1;

                        return null;
                    }
                },
            xRayAppInfo.hosts.toArray(new String[xRayAppInfo.hosts.size()])
        );
    }

    public String getPackageName(){
        return xRayAppInfo.app;
    }

    public boolean isSelectedToDisplay(){
        return selectedToDisplay;
    }

    public void setIsSelectedToDisplay( boolean display){
        this.selectedToDisplay = display;
    }

    public void setSelectedToDisplay( boolean selectedToDisplay){
        this.selectedToDisplay = selectedToDisplay;
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
}
