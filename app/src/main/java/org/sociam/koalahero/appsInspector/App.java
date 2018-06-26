package org.sociam.koalahero.appsInspector;

import android.content.Context;

import org.sociam.koalahero.koala.KoalaData.InteractionRequestDetails;
import org.sociam.koalahero.xray.XRayAppInfo;

public class App {

    private XRayAppInfo xRayAppInfo;
    private boolean selectedToDisplay;
    private boolean inTop10;

    private long dayUsage;
    private long weekUsage;
    private long monthUsage;

    public App(XRayAppInfo xRayAppInfo, Context context){
        this.xRayAppInfo = xRayAppInfo;
        this.selectedToDisplay = false;
        this.inTop10 = false;

        this.dayUsage = AppsInspector.calculateAppTimeUsage(Interval.DAY, this.xRayAppInfo.app, context);
        this.weekUsage = AppsInspector.calculateAppTimeUsage(Interval.WEEK, this.xRayAppInfo.app, context);
        this.monthUsage = AppsInspector.calculateAppTimeUsage(Interval.MONTH, this.xRayAppInfo.app, context);
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
