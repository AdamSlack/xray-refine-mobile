package org.sociam.koalahero;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.xray.XRayAppInfo;

public class PerAppViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_app_view);

        String packageName = getIntent().getStringExtra("PACKAGE_NAME");
        AppModel appModel = AppModel.getInstance();
        XRayAppInfo xRayAppInfo = appModel.apps.get(packageName);

        TextView titleTextView = (TextView) findViewById(R.id.per_app_title);
        TextView summaryTextView = (TextView) findViewById(R.id.per_app_summary);
        ImageView iconImageView = (ImageView) findViewById(R.id.per_app_icon);


        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(xRayAppInfo.app,0);

            titleTextView.setText(getPackageManager().getApplicationLabel(appInfo));
            summaryTextView.setText(xRayAppInfo.appStoreInfo.summary);


            Drawable icon = getPackageManager().getApplicationIcon(appInfo);
            iconImageView.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }
    }
}
