package org.sociam.koalahero.appsInspector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

public class AppsInspector {
    private static AppsInspector INSTANCE;

    private AppsInspector() {

    }

    public static AppsInspector getInstance() {
        if (INSTANCE == null) {
            return new AppsInspector();
        }
        return INSTANCE;
    }

    public static ArrayList<String> getInstalledApps(PackageManager pm) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> intentActivities = pm.queryIntentActivities(intent, 0);
        ArrayList<String> packageNames = new ArrayList<>();
        for(ResolveInfo ri : intentActivities) {
            packageNames.add(ri.activityInfo.packageName);
        }
        return packageNames;
    }


}
