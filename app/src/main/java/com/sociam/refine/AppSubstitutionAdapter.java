package com.sociam.refine;

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

import java.io.InputStream;
import java.util.ArrayList;

public class AppSubstitutionAdapter extends ArrayAdapter{

    public AppSubstitutionAdapter(Context context, ArrayList<XRayApp> alternativeApp) {
        super(context, 0, alternativeApp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        XRayApp app = (XRayApp) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_substitution, parent, false);
        }

        TextView appNameTextView = (TextView) convertView.findViewById(R.id.altAppTitleTextView);

        BarChart hostBarChart = (BarChart) convertView.findViewById(R.id.altAppBarChart);

        appNameTextView.setText(app.appStoreInfo.title);
        ImageView appIconImageView = (ImageView) convertView.findViewById(R.id.appIconImageView);
        new DownloadImageTask((ImageView) convertView.findViewById(R.id.altAppIcon))
                .execute(getContext().getString(R.string.xray_api_icons)+ app.iconURI);

        return convertView;
    }

//    BarData createHostBarData(String appPackageName) {
//
//    }


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
