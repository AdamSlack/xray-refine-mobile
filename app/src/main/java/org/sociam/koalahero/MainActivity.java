package org.sociam.koalahero;

import android.arch.core.util.Function;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sociam.koalahero.appViewer.AppAdapter;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.appsInspector.AppsInspector;
import org.sociam.koalahero.csm.CSMAPI;
import org.sociam.koalahero.csm.CSMAppInfo;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public AppModel appModel;

    // UI elements
    private ProgressBar pb;
    private TextView loading_bar_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Start with Loading Screen Layout.
        super.onCreate(savedInstanceState);
        beginLoading();
    }

    private void beginLoading() {
        setContentView(R.layout.loading_screen);

        this.appModel = AppModel.getInstance();

        // Set loading screen anim.
        WebView animWebView = (WebView) findViewById(R.id.loading_screen_web_view);
        animWebView.loadUrl("file:///android_asset/Loading_icon.gif");
        animWebView.setBackgroundColor(Color.TRANSPARENT);
        animWebView.setVerticalScrollBarEnabled(false);
        animWebView.setHorizontalScrollBarEnabled(false);
        animWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        // Retrieve App Package Names
        final ArrayList<String> appPackageNames = AppsInspector.getInstalledApps(getPackageManager());

        // Init Progress Bar.
        pb = (ProgressBar) findViewById(R.id.loading_screen_progress_bar);
        pb.setMax(appPackageNames.size());
        pb.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

        // Init Loading Message
        loading_bar_message = (TextView) findViewById(R.id.loading_bar_message);
        String loading_string = "0 out of " + String.valueOf(appPackageNames.size());
        loading_bar_message.setText(loading_string);

        // Begin Query for apps installed on phone.
        new XRayAPI.XRayAppData(
                // Completion Function
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void VOID) {
                        launchMainView();
                        return null;
                    }
                },
                // Progress Function
                new Function<XRayAppInfo, Void>() {
                    @Override
                    public Void apply(XRayAppInfo input) {
                        appModel.apps.put(input.app, input);
                        pb.setProgress(appModel.apps.size());

                        String loading_string =
                                String.valueOf(appModel.apps.size()) +
                                " out of " +
                                String.valueOf(appPackageNames.size());

                        loading_bar_message.setText(loading_string);

                        return null;
                    }
                }
                ,
                // App Context.
                getApplicationContext()
        ).execute(appPackageNames.toArray(new String[appPackageNames.size()]));


        // Index package names
    }

    private void launchMainView() {
        setContentView(R.layout.activity_main);
        appModel.index();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Koala Hero");

        GridView gridview = (GridView) findViewById(R.id.appGridView);
        gridview.setAdapter(new AppAdapter(this,appModel));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                System.out.println("position: " + position + " Id: " + id);

                displayPerAppView(appModel.appNames[position]);
            }
        });
    }


    private void displayPerAppView(String packageName ){

        // Launch Per App View Activity
        Intent intent = new Intent(this, PerAppViewActivity.class);
        intent.putExtra("PACKAGE_NAME", packageName );
        startActivity(intent);
    }



    private void foo() {
        XRayAPI api = XRayAPI.getInstance();

        new XRayAPI.XRayAppData(
                new Function<Void, Void>(){
                    @Override
                    public Void apply(Void nothing){
                        return null;
                    }
                },
                new Function<XRayAppInfo, Void>() {
                    @Override
                    public Void apply(XRayAppInfo appInfo){
                        System.out.println(appInfo.appStoreInfo.title);
                        return null;
                    }
                },
                getApplicationContext()

        ).execute("com.linkedin.android","com.whatsapp","com.tencent.mm");

        new CSMAPI.CSMRequest(
                new Function<CSMAppInfo, Void>() {
                    @Override
                    public Void apply(CSMAppInfo csmAppInfo) {
                        System.out.println(csmAppInfo.oneLiner);
                        return null;
                    }
                },
                getApplicationContext()
        ).execute("com.linkedin.android","com.whatsapp","com.tencent.mm");
    }
}
