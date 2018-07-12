package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchResultAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflator;

    private ImageView searchResultImage;
    private TextView searchResultTitle;
    private RatingBar searchResultRating;

    private App[] searchResults;
    private Drawable[] icons;
    private String[] URLs;

    public SearchResultAdapter(Context c, App[] searchResults) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = searchResults;
        this.icons = new Drawable[searchResults.length];
        Arrays.fill(this.icons, context.getDrawable(R.mipmap.ic_launcher));
        this.URLs = new String[searchResults.length];

        for(int i = 0; i < searchResults.length; i++) {
            if (!searchResults[i].getxRayAppInfo().title.equals("No Results Found.")) {
                String packageName = searchResults[i].getxRayAppInfo().app;
                String storeType = searchResults[i].getxRayAppInfo().storeType;
                String version = searchResults[i].getxRayAppInfo().version;
                String region = searchResults[i].getxRayAppInfo().region;

                this.URLs[i] = "https://negi.io/api/icons/" + packageName + "/" + storeType + "/" + region + "/" + version + "/" + "icon.png";
            }
        }

        new DownloadImageTask().execute(this.URLs);

    }

    public int getCount() {
        return searchResults.length;
    }

    public Object getItem(int position) {
        return searchResults[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        if( convertView == null) {
            grid = layoutInflator.inflate(R.layout.search_result_item, null);
        }else{
            grid = convertView;
        }

        App searchResult = searchResults[position];

        // Name
        this.searchResultImage = (ImageView) grid.findViewById(R.id.searchResultIcon);
        this.searchResultTitle = (TextView) grid.findViewById(R.id.searchResultTitle);
        this.searchResultRating = (RatingBar) grid.findViewById(R.id.searchResultRating);


        searchResultTitle.setText(searchResult.getxRayAppInfo().title);
        searchResultRating.setRating((float) searchResult.getxRayAppInfo().appStoreInfo.rating);
        searchResultImage.setImageDrawable(this.icons[position]);

        return grid;
    }


    // Code snippet lifted from https://stackoverflow.com/questions/5776851/load-image-from-url

    private class DownloadImageTask extends AsyncTask<String,Bitmap, Void> {

        private int position;

        @Override
        protected Void doInBackground(String... urls) {
            for (this.position=0; this.position < urls.length; this.position++) {
                String urldisplay = urls[this.position];
                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                    publishProgress(mIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            if (this.position < icons.length) {
                icons[this.position] = new BitmapDrawable(context.getResources(), values[0]);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


}
