package org.sociam.koalahero;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.gridAdapters.AdditionalInformationAdapter;
import org.sociam.koalahero.gridAdapters.AppAdapter;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.util.ArrayList;
import java.util.List;

public class PerAppViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_app_view);

        String packageName = getIntent().getStringExtra("PACKAGE_NAME");
        AppModel appModel = AppModel.getInstance();
        XRayAppInfo xRayAppInfo = appModel.installedApps.get(packageName);

        TextView titleTextView = (TextView) findViewById(R.id.per_app_title);
        TextView summaryTextView = (TextView) findViewById(R.id.per_app_summary);
        ImageView iconImageView = (ImageView) findViewById(R.id.per_app_icon);


        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app,0);

            // App Name and Summary
            titleTextView.setText(getPackageManager().getApplicationLabel(appInfo));
            summaryTextView.setText(xRayAppInfo.appStoreInfo.summary);

            // App Icon
            Drawable icon = getPackageManager().getApplicationIcon(appInfo);
            iconImageView.setImageDrawable(icon);


        }
        catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }


        // Additional Information
        List<String> additionalInfoCategories = new ArrayList<String>(); // Add or remove as appropriate
        additionalInfoCategories.add("CMS");
        additionalInfoCategories.add("Trackers");
        additionalInfoCategories.add("MapView");
        additionalInfoCategories.add("ForParents");


        String[] categories = new String[additionalInfoCategories.size()];
        for( int i = 0 ; i < categories.length; i++ )
            categories[i] = additionalInfoCategories.get(i);

        GridView gridview = (GridView) findViewById(R.id.additionalInfoGridView);
        gridview.setAdapter(new AdditionalInformationAdapter(this,categories));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                System.out.println("Position: " + position);

//                String chosen = categories[position];
//
//                // Launch Per App View Activity
//                Intent intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                boolean start = true;
//
//                switch( chosen ){
//                    case "CMS":
//                        intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                        break;
//                    case "Trackers":
//                        intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                        break;
//                    case "MapView":
//                        intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                        break;
//                    case "ForParents":
//                        intent = new Intent(context, AdditionalInfoCMSActivity.class);
//                        break;
//                    default:
//                        start = false;
//                }
//
//                if( start ){
//                    intent.putExtra("PACKAGE_NAME", packageName );
//                    startActivity(intent);
//                }

            }
        });
    }
}
