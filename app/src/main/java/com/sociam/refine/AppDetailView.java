package com.sociam.refine;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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
        long usageTime = calculateAppTimeUsage("day", appPackageName);
        String usageString = formatUsageTime(usageTime);
        totalUsageTextView.setText(usageString);

        appDataModel = AppDataModel.getInstance(getPackageManager(), getApplicationContext());
        setDescriptionText(appPackageName);
        loadHostsPieChart();
    }

    private long calculateAppTimeUsage(String interval, String appPackageName) {
        if(!MainActivity.checkForPermission(this)){
            return 0;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        if (interval.equals("Week")){
            calendar.add(Calendar.DATE, -7);
        }
        else if (interval.equals("Month")) {
            calendar.add(Calendar.DATE, -30);
        }
        else {
            calendar.add(Calendar.DATE, -1);
        }
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();
        System.out.println(Long.toString(start) + "  " + Long.toString(end) + "  " + Long.toString(end-start));

        Map<String, UsageStats> allStats = usageStatsManager.queryAndAggregateUsageStats(start, end);

        long totalTime;
        if(allStats.containsKey(appPackageName)){
            UsageStats stats = allStats.get(appPackageName);
            totalTime = stats.getTotalTimeInForeground();
        }
        else {
            totalTime = 0;
        }
        return totalTime;
    }

    private String formatUsageTime(long usageTime) {
        int minutes = (int) ((usageTime / (1000*60)) % 60);
        int hours   = (int) ((usageTime / (1000*60*60)) % 24);
        return Integer.toString(hours) + " Hrs, " + Integer.toString(minutes) +"min";
    }

    private void initialiseTimeWindowSpinner() {
        String[] items = new String[]{"Day", "Week", "Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long usageTime = calculateAppTimeUsage((String) dropdown.getSelectedItem(), appPackageName);
                String usageString = formatUsageTime(usageTime);
                totalUsageTextView.setText(usageString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dropdown.setSelection(0);
                long usageTime = calculateAppTimeUsage((String) dropdown.getSelectedItem(), appPackageName);
                String usageString = formatUsageTime(usageTime);
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

        String dataString = "";
        int numLegendEntries = 2;

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
