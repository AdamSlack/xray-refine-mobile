package org.sociam.koalahero;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoCMSActivity;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoForParentsActivity;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoMapViewActivity;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoTrackersActivity;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.audio.AudioRecorder;
import org.sociam.koalahero.gridAdapters.AdditionalInformationAdapter;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.gridAdapters.HeightAdjustingGridView;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PerAppViewActivity extends AppCompatActivity {

    private String packageName;
    private App appInfo;
    private XRayAppInfo xRayAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_app_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Inspection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Context context = this;

        Intent prevInent = getIntent();

        AppModel appModel = AppModel.getInstance();

        AudioRecorder.getINSTANCE(this).updateRecordingUI(this);

        this.packageName = appModel.selectedAppPackageName;
        this.appInfo = appModel.getApp(packageName);
        this.xRayAppInfo = this.appInfo.getxRayAppInfo();

        TextView titleTextView = (TextView) findViewById(R.id.per_app_title);
        TextView summaryTextView = (TextView) findViewById(R.id.per_app_summary);
        ImageView iconImageView = (ImageView) findViewById(R.id.per_app_icon);
        TextView developerName = (TextView) findViewById(R.id.developerNameTextView);
        TextView downloadsNumberTextView = (TextView) findViewById(R.id.installsTextView);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.appDetailRatingBar);
        TextView pegiTextView = (TextView) findViewById(R.id.pegiRatingTextView);

        // Set Pegi Rating
        pegiTextView.setText(xRayAppInfo.appStoreInfo.contentRating);

        // Set Number of stars.
        ratingBar.setNumStars(5);
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.01f);
        ratingBar.setRating((float) xRayAppInfo.appStoreInfo.rating);

        // set Downloads
        Double installs = 0.5*(xRayAppInfo.appStoreInfo.maxInstalls + xRayAppInfo.appStoreInfo.minInstalls);
        downloadsNumberTextView.setText(NumberFormat.getNumberInstance(Locale.ENGLISH).format(installs));

        // Set Developer name.
        developerName.setText(xRayAppInfo.developerInfo.devName);

        try {
            ApplicationInfo deviceAppInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app,0);

            // App Name and Summary
            titleTextView.setText(getPackageManager().getApplicationLabel(deviceAppInfo));
            summaryTextView.setText(xRayAppInfo.appStoreInfo.summary);

            // App Icon
            Drawable icon = getPackageManager().getApplicationIcon(deviceAppInfo);
            iconImageView.setImageDrawable(icon);


        }
        catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }

        ImageView iv0 = (ImageView) findViewById(R.id.additional_option_0_image);
        ImageView iv1 = (ImageView) findViewById(R.id.additional_option_1_image);
        ImageView iv2 = (ImageView) findViewById(R.id.additional_option_2_image);
        ImageView iv3 = (ImageView) findViewById(R.id.additional_option_3_image);

        iv0.setImageDrawable(getDrawable(R.drawable.expert));
        iv1.setImageDrawable(getDrawable(R.drawable.detective));
        iv2.setImageDrawable(getDrawable(R.drawable.world));
        iv3.setImageDrawable(getDrawable(R.drawable.busts));


        ConstraintLayout cl0 = (ConstraintLayout) findViewById(R.id.additional_option_0);
        ConstraintLayout cl1 = (ConstraintLayout) findViewById(R.id.additional_option_1);
        ConstraintLayout cl2 = (ConstraintLayout) findViewById(R.id.additional_option_2);
        ConstraintLayout cl3 = (ConstraintLayout) findViewById(R.id.additional_option_3);

        cl0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdditionalInfoCMSActivity.class);
                startActivity(intent);
            }
        });

        cl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdditionalInfoTrackersActivity.class);
                startActivity(intent);
            }
        });

        cl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdditionalInfoMapViewActivity.class);
                startActivity(intent);
            }
        });

        cl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdditionalInfoForParentsActivity.class);
                startActivity(intent);
            }
        });


        // Additional Information
//        List<String> additionalInfoCategories = new ArrayList<String>(); // Add or remove as appropriate
//        additionalInfoCategories.add("Experts Say");
//        additionalInfoCategories.add("Trackers");
//        additionalInfoCategories.add("MapView");
//        additionalInfoCategories.add("ForParents");
//
//
//        final String[] categories = new String[additionalInfoCategories.size()];
//        for( int i = 0 ; i < categories.length; i++ )
//            categories[i] = additionalInfoCategories.get(i);
//
//        HeightAdjustingGridView gridview = (HeightAdjustingGridView) findViewById(R.id.additionalInfoGridView);
//        gridview.setAdapter(new AdditionalInformationAdapter(this,categories));
//
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//                System.out.println("Position: " + position);
//
//                String chosen = categories[position];
//
//                // Launch Per App View Activity
//                Intent intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                boolean start = true;
//
//                switch( chosen ){
//                    case "Experts Say":
//                        intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                        break;
//                    case "Trackers":
//                        intent = new Intent(context, AdditionalInfoTrackersActivity.class);
//                        break;
//                    case "MapView":
//                        intent = new Intent(context, AdditionalInfoMapViewActivity.class);
//                        break;
//                    case "ForParents":
//                        intent = new Intent(context, AdditionalInfoForParentsActivity.class);
//                        break;
//                    default:
//                        start = false;
//                }
//
//                if( start ){
//                    startActivity(intent);
//                }
//
//            }
//        });
    }
}
