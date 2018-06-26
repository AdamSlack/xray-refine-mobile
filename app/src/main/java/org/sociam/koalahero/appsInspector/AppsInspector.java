package org.sociam.koalahero.appsInspector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import org.sociam.koalahero.PreferenceManager.PreferenceManager;
import org.sociam.koalahero.koala.KoalaAPI;
import org.sociam.koalahero.koala.KoalaData.AuthDetails;
import org.sociam.koalahero.koala.KoalaData.InteractionRequestDetails;
import org.sociam.koalahero.koala.KoalaData.JSONData;
import org.sociam.koalahero.koala.KoalaData.PhoneInfoRequestDetails;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
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

    public static void logInstalledAppInfo(Context context, ArrayList<String> installedApps, ArrayList<String> topTenApps) {
        PhoneInfoRequestDetails pird = new PhoneInfoRequestDetails();
        PreferenceManager pm = PreferenceManager.getInstance(context);
        pird.authDetails = new AuthDetails(pm);

        pird.phoneInfo.studyID = pm.getKoalaStudyID();
        pird.phoneInfo.installedApps = installedApps;
        pird.phoneInfo.topTenApps = new ArrayList<String>(topTenApps);
        pird.phoneInfo.retrievalDatetime = new Date();

        KoalaAPI koalaAPI = KoalaAPI.getInstance();
        koalaAPI.executePhoneInformationRequest(context, pird);
    }

    public static void logInteractionInfo(Context context, String appPageViewName, String associatedAppID, String eventType, JSONData additionalData) {
        InteractionRequestDetails ird = new InteractionRequestDetails();

        // Auth Details
        PreferenceManager pm = PreferenceManager.getInstance(context);
        ird.authDetails = new AuthDetails(pm);

        // Log Details
        ird.interactionLog.studyID = pm.getKoalaStudyID();
        ird.interactionLog.interactionDatetime = new Date();
        ird.interactionLog.pageName = appPageViewName;
        ird.interactionLog.associatedAppID = associatedAppID;
        ird.interactionLog.interactionType = eventType;
        ird.interactionLog.additionalData = additionalData;

        KoalaAPI koalaAPI = KoalaAPI.getInstance();
        koalaAPI.executeInteractionLogRequest(context, ird);
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

    public static Drawable getAppPackageIcon(String appPackageName, PackageManager pm)
            throws PackageManager.NameNotFoundException {
        return pm.getApplicationIcon(appPackageName);
    }


}
