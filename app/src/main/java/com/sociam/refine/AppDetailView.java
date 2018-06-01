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

        final WebView webView = (WebView) findViewById(R.id.appGraphWebView);
        webView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                loadWebView(webView, (int) (webView.getWidth()/3.33));
            }
        });

        initialiseTimeWindowSpinner();
        long usageTime = calculateAppTimeUsage("day", appPackageName);
        String usageString = formatUsageTime(usageTime);
        totalUsageTextView.setText(usageString);

        appDataModel = AppDataModel.getInstance(getPackageManager(), getApplicationContext());
        setDescriptionText(appPackageName);
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


    private void loadWebView(WebView webView, int width) {

        String webViewContent = "<html>"
                +"<head>"
                +"<!--Load the AJAX API-->"
                +"<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"
                +"<script type=\"text/javascript\">"
                // Load the Visualization API and the corechart package.
                +"google.charts.load('current', {'packages':['corechart']});"
                +// Set a callback to run when the Google Visualization API is loaded.
                "google.charts.setOnLoadCallback(drawChart);"
                // Callback that creates and populates a data table,
                // instantiates the pie chart, passes in the data and
                // draws it.
                +"function drawChart() {"
                // Create the data table.
                +"var data = new google.visualization.DataTable();"
                +"data.addColumn('string', '3rd Party');"
                +"data.addColumn('number', 'Number of Occurences');"
                +"data.addRows(["
                +"['Google', " + Integer.toString((int)(Math.random()*10))+"],"
                +"['Facebook', "+ Integer.toString((int)(Math.random()*10))+" ],"
                +"['AdMob', "+ Integer.toString((int)(Math.random()*10))+"],"
                +"['DodgeyDave', "+ Integer.toString((int)(Math.random()*10))+"],"
                +"['Paul from down the street', "+ Integer.toString((int)(Math.random()*10))+"]"
                +"]);"
                // Set chart options
                +"var options = {'title':'Who Data is Shared With.',"
                +"'width':" + Integer.toString(400)+","
                +"'height':300};"
                // Instantiate and draw our chart, passing in some options.
                +"var chart = new google.visualization.PieChart(document.getElementById('chart_div'));"
                +"chart.draw(data, options);"
                +"}"
                +"</script>"
                +"</head>"
                +"<body>"
                +"<!--Div that will hold the pie chart-->"
                +"<div id=\"chart_div\"></div>"
                +"</body>"
                +"</html>";

        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocusFromTouch();
        webView.loadDataWithBaseURL("file://android_asset/", webViewContent, "text/html", "utf-8", null);
    }




}
