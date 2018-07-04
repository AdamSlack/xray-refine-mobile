package org.sociam.koalahero;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppDisplayMode;
import org.sociam.koalahero.appsInspector.Interval;
import org.sociam.koalahero.audio.AudioRecorder;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final int APP_USAGE_PERMISSION_INTENT = 1;
    public static final int SETTINGS_REQUEST_CODE = 298;
    public static final int AUDIO_PERMISSION_REQUEST_CODE = 118;

    public static String PACKAGE_NAME;
    private AppModel appModel;
    private PreferenceManager preferenceManager;
    private KoalaAPI koalaAPI;

    // UI elements
    private ProgressBar pb;
    private TextView loading_bar_message;
    private DrawerLayout mDrawerLayout;

    // Audio
    private AudioRecorder audioRecorder;

    // Notifications
    public static String NOTIFICATION_CHANNEL_ID = "KoalaChannel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Create NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }




        this.PACKAGE_NAME = getApplicationContext().getPackageName();
        this.preferenceManager = PreferenceManager.getInstance(getApplicationContext());
        this.appModel = AppModel.getInstance();
        this.koalaAPI = KoalaAPI.getInstance(getApplicationContext());
        this.audioRecorder = AudioRecorder.getINSTANCE( this );

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
        } else if (requestCode == AUDIO_PERMISSION_REQUEST_CODE) {

        } else if (requestCode == SETTINGS_REQUEST_CODE) {

            if( resultCode == RESULT_OK) {

                // Handle Display Mode
                String displayMode = data.getStringExtra("DISPLAY_MODE");
                switch (displayMode){
                    case "TOP_10":
                        appModel.setDisplayMode(AppDisplayMode.TOP_TEN);
                        break;
                    case "ALL":
                        appModel.setDisplayMode(AppDisplayMode.All);
                        break;
                    case "SELECTED":
                        appModel.setDisplayMode(AppDisplayMode.SELECTED);
                        break;
                }
                // Handle Sort Mode
                String sortMode = data.getStringExtra("SORT_MODE");
                switch (sortMode){
                    case "DAY":
                        appModel.setSortMode(Interval.DAY);
                        break;
                    case "WEEK":
                        appModel.setSortMode(Interval.WEEK);
                        break;
                    case "MONTH":
                        appModel.setSortMode(Interval.MONTH);
                        break;
                    default:
                }

                updateGridView();
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


    private void loadCSMData() {
        CSMAPI csmapi = CSMAPI.getInstance(getApplicationContext());

        pb.setProgress(0);

        String loading_string =
                String.valueOf("CSM Info: " + 0) +
                        " out of " +
                        String.valueOf(appModel.getInstalledAppsKeys().size());

        loading_bar_message.setText(loading_string);
        Set<String> packageNames = appModel.getInstalledAppsKeys();
        csmapi.exectuteCSMRequest(
            // Completion Function
            new Function<Void, Void>() {
                @Override
                public Void apply(Void input) {
                    launchMainView();
                    return null;
                }
            },
            // Progress Function
            new Function<CSMAppInfo, Void>() {
                @Override
                public Void apply(CSMAppInfo input) {

                    App appInfo = appModel.getApp(input.appPackageName);
                    if(appInfo != null) {
                        appInfo.setCsmAppInfo(input);
                    }
                    pb.setProgress(pb.getProgress() + 1);

                    String loading_string = String.valueOf(
                        "CSM Info: " + pb.getProgress()) +
                        " out of " +
                        String.valueOf(appModel.getInstalledAppsKeys().size()
                    );
                    loading_bar_message.setText(loading_string);

                    return null;
                }
            },
            packageNames.toArray(new String[packageNames.size()])
        );
    }

    private class HostLoadingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    private void loadAppHostCompanyMappings() {
        TrackerMapperAPI TMAPI = TrackerMapperAPI.getInstance(getApplicationContext());




    }

    private void loadXRayAppData(final String... packageNames) {
        // Begin Query for apps installed on phone.
        new XRayAPI.XRayAppData(
                // Completion Function
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void VOID) {
                        loadCSMData();
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
                                String.valueOf("App Info: " + pb.getProgress()) +
                                        " out of " +
                                        String.valueOf(packageNames.length);

                                loading_bar_message.setText(loading_string);

                        return null;
                    }
                }
                ,
                // App Context.
                getApplicationContext()
        ).execute(packageNames);
    }

    private void beginLoading() {
        setContentView(R.layout.loading_screen);
        // Set loading screen anim.
        // Set loading screen anim.
        WebView animWebView = (WebView) findViewById(R.id.loading_screen_web_view);
        animWebView.loadUrl("file:///android_asset/Loading_icon.gif");
        animWebView.setVerticalScrollBarEnabled(false);
        animWebView.setHorizontalScrollBarEnabled(false);
        animWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v,  MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        animWebView.setBackgroundColor(Color.TRANSPARENT);

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

        loadXRayAppData(appPackageNames.toArray(new String[appPackageNames.size()]));

    }

    private void launchMainView() {
        setContentView(R.layout.activity_main);

        // Log Installed and Top Ten Apps to the Database.
        AppsInspector.logInstalledAppInfo(
                getApplicationContext(),
                this.appModel.getInstalledApps(),
                this.appModel.getTopTen()
        );


        // Microphone Permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    AUDIO_PERMISSION_REQUEST_CODE);

        }

        // Prepare AppModel
        appModel.loadData( this );
        appModel.fixData();
        appModel.index();
        appModel.createAlphabeticalIndex();
        appModel.setReady();


        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {


                        switch (menuItem.getItemId()) {

                            case R.id.nav_app_selection:
                                launchAppSelector();
                                break;
                            case R.id.nav_recording:
                                launchAudioRecordingMenu();
                                break;
                        }

                        mDrawerLayout.closeDrawers();

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
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                launchPerAppView(appModel.getAppPackageName(position));
            }
        });

        appAdapter = new AppAdapter(this,appModel);
        gridview.setAdapter(appAdapter);

        updateGridView();


        if( isFirstLaunch() ) {
            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.content_frame);
            Snackbar snackbar = Snackbar
                    .make(cl, "Not happy with the top 10 apps listed?", Snackbar.LENGTH_LONG)
                    .setAction("Customise", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchAppSelector();
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
            snackbar.setDuration(10000);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.colorSecondary));

            snackbar.show();
        }

    }

    private AppAdapter appAdapter;
    public void updateGridView(){

        TextView message = (TextView) findViewById( R.id.noAppsMessage );
        if( appAdapter.getCount() == 0 ) message.setVisibility(View.VISIBLE);
        else message.setVisibility(View.INVISIBLE);

        appAdapter.notifyDataSetChanged();

        TextView displayMode = (TextView) findViewById(R.id.display_mode);
        switch ( appModel.getDisplayMode() ){
            case SELECTED:
                displayMode.setText("Showing Selected");
                break;
            case TOP_TEN:
                displayMode.setText("Showing Top 10");
                break;
            case All:
                displayMode.setText("Showing All");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                launchSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void launchPerAppView(String packageName ){

        appModel.selectedAppPackageName = packageName;
        // Launch Per App View Activity
        Intent intent = new Intent(this, PerAppViewActivity.class);
        //intent.putExtra("PACKAGE_NAME", packageName );

        startActivity(intent);
    }

    private void launchSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent,SETTINGS_REQUEST_CODE);
    }

    private void launchAppSelector(){
        Intent intent = new Intent( this ,AppSelectorActivity.class);
        startActivity(intent);
    }

    private void launchAudioRecordingMenu(){
        Intent intent = new Intent( this ,AudioRecordingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if( appModel.isReady() ) {
            updateGridView();
        }

        audioRecorder.updateRecordingUI(this);
    }


    public boolean isFirstLaunch(){

        File file = new File(this.getFilesDir(), "firstLaunch.dat");
        if( file.exists()){
            return false;
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(1);
            out.close();
        } catch (IOException e){}

        return true;
    }
}
