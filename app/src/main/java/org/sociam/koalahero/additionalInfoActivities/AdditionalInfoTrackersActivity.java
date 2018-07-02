package org.sociam.koalahero.additionalInfoActivities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;
import org.sociam.koalahero.xray.AppGenre;
import org.sociam.koalahero.xray.AppGenreHostInfo;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayJsonParser;
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

public class AdditionalInfoTrackersActivity extends AppCompatActivity {

    private AppModel appModel;
    private String packageName;
    private App selectedApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_trackers);

        this.appModel = AppModel.getInstance();
        this.packageName = this.appModel.selectedAppPackageName;
        this.selectedApp = this.appModel.getApp(this.packageName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Who is Watching?");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        XRayAPI xRayAPI = XRayAPI.getInstance();

        HashMap<AppGenre, AppGenreHostInfo> appGenreHostInfos = xRayAPI.readGenreHostInfo(getApplicationContext());

        HashMap<String, TrackerMapperCompany> companyMap = this.selectedApp.companies;

        ArrayList<TrackerMapperCompany> companyNames = new ArrayList<>(companyMap.values());

        ArrayList<String> hostNames = this.selectedApp.getxRayAppInfo().hosts;

        AppGenreHostInfo thisAppsGenreInfo = appGenreHostInfos.get(this.selectedApp.getxRayAppInfo().appStoreInfo.getAppGenre());


        TextView thisAppHostCount = (TextView) findViewById(R.id.thisAppValueTextView);
        TextView genreAvgHostCount = (TextView) findViewById(R.id.genreAverageValue);
        TextView genreAvgHostLabel = (TextView) findViewById(R.id.genreAverageLabel);

        TextView appTitle = (TextView) findViewById(R.id.per_app_title);
        TextView devTitle = (TextView) findViewById(R.id.trackerDevNameTextView);

        ImageView appIcon = (ImageView) findViewById(R.id.per_app_icon);

        appTitle.setText(this.selectedApp.getxRayAppInfo().appStoreInfo.title);
        devTitle.setText(this.selectedApp.getxRayAppInfo().developerInfo.devName);

        thisAppHostCount.setText(String.valueOf(hostNames.size()));
        genreAvgHostCount.setText(String.valueOf((int) thisAppsGenreInfo.genreAvgHosts));
        genreAvgHostLabel.setText(thisAppsGenreInfo.getAppGenre().toLabel());

        // Set information read from device
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(this.selectedApp.getxRayAppInfo().app,0);

            // App Icon
            Drawable icon = getPackageManager().getApplicationIcon(appInfo);
            appIcon.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
