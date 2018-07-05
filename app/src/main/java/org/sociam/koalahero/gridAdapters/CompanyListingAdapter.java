package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.trackerMapper.TrackerMapperCompany;

import java.io.IOException;
import java.io.InputStream;

public class CompanyListingAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflator;


    private TrackerMapperCompany[] companies;

    public CompanyListingAdapter(Context c, TrackerMapperCompany[] companies) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.companies = companies;

    }

    public int getCount() {
        return companies.length;
    }

    public Object getItem(int position) {
        return companies[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        if( convertView == null) {
            grid = layoutInflator.inflate(R.layout.company_list_view_item, null);
        }else{
            grid = convertView;
        }

        TextView companyNameTV;

        TrackerMapperCompany company = companies[position];

        // Name
        companyNameTV = (TextView) grid.findViewById(R.id.companyNameTV);
        companyNameTV.setText(company.companyName);

        RecyclerView rv = (RecyclerView) grid.findViewById(R.id.companyPurposeListView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mLayoutManager);

        CompanyPurposeListAdapter cpla = new CompanyPurposeListAdapter(company.categories.toArray(new String[company.categories.size()]));
        rv.setAdapter(cpla);


        return grid;
    }
}

