package org.sociam.koalahero.additionalInfoActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.AppModel;

public class AdditionalInfoForParentsActivity extends AppCompatActivity {

    private String packageName;
    private AppModel appModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_for_parents);

        this.appModel = AppModel.getInstance();
        this.packageName = this.appModel.selectedAppPackageName;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Inspection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
