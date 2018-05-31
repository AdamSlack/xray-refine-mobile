package com.sociam.refine;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Map;

public class AppDetailView extends AppCompatActivity {

    private String appPackageName = "";
    private Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail_view);

        Intent intent = getIntent();

        TextView appNameTextView = (TextView) findViewById(R.id.appNameTextView);
        TextView appPackageNameTextView = (TextView) findViewById(R.id.appPackageNameTextView);
        ImageView appIconImageView = (ImageView) findViewById(R.id.appIconImageView);

        dropdown = findViewById(R.id.timePediodSpinner);

        if(intent.hasExtra("appPackageName")){
            AppInfo app = new AppInfo(intent.getStringExtra("appPackageName"), getPackageManager());
            appPackageName = app.getAppName();
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
        calculateAppTimeUsage("day", appPackageName);

    }

    private void calculateAppTimeUsage(String interval, String appPackageName) {
        if(!MainActivity.checkForPermission(this)){
            return;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        if (interval.equals("week")){
            calendar.add(Calendar.DATE, -7);
        }
        else if (interval.equals("month")) {
            calendar.add(Calendar.MONTH, -1);
        }
        else {
            calendar.add(Calendar.DATE, -1);
        }
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();
        Map<String, UsageStats> stats = usageStatsManager.queryAndAggregateUsageStats(start, end);

    }

    private void initialiseTimeWindowSpinner() {
        String[] items = new String[]{"Day", "Week", "Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAppTimeUsage((String) dropdown.getSelectedItem(), appPackageName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dropdown.setSelection(0);
            }
        });
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
                +"'width':" + Integer.toString(width)+","
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
