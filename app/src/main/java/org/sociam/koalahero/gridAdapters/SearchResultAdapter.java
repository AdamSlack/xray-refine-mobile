package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;

public class SearchResultAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflator;


    private App[] searchResults;

    public SearchResultAdapter(Context c, App[] searchResults) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = searchResults;

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
        ImageView searchResultImage = (ImageView) grid.findViewById(R.id.searchResultIcon);
        TextView searchResultTitle = (TextView) grid.findViewById(R.id.searchResultTitle);
        RatingBar searchResultRating = (RatingBar) grid.findViewById(R.id.searchResultRating);


        searchResultTitle.setText(searchResult.getxRayAppInfo().title);
        searchResultRating.setRating((float) searchResult.getxRayAppInfo().appStoreInfo.rating);

        return grid;
    }


}
