package org.sociam.koalahero;

import android.arch.core.util.Function;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.sociam.koalahero.appViewer.AppAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.sociam.koalahero.PreferenceManager.PreferenceManager;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.appsInspector.AppsInspector;
import org.sociam.koalahero.csm.CSMAPI;
import org.sociam.koalahero.csm.CSMAppInfo;
import org.sociam.koalahero.koala.KoalaData.AuthDetails;
import org.sociam.koalahero.koala.KoalaData.PhoneInfoRequestDetails;
import org.sociam.koalahero.koala.KoalaAPI;
import org.sociam.koalahero.koala.RegistrationDetails;
import org.sociam.koalahero.koala.KoalaData.TokenResponse;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;
    private AppModel appModel;
    private PreferenceManager preferenceManager;
    private KoalaAPI koalaAPI;

    // UI elements
    private ProgressBar pb;
    private TextView loading_bar_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.PACKAGE_NAME = getApplicationContext().getPackageName();
        this.preferenceManager = PreferenceManager.getInstance(getApplicationContext());
        this.appModel = AppModel.getInstance();
        this.koalaAPI = KoalaAPI.getInstance();

        // if no token, launch login,
        if(preferenceManager.getKoalaToken().equals("")) {
            launchLogin();
        }
        else if(appModel.apps.size() == 0){
            beginLoading();
        }
        else{
            launchMainView();
        }
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

        PhoneInfoRequestDetails pird = new PhoneInfoRequestDetails();
        pird.authDetails = new AuthDetails(preferenceManager);

        pird.phoneInfo.studyID = preferenceManager.getKoalaStudyID();
        pird.phoneInfo.installedApps = appPackageNames;
        pird.phoneInfo.topTenApps = new ArrayList<String>(appPackageNames.subList(0,10));
        pird.phoneInfo.retrievalDatetime = new Date();

        this.koalaAPI.executePhoneInformationRequest(getApplicationContext(), pird);

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
