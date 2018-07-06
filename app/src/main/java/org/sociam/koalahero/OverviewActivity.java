package org.sociam.koalahero;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.appsInspector.CountryCodeConverter;
import org.sociam.koalahero.xray.AppGenre;
import org.sociam.koalahero.xray.AppGenreHostInfo;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Overview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        appModel = AppModel.getInstance();

        int nApps = appModel.getNumberAppsToDisplay();


        for( int i = 0 ; i < nApps && i < 10 ; i++ ){

            try {

                ImageView iv = (ImageView) findViewById(getResId("icon_" + i, R.id.class));

                App app = appModel.getApp(i);
                XRayAppInfo xRayAppInfo = app.getxRayAppInfo();
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app, 0);

                // Icon
                Drawable icon = getPackageManager().getApplicationIcon(appInfo);
                iv.setImageDrawable(icon);

            } catch (PackageManager.NameNotFoundException e){

            }

        }

        for( int i = nApps; i < 10; i++ ){

            ImageView iv = (ImageView) findViewById(getResId("icon_" + i, R.id.class));
            iv.setVisibility(View.GONE);

        }

        // =============== BAR CHART ===============

        ArrayList<Integer> barValues = new ArrayList<>();
        ArrayList<String> axisLabels = new ArrayList<String>();
        Map<String,Integer> hostCountriesCount = new HashMap<String,Integer>();

        int nMax = 0;

        for( int i = 0 ; i < nApps && i < 10; i++ ){
            App a = appModel.getApp(i);

            barValues.add( a.getxRayAppInfo().hosts.size() );

            try {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(a.getxRayAppInfo().app,0);
                axisLabels.add("");
            }
            catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }

            Map<String,Integer> countryCodeCounts = a.localeCounts;
            for( String code : countryCodeCounts.keySet() ){

                if( code != null ) {
                    if (hostCountriesCount.containsKey(code.toUpperCase())) {
                        hostCountriesCount.put(code.toUpperCase(), hostCountriesCount.get(code.toUpperCase()) + countryCodeCounts.get(code));
                    } else {
                        hostCountriesCount.put(code.toUpperCase(), countryCodeCounts.get(code));
                    }
                }

            }

            if( a.getxRayAppInfo().hosts.size() > nMax) nMax = a.getxRayAppInfo().hosts.size();



        }

        XRayAPI xRayAPI = XRayAPI.getInstance();
        HashMap<AppGenre, AppGenreHostInfo> appGenreHostInfos = xRayAPI.readGenreHostInfo(getApplicationContext());


        BarData bd = this.buildBarData(barValues, axisLabels);
        this.buildHostBarChart(bd, axisLabels, (int) nMax);


        // =============== MAP ===============

        CountryCodeConverter ccC = new CountryCodeConverter();

        String rows = "";
        int n = 0;
        for (String code: hostCountriesCount.keySet()){

            if( code != "-99") {
                if (n > 0) rows += ",";
                rows += "['" + ccC.iso2CountryCodeToName(code) + "'," + hostCountriesCount.get(code) + "]";
                n++;
            }
        }

        String data = "";

        data += "<html><head><script type='text/javascript' src='https://www.google.com/jsapi'></script> <script type='text/javascript'> google.load('visualization', '1', {'packages': ['geochart']});google.setOnLoadCallback(drawRegionsMap);";
        data += " function drawRegionsMap() { var data = new google.visualization.DataTable();data.addColumn('string', 'Country');data.addColumn('number', 'Number of Hosts');data.addRows([" + rows + "]);";
        data += "var options = {colorAxis: {colors: ['#398239', '#ca0300']}};var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));chart.draw(data, options); };</script></head><body style='padding: 10px 0;'><div id='chart_div' style='width: 100%;'></div></body></html> ";

        WebView wv = (WebView) findViewById(R.id.web_view);
        //wv.loadUrl("file:///android_asset/geo.html");
        //wv.loadUrl("https://www.google.co.uk");
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
    }

    private AppModel appModel;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }



    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    private void buildHostBarChart(BarData barData, final ArrayList<String> labels, int maxValue){
        BarChart barChart = (BarChart) findViewById(R.id.bar_chart);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });

        barChart.getAxisLeft().setAxisMaximum(maxValue);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setLabelRotationAngle(90);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setLabelCount(barData.getEntryCount());
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.invalidate();
    }

    private BarData buildBarData(ArrayList<Integer> data, ArrayList<String> axisValues) {
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        for(int i=0 ; i < data.size(); i++) {
            BarEntry be = new BarEntry(barEntries.size(), data.get(i), axisValues.get(i));
            barEntries.add(be);
            }

        BarDataSet bds = new BarDataSet(barEntries, "Host Counts");
        bds.setColors(ColorTemplate.JOYFUL_COLORS);

        BarData bd = new BarData(bds);
        bd.setBarWidth(0.9f);


        return bd;
    }
}
