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
import java.util.Arrays;

public class SearchResultAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflator;

    private ImageView searchResultImage;
    private TextView searchResultTitle;
    private RatingBar searchResultRating;

    private App[] searchResults;
//    private Drawable[] icons;

    public SearchResultAdapter(Context c, App[] searchResults) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = searchResults;

//        this.icons = new Drawable[searchResults.length];
//        Arrays.fill(icons, context.getDrawable(R.mipmap.ic_launcher));
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

        if(!searchResult.getxRayAppInfo().title.equals("No Results Found.")) {
            String packageName = searchResult.getxRayAppInfo().app;
            String storeType = searchResult.getxRayAppInfo().storeType;
            String version = searchResult.getxRayAppInfo().version;
            String region = searchResult.getxRayAppInfo().region;

            String URL = "https://negi.io/api/icons/" + packageName + "/" + storeType + "/" + region + "/" + version + "/" + "icon.png";
            new DownloadImageTask(position).execute(URL);
        }
        return grid;
    }


    // Code snippet lifted from https://stackoverflow.com/questions/5776851/load-image-from-url

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private int position;

        private DownloadImageTask(){}
        private DownloadImageTask(int position) {
            this.position = position;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            searchResultImage.setImageBitmap(result);
        }
    }


}
