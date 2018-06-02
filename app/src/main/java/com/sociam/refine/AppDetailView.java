package com.sociam.refine;

/**
 * App Detail View - Activity
 *
 * Provides users with a view of host and usage information for a specific application, data for
 * a specific app found from the app data model. Usage information is obtained using static
 * AppUsageManager methods.
 *
 * The host pie chart is built using the MPAndroidChart library.
 *
 */

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class AppDetailView extends AppCompatActivity {

    private String appPackageName = "";
    private Spinner dropdown;
    private TextView totalUsageTextView;
    private AppDataModel appDataModel;

    // Chart Variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        TextView appNameTextView = (TextView) findViewById(R.id.appNameTextView);
        TextView appPackageNameTextView = (TextView) findViewById(R.id.appPackageNameTextView);
        ImageView appIconImageView = (ImageView) findViewById(R.id.appIconImageView);

        dropdown = findViewById(R.id.timePediodSpinner);
        totalUsageTextView = findViewById(R.id.timeUsageTextView);

        if(intent.hasExtra("appPackageName")){
            AppInfo app = new AppInfo(intent.getStringExtra("appPackageName"), getPackageManager());
            appPackageName = app.getAppPackageName();
            appNameTextView.setText(app.getAppName());
            appPackageNameTextView.setText(app.getAppPackageName());
            appIconImageView.setImageDrawable(app.getAppIcon());
        }

        initialiseTimeWindowSpinner();
        long usageTime = AppUsageManager.calculateAppTimeUsage("day", appPackageName, getApplicationContext());
        String usageString = AppUsageManager.formatUsageTime(usageTime);
        totalUsageTextView.setText(usageString);

        appDataModel = AppDataModel.getInstance(getPackageManager(), getApplicationContext());
        setDescriptionText(appPackageName);
        loadHostsPieChart();
    }

    private void initialiseTimeWindowSpinner() {
        String[] items = new String[]{"Day", "Week", "Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long usageTime = AppUsageManager.calculateAppTimeUsage((String) dropdown.getSelectedItem(), appPackageName, getApplicationContext());
                String usageString = AppUsageManager.formatUsageTime(usageTime);
                totalUsageTextView.setText(usageString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dropdown.setSelection(0);
                long usageTime = AppUsageManager.calculateAppTimeUsage((String) dropdown.getSelectedItem(), appPackageName, getApplicationContext());
                String usageString = AppUsageManager.formatUsageTime(usageTime);
                totalUsageTextView.setText(usageString);
            }
        });
    }

    private void setDescriptionText(String appPackageName) {
        TextView appDescTextView = (TextView) findViewById(R.id.appDescTextView);
        if(appDataModel.getxRayApps().containsKey(appPackageName)) {
            String summary = appDataModel.getxRayApps().get(appPackageName).appStoreInfo.summary;
            appDescTextView.setText(summary == null ? "Description Unknown" : summary);
        }
        else {
            appDescTextView.setText("Description Unknown");
        }
    }


    private void loadHostsPieChart() {

        ArrayList<String> hosts = new ArrayList<>();
        if (appDataModel.getxRayApps().containsKey(appPackageName)) {
            hosts = appDataModel.getxRayApps().get(appPackageName).hosts;
        }

        PieChart pieChart = (PieChart) findViewById(R.id.hostPieChart);
        ArrayList<PieEntry> dataEntries = new ArrayList<>();

        for(String host : hosts) {
            dataEntries.add(new PieEntry(1f, host));
        }

        PieDataSet pieDataSet = new PieDataSet(dataEntries, "Hosts Found in App.");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(pieDataSet));

    }

}
