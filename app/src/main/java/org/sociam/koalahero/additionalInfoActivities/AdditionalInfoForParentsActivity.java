package org.sociam.koalahero.additionalInfoActivities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.audio.AudioRecorder;
import org.sociam.koalahero.gridAdapters.CompanyListingAdapter;
import org.sociam.koalahero.trackerMapper.TrackerMapperAPI;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;

import java.util.List;

public class AdditionalInfoForParentsActivity extends AppCompatActivity {

    private String packageName;
    private AppModel appModel;
    private App app;
    private TrackerMapperCompany[] companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_for_parents);

        this.appModel = AppModel.getInstance();
        this.packageName = this.appModel.selectedAppPackageName;
        this.app = this.appModel.getApp(this.packageName);
        this.companies = this.app.companies.values()
                .toArray(new TrackerMapperCompany[this.app.companies.values().size()]);

        AudioRecorder.getINSTANCE(this).updateRecordingUI(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Do You Know Them?");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        TextView appDev = (TextView) findViewById(R.id.csmDeveloperTextView);
        appDev.setText(this.app.getxRayAppInfo().developerInfo.devName);


        TextView appTitle = (TextView) findViewById(R.id.per_app_title);
        ImageView appIcon = (ImageView) findViewById(R.id.per_app_icon);

        // Set information read from device
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(this.packageName,0);

            // App Name
            appTitle.setText(getPackageManager().getApplicationLabel(appInfo));
            // App Icon
            Drawable icon = getPackageManager().getApplicationIcon(appInfo);
            appIcon.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ListView companyListView = (ListView) findViewById(R.id.companyListView);
        CompanyListingAdapter cla = new CompanyListingAdapter(getApplicationContext(), this.companies);
        companyListView.setAdapter(cla);

    }
}
