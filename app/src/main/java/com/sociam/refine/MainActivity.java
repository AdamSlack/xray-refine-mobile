package com.sociam.refine;
/**
 * Main Activity - Activity
 *
 * The main activity for the application, where the app starts when it is launched from the device.
 *
 * If the main activity is started without first populating the app data model, then the activity
 * uses a splash screen layout, otherwise it uses the activity_main layout.
 *
 * This activity also determines what actions are performed within the navigation drawer attached to
 * the main view.
 *
 * A Host-Usage graph is built using the MPAndroidChart library, the resulting dataset is then put
 * into the Graph Data Model, methods for building graphs should really be put in a separate
 * Graph Building Factory.
 *
*/


import android.app.AppOpsManager;
import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private AppDataModel appDataModel;
    private GraphDataModel graphDataModel;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDataModel = AppDataModel.getInstance(getPackageManager());
        graphDataModel = GraphDataModel.getInstance();

        appPreferences = AppPreferences.getInstance(getApplicationContext());
        if(appDataModel.companyDetails == null) {
            setContentView(R.layout.splash_screen);
            TextView loadingMessage = (TextView) findViewById(R.id.loadingMessage);
            loadingMessage.setText("Loading Company Details");
            RotateAnimation animation = new RotateAnimation(0f, 350f, 50f, 50f);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setDuration(700);

            new ReadCompanyDetails().execute();

        }
        else if(appDataModel.getxRayApps().keySet().size() != appDataModel.getAllPhoneAppInfos().keySet().size()) {
            appDataModel.buildXRayAppDataModel(getApplicationContext());
            setContentView(R.layout.splash_screen);
            TextView loadingMessage = (TextView) findViewById(R.id.loadingMessage);
            loadingMessage.setText("Loading Application Data");
            RotateAnimation animation = new RotateAnimation(0f, 350f, 50f, 50f);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setDuration(700);

            final ImageView splash = (ImageView) findViewById(R.id.splashIcon);
            splash.startAnimation(animation);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    while (appDataModel.getxRayApps().keySet().size() != appDataModel.getAllPhoneAppInfos().keySet().size()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException exc) {

                        }
                    }
                    appDataModel.trackedPhoneAppInfos = new HashMap<>(appDataModel.getAllPhoneAppInfos());
                    Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
        }
        else {
            setContentView(R.layout.activity_main);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Overall Host Exposure");

            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

            mDrawerLayout = findViewById(R.id.drawer_layout);

            mDrawerLayout.addDrawerListener(
                    new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            // Respond when the drawer's position changes
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {
                            // Respond when the drawer is opened
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            // Respond when the drawer is closed
                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            // Respond when the drawer motion state changes
                        }
                    }
            );

            setNavigationViewListener();
            buildUsageHostDataset();
        }

        if (!MainActivity.appHasUsageDataPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_view_apps: {
                navigateToViewApps();
                break;
            }
            case R.id.study_opt_in: {
                navigateToStudyOptIn();
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToStudyOptIn() {
        Intent navToStudy = new Intent(getApplicationContext(), PreferencesActivity.class);
        startActivity(navToStudy);
    }

    private void navigateToViewApps() {
        Intent findOwnApps = new Intent(getApplicationContext(), ListApplications.class);
        startActivity(findOwnApps);
    }

    static public boolean appHasUsageDataPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void buildUsageHostBarChart(BarData barData, ArrayList<String> axisValues){
        HorizontalBarChart hbc = (HorizontalBarChart) findViewById(R.id.hostBarChart);
        hbc.setData(barData);
        hbc.setFitBars(true);
        hbc.getXAxis().setDrawLabels(false);
        hbc.getXAxis().setDrawAxisLine(false);
        hbc.getXAxis().setDrawGridLines(false);
        hbc.zoom(1.0f,3.0f, 0, axisValues.size());
        hbc.getXAxis().setLabelCount(barData.getEntryCount());
        hbc.setScaleEnabled(false);

        hbc.invalidate();
    }

    private void buildUsageHostDataset() {

        if(graphDataModel.hostDataHorizontalDataSet != null && graphDataModel.hostDataAxisLabels != null) {
            buildUsageHostBarChart(graphDataModel.hostDataHorizontalDataSet, graphDataModel.hostDataAxisLabels);
            return;
        }

        HorizontalBarChart hbc = (HorizontalBarChart) findViewById(R.id.hostBarChart);
        if(hbc.getData() != null) {
            if(hbc.getData().getEntryCount() != 0) {
                return;
            }
        }

        HashMap<String, Long> hostTimes = new HashMap<>();
        // Get Host Usage Times
        for(String appPackageName : appDataModel.getTrackedPhoneAppInfos().keySet()) {
            long usageTime = AppUsageManager.calculateAppTimeUsage("week", appPackageName, getApplicationContext());
            if(appDataModel.getxRayApps().containsKey(appPackageName)) {
                HashMap<String, Integer> hosts = appDataModel.getxRayApps().get(appPackageName).companies;
                for(String host : hosts.keySet() ) {
                    if(hostTimes.containsKey(host)) {
                        hostTimes.put(host, hostTimes.get(host) + usageTime*hosts.get(host));
                    }
                    else{
                        hostTimes.put(host, usageTime*hosts.get(host));
                    }
                }
            }
        }

        // Filter hosts with usage time and separate time and hostname.
        ArrayList<String> hosts = new ArrayList<>();
        ArrayList<Pair<Float, Integer>> usageTimes = new ArrayList<>();

        for(String host : hostTimes.keySet()) {
            if(hostTimes.get(host) > 0) {
                usageTimes.add(new Pair<Float, Integer>((float) hostTimes.get(host), hosts.size()));
                hosts.add(host);

            }
        }

        // sort pairs out.
        Collections.sort(usageTimes, new Comparator<Pair<Float, Integer>>() {
            @Override
            public int compare(Pair<Float, Integer> o1, Pair<Float, Integer> o2) {
                return o1.first < o2.first ? -1 : o1.first.equals(o2.first)? 0 : 1;
            }
        });

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> axisValues = new ArrayList<>();

        for(Pair<Float, Integer> usageTime : usageTimes) {
            barEntries.add(new BarEntry(barEntries.size(), usageTime.first,hosts.get(usageTime.second)));
        }


        BarDataSet bds = new BarDataSet(barEntries, "Weekly Host Exposure");
        bds.setValueFormatter(new DefaultValueFormatter(0) {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return entry.getData().toString();
            }
        });
        bds.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData barData = new BarData(bds);
        barData.setBarWidth(0.9f);

        graphDataModel.hostDataHorizontalDataSet = barData;
        graphDataModel.hostDataHorizontalDataEntries = barEntries;
        graphDataModel.hostDataAxisLabels = axisValues;

        buildUsageHostBarChart(barData, axisValues);

        return;
    }

    private class ReadCompanyDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            appDataModel.domainCompanyPairs = new HashMap<String, String>();
            appDataModel.companyDetails = new HashMap<String, CompanyDetails>();
        }

        protected Void doInBackground(Void ...params){
            try {
                XRayJsonReader jsonReader = new XRayJsonReader();
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("company_details.json")));
                for (CompanyDetails c :jsonReader.readCompanyDetails(br)) {
                    appDataModel.companyDetails.put(c.companyName.toLowerCase(), c);
                    for(String domain : c.companyDomains) {
                        appDataModel.domainCompanyPairs.put(domain.toLowerCase(), c.companyName.toLowerCase());
                    }
                }

                System.out.println("Just chillin");

            }
            catch (IOException exc) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }
    }
}
