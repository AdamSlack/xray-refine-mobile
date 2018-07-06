package org.sociam.koalahero.additionalInfoActivities;

import android.graphics.Color;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonGeometryCollection;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;
import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.audio.AudioRecorder;
import org.sociam.koalahero.appsInspector.CountryCodeConverter;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Locale;
import java.util.Map;

public class AdditionalInfoMapViewActivity extends AppCompatActivity{
//    private MapView mapView;
//    private GoogleMap gmap;
//    private GeoJsonLayer layer;
//
//    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyBsrb-XjGWafuOr61to_VcnBoe1--JDyDk";

    private CountryCodeConverter ccConverter;
    private String packageName;
    private AppModel appModel;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_map_view);

        this.ccConverter = new CountryCodeConverter();
        this.appModel = AppModel.getInstance();
        this.packageName = this.appModel.selectedAppPackageName;
        this.app = this.appModel.getApp(this.packageName);

        AudioRecorder.getINSTANCE(this).updateRecordingUI(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Where In The World");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
//        }
//
//        this.mapView = findViewById(R.id.mapView);
//        this.mapView.onCreate(mapViewBundle);
//        this.mapView.getMapAsync(this);


        String rows = "";
        int n = 0;

        Map<String,Integer> countryCodeCounts = app.localeCounts;

        for (String code: countryCodeCounts.keySet()){
            //['Germany', 200]

            if( code != null && code != "-99") {
                if (n > 0) rows += ",";
                rows += "['" + ccConverter.iso2CountryCodeToName(code.toUpperCase()) + "'," + countryCodeCounts.get(code) + "]";
                n++;
            }
        }

        String data = "";

        data += "<html><head><script type='text/javascript' src='https://www.google.com/jsapi'></script> <script type='text/javascript'> google.load('visualization', '1', {'packages': ['geochart']});google.setOnLoadCallback(drawRegionsMap);";
        data += " function drawRegionsMap() { var data = new google.visualization.DataTable();data.addColumn('string', 'Country');data.addColumn('number', 'Number of Hosts');data.addRows([" + rows + "]);";
        data += "var options = {};var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));chart.draw(data, options); };</script></head><body style='padding: 10px 0;'><div id='chart_div' style='width: 100%;'></div></body></html> ";


        WebView wv = (WebView) findViewById(R.id.map_view);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");


    }

//

//    @Override
//    public void onMapReady(GoogleMap map) {
//        this.gmap = map;
//        try {
//
//            // create empty layer. I can't find out how to make an empty geo json layer :/
//            this.layer = new GeoJsonLayer(gmap, R.raw.country_data, getApplicationContext());
//            ArrayList<GeoJsonFeature> features = new ArrayList<>();
//            for(GeoJsonFeature f : this.layer.getFeatures()) {
//                GeoJsonPolygonStyle s = new GeoJsonPolygonStyle();
//                s.setFillColor(0x00000000);
//                s.setStrokeColor(0x00000000);
//                f.setPolygonStyle(s);
//                features.add(f);
//            }
//            for(GeoJsonFeature f : features) {
//                this.layer.removeFeature(f);
//            }
//
//            for(GeoJsonFeature f : features) {
//                String code = f.getId();
//                String twoISO = ccConverter.iso3CountryCodeToIso2CountryCode(code);
//                System.out.println(code + " -- " + twoISO);
//
//                if(twoISO != null && app.localeCounts.containsKey(twoISO)) {
//                    System.out.println("\n---------\n---------\n--------\n--------\n---------\n---------\n---------\nIt's a Match! " + twoISO);
//
//                    GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
//                    style.setFillColor(getApplicationContext().getResources().getColor(R.color.colorRed));
//                    style.setStrokeColor(getApplicationContext().getResources().getColor(R.color.colorRedDark));
//                    f.setPolygonStyle(style);
//
//                    this.layer.addFeature(f);
//                }
//
//            }
//
//            layer.addLayerToMap();
//        }
//        catch (IOException err) {
//            err.printStackTrace();
//        }
//        catch (JSONException err) {
//            err.printStackTrace();
//        }
//        catch (ConcurrentModificationException err) {
//            err.printStackTrace();
//        }
//        gmap.setMinZoomPreference(1.0f);
//        gmap.setMaxZoomPreference(21.0f);
//        LatLng ny = new LatLng(51.5074, 0.1278);
//        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//    @Override
//    protected void onPause() {
//        mapView.onPause();
//        super.onPause();
//    }
//    @Override
//    protected void onDestroy() {
//        mapView.onDestroy();
//        super.onDestroy();
//    }
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }

}
