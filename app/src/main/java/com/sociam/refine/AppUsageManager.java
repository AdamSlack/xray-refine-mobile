package com.sociam.refine;
/**
 *  App Usage Manager
 *
 *  Provides methods for obtaining app usage information, can fetch the total time an app is in the
 *  foreground in the past day, week, or month.
 *
 *  A method for formatting the usage time as a XX Hrs XX Min string is also provided.
*/

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.Calendar;
import java.util.Map;

public class AppUsageManager {

    static long calculateAppTimeUsage(String interval, String appPackageName, Context context) {
        if(!MainActivity.appHasUsageDataPermission(context)){
            return 0;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        if (interval.equals("Week")){
            calendar.add(Calendar.DATE, -7);
        }
        else if (interval.equals("Month")) {
            calendar.add(Calendar.DATE, -30);
        }
        else {
            calendar.add(Calendar.DATE, -1);
        }
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        Map<String, UsageStats> allStats = usageStatsManager.queryAndAggregateUsageStats(start, end);

        long totalTime;
        if(allStats.containsKey(appPackageName)){
            UsageStats stats = allStats.get(appPackageName);
            totalTime = stats.getTotalTimeInForeground();
        }
        else {
            totalTime = 0;
        }
        return totalTime/1000;
    }

    static String formatUsageTime(long usageTime) {
        int minutes = (int) ((usageTime / (60)) % 60);
        int hours   = (int) ((usageTime / (60*60)) % 24);
        return Integer.toString(hours) + " Hrs, " + Integer.toString(minutes) +" min";
    }
}
