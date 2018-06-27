package org.sociam.koalahero;

import android.app.Activity;
import android.app.AppOpsManager;
import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Process;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppDisplayMode;
import org.sociam.koalahero.gridAdapters.AppAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.sociam.koalahero.PreferenceManager.PreferenceManager;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.appsInspector.AppsInspector;
import org.sociam.koalahero.csm.CSMAPI;
import org.sociam.koalahero.csm.CSMAppInfo;
import org.sociam.koalahero.koala.KoalaData.NoJSONData;
import org.sociam.koalahero.koala.KoalaAPI;
import org.sociam.koalahero.koala.KoalaData.RegistrationDetails;
import org.sociam.koalahero.koala.KoalaData.TokenResponse;
import org.sociam.koalahero.trackerMapper.TrackerMapperAPI;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int APP_USAGE_PERMISSION_INTENT = 1;

    public static String PACKAGE_NAME;
    private AppModel appModel;
    private PreferenceManager preferenceManager;
    private KoalaAPI koalaAPI;

    // UI elements
    private ProgressBar pb;
    private TextView loading_bar_message;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        this.PACKAGE_NAME = getApplicationContext().getPackageName();
        this.preferenceManager = PreferenceManager.getInstance(getApplicationContext());
        this.appModel = AppModel.getInstance();
        this.koalaAPI = KoalaAPI.getInstance();

        AppsInspector.logInteractionInfo(
                getApplicationContext(),
                "MainActivity",
                "",
                "app_launch",
                new NoJSONData()
        );


        if(!checkForUsageManagerPermission(getApplicationContext())) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),APP_USAGE_PERMISSION_INTENT);
        }
        else {
            startApp();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_CANCELED) {
            if(checkForUsageManagerPermission(getApplicationContext())) {
                startApp();
            }
            else{
                // how to handle not granting permission????
            }
        }
        else if (requestCode == APP_USAGE_PERMISSION_INTENT) {
            if(checkForUsageManagerPermission(getApplicationContext())) {
                startApp();
            }
            else{
                // how to handle not granting permission????
            }
        }
    }
    private void startApp() {
        // if no token, launch login,
        if(preferenceManager.getKoalaToken().equals("")) {
            launchLogin();
        }
        else if(appModel.getTotalNumberApps() == 0){
            beginLoading();
        }
        else{
            launchMainView();
        }
    }

    private boolean checkForUsageManagerPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void launchLogin() {
        // Once logged in and authenticated. launch loading.
        setContentView(R.layout.login_screen);
        final EditText studyIDET = (EditText) findViewById(R.id.login_studyID_et);
        final EditText passwordET = (EditText) findViewById(R.id.login_password_et);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RegistrationDetails regDeets = new RegistrationDetails(studyIDET.getText().toString(), passwordET.getText().toString());
                koalaAPI.executeKoalaLoginRequest(
                    new Function<TokenResponse, Void>() {
                         @Override
                         public Void apply(TokenResponse tokenResponse) {
                             if(!tokenResponse.token.equals("")) {
                                 preferenceManager.saveKoalaStudyID(regDeets.study_id);
                                 preferenceManager.saveKoalaToken(tokenResponse.token);
                                 beginLoading();
                             }
                             else {
                                 studyIDET.setError("Invalid Login Details");
                                 passwordET.setError("Invalid Login Details");
                             }
                             return null;
                         }
                    },
                    getApplicationContext(),
                    regDeets
                );
            }
        });
    }


    private void beginLoading() {
        setContentView(R.layout.loading_screen);
        // Set loading screen anim.


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

                        App newApp = new App(input, getApplicationContext());
                        appModel.addApp(newApp);

                        pb.setProgress(appModel.getTotalNumberApps());

                        String loading_string =
                                String.valueOf(appModel.getTotalNumberApps()) +
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


        // Get the Top Ten Apps



        // Index package names
    }

    private void launchMainView() {
        setContentView(R.layout.activity_main);

        // Log Installed and Top Ten Apps to the Database.
        AppsInspector.logInstalledAppInfo(
                getApplicationContext(),
                this.appModel.getInstalledApps(),
                this.appModel.getTopTen()
        );


        appModel.index();
        appModel.setDisplayMode(AppDisplayMode.All);

        appModel.getApp(0).setInTop10(true);
        appModel.getApp(1).setInTop10(true);
        appModel.getApp(2).setInTop10(true);
        appModel.setDisplayMode(AppDisplayMode.TOP_TEN);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + menuItem);
                        switch (menuItem.getItemId()) {

                            case R.id.nav_view_all:
                                appModel.setDisplayMode(AppDisplayMode.All);
                                break;
                            case R.id.nav_view_selected:
                                appModel.setDisplayMode(AppDisplayMode.SELECTED);
                                break;
                            case R.id.nav_view_top_10:
                                appModel.setDisplayMode(AppDisplayMode.TOP_TEN);
                                break;
                        }

                        updateGridView();

                        return true;
                    }
                });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Koala Hero");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Menu Bar Details: https://developer.android.com/training/implementing-navigation/nav-drawer


        GridView gridview = (GridView) findViewById(R.id.appGridView);
        gridview.setAdapter(new AppAdapter(this,appModel));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                displayPerAppView(appModel.getAppPackageName(position));
            }
        });
    }

    public void updateGridView(){
        GridView gridview = (GridView) findViewById(R.id.appGridView);
        gridview.setAdapter(new AppAdapter(this,appModel));
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


    private void displayPerAppView(String packageName ){

        // Launch Per App View Activity
        Intent intent = new Intent(this, PerAppViewActivity.class);
        intent.putExtra("PACKAGE_NAME", packageName );
        startActivity(intent);
    }



    // Just here to test the API consumers...
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

        TrackerMapperAPI TMAPI = TrackerMapperAPI.getInstance(getApplicationContext());
        TMAPI.executeTrackerMapperRequest(
            new Function<TrackerMapperCompany, Void>() {
                @Override
                public Void apply(TrackerMapperCompany input) {
                    System.out.print(input);
                    return null;
                }
            },
        "facebook.com"
        );
    }

}
