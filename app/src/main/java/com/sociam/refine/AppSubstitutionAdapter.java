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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

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

            hostBarChart.setDrawValueAboveBar(false);
            hostBarChart.getAxisLeft().setAxisMaximum(hostBarChart.getYMax());
            hostBarChart.getAxisLeft().setAxisMinimum(hostBarChart.getYMin());
            hostBarChart.getXAxis().setDrawLabels(false);
            hostBarChart.getXAxis().setDrawAxisLine(false);
            hostBarChart.getXAxis().setDrawGridLines(false);
            hostBarChart.getXAxis().setLabelCount(hostBarChart.getData().getEntryCount());
            System.out.println(hostBarChart.getXAxis().getLabelRotationAngle());
            hostBarChart.getXAxis().setLabelRotationAngle(90);
            System.out.println(hostBarChart.getXAxis().getLabelRotationAngle());
            System.out.println("  ");
            hostBarChart.setScaleEnabled(false);

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

        HashSet<String> originalAppHosts = new HashSet<>(AppDataModel
                .getInstance(context.getPackageManager(), context)
                        .getxRayApps().get(originalAppPackageName).hosts);

        HashSet<String> altAppHosts = new HashSet<>(app.hosts);

        Set<String> combinedHostSet = new HashSet<>();
        combinedHostSet.addAll(originalAppHosts);
        combinedHostSet.addAll(altAppHosts);

        ArrayList<String> combinedHosts = new ArrayList<String>(combinedHostSet);
        ArrayList<Float> exposureChanges = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();
        for(String host : combinedHosts) {
            exposureChanges.add(0f);
            int total = exposureChanges.size()-1;
            if(altAppHosts.contains(host)) {
                exposureChanges.set(total, exposureChanges.get(total) + usageTime);
            }
            if(originalAppHosts.contains(host)) {
                exposureChanges.set(total, exposureChanges.get(total) - usageTime);
            }
            if(exposureChanges.get(total) >= 0) {
                colours.add(context.getResources().getColor(R.color.increased_exposure));
            }
            else {
                colours.add(context.getResources().getColor(R.color.decreased_exposure));
            }
        }

        ArrayList<BarEntry> jointEntries = new ArrayList<>();
        ArrayList<String> axisLables = new ArrayList<>();

        for(int i=0; i < exposureChanges.size(); i++){
            BarEntry be = new BarEntry(jointEntries.size(), exposureChanges.get(i), combinedHosts.get(i));
            jointEntries.add(be);
            axisLables.add(combinedHosts.get(i));
        }

        BarDataSet bds = new BarDataSet(jointEntries, "New Host Exposure");
        bds.setColors(colours);
        bds.setValueFormatter(new DefaultValueFormatter(0) {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return entry.getData().toString();
            }
        });

        BarData bd = new BarData(bds);

        bd.setBarWidth(0.9f);

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
