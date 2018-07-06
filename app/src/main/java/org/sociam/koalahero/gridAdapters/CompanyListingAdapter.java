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

        // Flag Icon

        TextView companyViewUnicode = (TextView) grid.findViewById(R.id.companyViewUnicode);
        System.out.println(company.locale);
        if(company.locale != null && company.locale.length() == 2) {
            companyViewUnicode.setText(this.codeToEmojiString(company.locale.toUpperCase()));
        }
        else {
            companyViewUnicode.setText("☠");
        }


        // Purposes.
        RecyclerView rv = (RecyclerView) grid.findViewById(R.id.companyPurposeListView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mLayoutManager);

        String[] purposeArray = company.categories.toArray(new String[company.categories.size()+1]);
        if(purposeArray[0] == null) {
            purposeArray[0] = "Unknown";
        }
        CompanyPurposeListAdapter cpla = new CompanyPurposeListAdapter(purposeArray);
        rv.setAdapter(cpla);


        return grid;
    }

    public String codeToEmojiString(String code) {
        // StackOverflow - https://stackoverflow.com/questions/42234666/get-emoji-flag-by-country-code
        int flagOffset = 0x1F1E6;
        int asciiOffset = 0x41;

        int firstChar = Character.codePointAt(code, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(code, 1) - asciiOffset + flagOffset;

        return new String(Character.toChars(firstChar))  + new String(Character.toChars(secondChar));
    }

}

