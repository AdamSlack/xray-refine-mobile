package org.sociam.koalahero.additionalInfoActivities;

import android.graphics.Color;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.geojson.GeoJsonFeature;
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
import java.util.Locale;

public class AdditionalInfoMapViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    private MapView mapView;
    private GoogleMap gmap;
    private GeoJsonLayer layer;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyBsrb-XjGWafuOr61to_VcnBoe1--JDyDk";

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

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        this.mapView = findViewById(R.id.mapView);
        this.mapView.onCreate(mapViewBundle);
        this.mapView.getMapAsync(this);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.gmap = map;
        try {
            this.layer = new GeoJsonLayer(gmap, R.raw.country_data, getApplicationContext());
            ArrayList<GeoJsonFeature> flaggedForRemoval = new ArrayList<>();
            for(GeoJsonFeature f : this.layer.getFeatures()) {
                String code = f.getId();
                String twoISO = ccConverter.iso3CountryCodeToIso2CountryCode(code);
                System.out.println(code + " -- " + twoISO);
                if(twoISO != null && app.localeCounts.containsKey(twoISO)) {
                    GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
                    style.setFillColor(getApplicationContext().getResources().getColor(R.color.colorRed));
                    style.setStrokeColor(getApplicationContext().getResources().getColor(R.color.colorRedDark));
                    f.setPolygonStyle(style);
                }
                else {
                    flaggedForRemoval.add(f);
                }
            }
            for(GeoJsonFeature f : flaggedForRemoval) {
                layer.removeFeature(f);
            }
            layer.addLayerToMap();
        }
        catch (IOException err) {

        }
        catch (JSONException err) {

        }
        gmap.setMinZoomPreference(1.0f);
        gmap.setMaxZoomPreference(21.0f);
        LatLng ny = new LatLng(51.5074, 0.1278);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
