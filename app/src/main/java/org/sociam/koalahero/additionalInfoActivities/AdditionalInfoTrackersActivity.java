package org.sociam.koalahero.additionalInfoActivities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;
import org.sociam.koalahero.xray.AppGenre;
import org.sociam.koalahero.xray.AppGenreHostInfo;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayJsonParser;
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.sociam.koalahero.audio.AudioRecorder;

public class AdditionalInfoTrackersActivity extends AppCompatActivity {

    private AppModel appModel;
    private String packageName;
    private App selectedApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info_trackers);

        this.appModel = AppModel.getInstance();
        this.packageName = this.appModel.selectedAppPackageName;
        this.selectedApp = this.appModel.getApp(this.packageName);

        AudioRecorder.getINSTANCE(this).updateRecordingUI(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Is This Normal?");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        XRayAPI xRayAPI = XRayAPI.getInstance();

        HashMap<AppGenre, AppGenreHostInfo> appGenreHostInfos = xRayAPI.readGenreHostInfo(getApplicationContext());
        HashMap<String, TrackerMapperCompany> companyMap = this.selectedApp.companies;

        ArrayList<TrackerMapperCompany> companyNames = new ArrayList<>(companyMap.values());
        ArrayList<String> hostNames = this.selectedApp.getxRayAppInfo().hosts;

        AppGenreHostInfo thisAppsGenreInfo = appGenreHostInfos.get(this.selectedApp.getxRayAppInfo().appStoreInfo.getAppGenre());

        /**
         *  Rating Setup
         */

        TextView thisAppHostCountTV = (TextView) findViewById(R.id.thisAppHostCount);
        ImageView ratingIconIV = (ImageView) findViewById(R.id.hostRatingImage);
        TextView scoreCommentTV = (TextView) findViewById(R.id.scoreCommentTV);


        Integer genreAverageHostCount = (int) thisAppsGenreInfo.genreAvgHosts;
        Integer thisAppHostCount = hostNames.size();

        thisAppHostCountTV.setText(thisAppHostCount.toString());

        if(thisAppHostCount > genreAverageHostCount*1.2) {
            ratingIconIV.setImageDrawable(getDrawable(R.drawable.sad_face));
            scoreCommentTV.setText("Worse than Average");
        }
        else if(thisAppHostCount < genreAverageHostCount*0.8) {
            ratingIconIV.setImageDrawable(getDrawable(R.drawable.happy_face));
            scoreCommentTV.setText("Better than Average");
        }
        else {
            ratingIconIV.setImageDrawable(getDrawable(R.drawable.expressionless_face));
            scoreCommentTV.setText("About Average");
        }


        /**
         *  Header Info
         */
        TextView appTitle = (TextView) findViewById(R.id.per_app_title);
        TextView devTitle = (TextView) findViewById(R.id.trackerDevNameTextView);

        ImageView appIcon = (ImageView) findViewById(R.id.per_app_icon);

        appTitle.setText(this.selectedApp.getxRayAppInfo().appStoreInfo.title);
        devTitle.setText(this.selectedApp.getxRayAppInfo().developerInfo.devName);


        /**
         *  Barchart Setup
         */
        ArrayList<Integer> barValues = new ArrayList<>();
        barValues.add(thisAppHostCount);
        barValues.add(genreAverageHostCount );
        barValues.add(0);

        ArrayList<String> axisLabels = new ArrayList<String>(Arrays.asList("This App", "Genre Avg", "All Apps Avg"));

        BarData bd = this.buildBarData(barValues, axisLabels);
        this.buildHostBarChart(bd, axisLabels);

        /**
         * Set information read from device
         */
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(this.selectedApp.getxRayAppInfo().app,0);

            // App Icon
            Drawable icon = getPackageManager().getApplicationIcon(appInfo);
            appIcon.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void buildHostBarChart(BarData barData, final ArrayList<String> labels){
        BarChart barChart = (BarChart) findViewById(R.id.hostBarChart);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setLabelCount(barData.getEntryCount());
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(false);

        barChart.invalidate();
    }

    private BarData buildBarData(ArrayList<Integer> data, ArrayList<String> axisValues) {
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        for(int i=0 ; i < data.size(); i++) {
            BarEntry be = new BarEntry(barEntries.size(), data.get(i), axisValues.get(i));
            barEntries.add(be);
            }

        BarDataSet bds = new BarDataSet(barEntries, "Host Counts");
        bds.setColors(ColorTemplate.JOYFUL_COLORS);

        BarData bd = new BarData(bds);
        bd.setBarWidth(0.9f);


        return bd;
    }
}
