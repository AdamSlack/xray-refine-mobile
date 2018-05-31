package com.sociam.refine;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListApplications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_applications);

        ListView appListView = (ListView) findViewById(R.id.appListView);

        ArrayList<AppInfo> apps = getAppInfo();

        AppInfoAdapter appInfoAdapter = new AppInfoAdapter(this, apps);
        appListView.setAdapter(appInfoAdapter);
    }

    private List<ResolveInfo> getAppList() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        return pkgAppsList;
    }

    private ArrayList<AppInfo> getAppInfo() {
        List<ResolveInfo> appList = getAppList();
        Set<AppInfo> appSet = new HashSet<AppInfo>();
        PackageManager pm = getPackageManager();
        for(ResolveInfo app : appList) {
            appSet.add(new AppInfo(app, pm));
        }
        return new ArrayList<AppInfo>(appSet);
    }
}
