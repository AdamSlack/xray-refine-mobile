package org.sociam.koalahero.additionalInfoActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;

import android.widget.TextView;
import android.widget.Toast;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.audio.AudioRecorder;
import org.sociam.koalahero.csm.CSMAPI;
import org.sociam.koalahero.csm.CSMAppInfo;
import org.sociam.koalahero.csm.CSMParentalGuidance;
import org.sociam.koalahero.gridAdapters.CSMGuidanceAdapter;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.ArrayList;

public class AdditionalInfoCMSActivity extends AppCompatActivity {

    private String packageName;
    private CSMAPI csmapi;
    private AppModel appModel;
    private XRayAppInfo xRayAppInfo;
    private CSMAppInfo csmAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_cms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Experts Say...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AudioRecorder.getINSTANCE(this).updateRecordingUI(this);

        final Context context = this;

        this.appModel = AppModel.getInstance();
        this.csmapi = CSMAPI.getInstance(getApplicationContext());

        this.packageName = appModel.selectedAppPackageName;
        this.xRayAppInfo = appModel.getApp(packageName).getxRayAppInfo();
        this.csmAppInfo = appModel.getApp(packageName).getCsmAppInfo();

        TextView titleTextView = (TextView) findViewById(R.id.per_app_title);
        TextView oneLinerTextView = (TextView) findViewById(R.id.oneLinerTextView);
        ImageView iconImageView = (ImageView) findViewById(R.id.per_app_icon);
        TextView ageRatingTextView = (TextView) findViewById(R.id.ageRatingValue);
        RatingBar csmRatingBar = (RatingBar) findViewById(R.id.csmRatingBar);
        TextView developerTextView = (TextView) findViewById(R.id.csmDeveloperTextView);


        // Set Rating bar values
        csmRatingBar.setNumStars(5);
        csmRatingBar.setMax(5);
        csmRatingBar.setStepSize(0.01f);
        csmRatingBar.setRating((float) this.csmAppInfo.CSMRating);

        // set developer name
        developerTextView.setText(xRayAppInfo.developerInfo.devName);

        // Set Age Rating
        ageRatingTextView.setText(this.csmAppInfo.ageRating);

        // Set information read from server.
        oneLinerTextView.setText(this.csmAppInfo.oneLiner);

        final ArrayList<CSMParentalGuidance.Guidance> guidances = new ArrayList<>(this.csmAppInfo.parentalGuidances.guidanceCategories.values());
        CSMGuidanceAdapter adapter = new CSMGuidanceAdapter(this, guidances);
        ListView guidanceListView = findViewById(R.id.guidanceRatingsListView);
        guidanceListView.setAdapter(adapter);

        guidanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog alertDialog = new AlertDialog.Builder(AdditionalInfoCMSActivity.this).create();
                alertDialog.setTitle("Additional Information");
                alertDialog.setMessage(guidances.get(position).description);
                alertDialog.setIcon(R.drawable.ic_menu);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.show();
            }
        });

        if(xRayAppInfo.icon != null) {
            iconImageView.setImageDrawable(xRayAppInfo.icon);
        }
        else {
            // Set information read from device
            try {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app, 0);
                Drawable icon = getPackageManager().getApplicationIcon(appInfo);
                iconImageView.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Set information read from device
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app, 0);
            // App Name
            titleTextView.setText(getPackageManager().getApplicationLabel(appInfo));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
