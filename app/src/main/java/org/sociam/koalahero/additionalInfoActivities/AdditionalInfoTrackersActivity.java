package org.sociam.koalahero.additionalInfoActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;

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
    }
}
