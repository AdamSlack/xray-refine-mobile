package org.sociam.koalahero.additionalInfoActivities;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.xray.XRayAppInfo;

public class AdditionalInfoCMSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_cms);


        final Context context = this;

        final String packageName = getIntent().getStringExtra("PACKAGE_NAME");
        AppModel appModel = AppModel.getInstance();
        XRayAppInfo xRayAppInfo = appModel.getApp(packageName).getxRayAppInfo();

        TextView titleTextView = (TextView) findViewById(R.id.per_app_title);
        TextView summaryTextView = (TextView) findViewById(R.id.per_app_summary);
        ImageView iconImageView = (ImageView) findViewById(R.id.per_app_icon);

        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app,0);

            // App Name
            titleTextView.setText(getPackageManager().getApplicationLabel(appInfo));
            summaryTextView.setText(xRayAppInfo.appStoreInfo.summary);
            // App Icon
            Drawable icon = getPackageManager().getApplicationIcon(appInfo);
            iconImageView.setImageDrawable(icon);

        }
        catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }
    }
}
