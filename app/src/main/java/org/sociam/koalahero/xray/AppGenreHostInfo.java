package org.sociam.koalahero.xray;

import org.sociam.koalahero.appsInspector.App;

public class AppGenreHostInfo {
    private String   category;
    public Integer  hostCount;
    public Integer  appCount;
    public double   genreAvgHosts;
    private AppGenre appGenre;

    public AppGenreHostInfo() {
        this.category = "";
        this.hostCount = -1;
        this.appCount = -1;
        this.genreAvgHosts = -1;
        this.appGenre = AppGenre.UNKNOWN;
    }

    public void setCategory(String category) {
        this.category = category;
        this.appGenre = AppGenre.valueOf(category);
    }

    public void setAppGenre(AppGenre appGenre) {
        this.appGenre = appGenre;
        this.category = appGenre.toString();
    }

    public AppGenre getAppGenre() {
        return appGenre;
    }

    public String getCategory() {
        return category;
    }
}
