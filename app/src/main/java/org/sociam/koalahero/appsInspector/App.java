package org.sociam.koalahero.appsInspector;

import org.sociam.koalahero.xray.XRayAppInfo;

public class App {

    private XRayAppInfo xRayAppInfo;
    private boolean selectedToDisplay;
    private boolean inTop10;

    public App( XRayAppInfo xRayAppInfo ){
        this.xRayAppInfo = xRayAppInfo;
        this.selectedToDisplay = false;
        this.inTop10 = false;
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
