package com.sociam.refine;

import android.arch.core.util.Function;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SubstituteAppActivity extends AppCompatActivity {

    private AppDataModel appDataModel = null;

    private String appPackageName = null;
    private AppInfo appInfo = null;

    private ArrayList<XRayApp> altApps = new ArrayList<>();
    private ListView altAppListView;
    private AppSubstitutionAdapter altAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitute_app);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Alternative Apps");


        appDataModel = AppDataModel.getInstance(getPackageManager());
        //Get Extra..
        Intent intent = getIntent();
        if(intent.hasExtra("appPackageName")) {
            appPackageName = intent.getStringExtra("appPackageName");
            if(appDataModel.getTrackedPhoneAppInfos().containsKey(appPackageName)) {
                appInfo = appDataModel.getTrackedPhoneAppInfos().get(appPackageName);
            }
        }

        altAppListView = (ListView) findViewById(R.id.altAppListView);
        altAppAdapter = new AppSubstitutionAdapter(getApplicationContext(), altApps, appPackageName);
        altAppListView.setAdapter(altAppAdapter);

        if(appInfo != null) {
            initialiseAppFields(appInfo);
            new RequestXRayAltApps(altAppAdapter).execute(appPackageName);
        }
    }

    private class RequestXRayAltApps extends AsyncTask<String, XRayApp, Integer> {

        AppSubstitutionAdapter adapter;

        RequestXRayAltApps(AppSubstitutionAdapter adapter){
            this.adapter = adapter;
        }

        protected Integer doInBackground(String... appPackageNames){
            int count = appPackageNames.length;
            int total = 0;
            for(int i = 0; i < count; i++) {
                XRayAPIService.requestXRayAltApps(appPackageName, getApplicationContext(), new Function<String, Void>() {
                    @Override
                    public Void apply(String input) {
                        XRayAPIService.requestXRayAppData(input, getApplicationContext(), new Function<XRayApp, Void>() {
                            @Override
                            public Void apply(XRayApp input) {
                                altApps.add(input);
                                publishProgress();
                                return null;
                            }
                        });
                        return null;
                    }
                });
                total += 1;
            }
            return total;
        }

        @Override
        protected void onProgressUpdate(XRayApp... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }
    }

    private void initialiseAppFields(AppInfo appInfo) {
        TextView appTitle = (TextView) findViewById(R.id.appTitleSubTextView);
        TextView appPackageName = (TextView) findViewById(R.id.appPackageNameSubTextView);
        ImageView appIcon = (ImageView) findViewById(R.id.appIconImageView);

        appTitle.setText(appInfo.getAppName());
        appPackageName.setText(appInfo.getAppPackageName());
        appIcon.setImageDrawable(appInfo.getAppIcon());
    }
}
