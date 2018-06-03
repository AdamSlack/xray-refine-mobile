package com.sociam.refine;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppSubstitutionAdapter extends ArrayAdapter{

    private String originalAppPackageName;
    private Context context;
    private XRayApp app;

    public AppSubstitutionAdapter(Context context, ArrayList<XRayApp> alternativeApp, String originalAppPackageName) {
        super(context, 0, alternativeApp);
        this.originalAppPackageName = originalAppPackageName;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        app = (XRayApp) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_substitution, parent, false);
        }

        TextView appNameTextView = (TextView) convertView.findViewById(R.id.altAppTitleTextView);

        BarChart hostBarChart = (BarChart) convertView.findViewById(R.id.altAppBarChart);
        if(hostBarChart.getData() == null) {
            hostBarChart.setData(createHostBarData(originalAppPackageName));
            hostBarChart.invalidate();
        }

        appNameTextView.setText(app.appStoreInfo.title);
        ImageView appIconImageView = (ImageView) convertView.findViewById(R.id.appIconImageView);
        new DownloadImageTask((ImageView) convertView.findViewById(R.id.altAppIcon))
                .execute(getContext().getString(R.string.xray_api_icons)+ app.iconURI);

        return convertView;
    }

    BarData createHostBarData(String appPackageName) {
        Long usageTime = AppUsageManager.calculateAppTimeUsage("week", appPackageName, context);
        ArrayList<BarEntry> originalData = new ArrayList<BarEntry>(GraphDataModel.getInstance().hostDataHorizontalDataEntries);
        ArrayList<String> originalLabels = new ArrayList<>(GraphDataModel.getInstance().hostDataAxisLabels);

        ArrayList<String> originalAppHosts = AppDataModel
                .getInstance(context.getPackageManager(), context)
                        .getxRayApps().get(originalAppPackageName).hosts;

        ArrayList<String> altAppHosts = app.hosts;

        Set<String> combinedHostSet = new HashSet<>();
        combinedHostSet.addAll(originalAppHosts);
        combinedHostSet.addAll(altAppHosts);
        ArrayList<String> combinedHosts = new ArrayList<String>(combinedHostSet);

        // Reduce host exposure from original app
        for(int i = 0; i < originalAppHosts.size(); i++) {
            int entryIdx = originalLabels.indexOf(originalAppHosts.get(i));
            if(entryIdx > -1) {
                BarEntry entry = originalData.get(entryIdx);
                float[] reducedVals = {(float) entryIdx, (float) (entry.getY() - usageTime)};
                entry.setVals(reducedVals);
            }
        }

        // increase host exposure from alternative app
        for(int i=0; i < altAppHosts.size(); i++) {
            int entryIdx = originalLabels.indexOf(altAppHosts.get(i));
            if (entryIdx > -1) {
                BarEntry entry = originalData.get(entryIdx);
                float[] increased = {(float) entryIdx, (float) (entry.getY()+usageTime)};
                entry.setVals(increased);
            }
            else {
                originalData.add(new BarEntry(originalLabels.size(), usageTime));
                originalLabels.add(altAppHosts.get(i));
            }
        }
        ArrayList<BarEntry> jointEntries = new ArrayList<>();
        for(String host : combinedHosts) {
            jointEntries.add(originalData.get(originalLabels.indexOf(host)));
        }
        BarDataSet bds = new BarDataSet(jointEntries, "New HostExposure");
        bds.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData bd = new BarData(bds);
        bd.setBarWidth(1f);
        return bd;
    }


    // From: stackoverflow, URL: https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
    // User: AndroidDeveloper, URL: https://stackoverflow.com/users/1196072/android-developer
    // This works great, i should use this when fetching XRay app data for sure.
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
