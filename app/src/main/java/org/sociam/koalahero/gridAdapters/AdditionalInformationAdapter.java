package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AdditionalInformationAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflator;


    private String[] additionalInfoCategories;

    public AdditionalInformationAdapter(Context c, String[] addtionalInfo) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.additionalInfoCategories = addtionalInfo;

    }

    public int getCount() {
        return additionalInfoCategories.length;
    }

    public Object getItem(int position) {
        return additionalInfoCategories[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        if( convertView == null) {
            grid = layoutInflator.inflate(R.layout.additional_info_grid_item, null);
        }else{
            grid = convertView;
        }



        ImageView buttonImageView;
        TextView buttonNameView;

        // Name
        buttonNameView = (TextView) grid.findViewById(R.id.buttonName);
        buttonNameView.setText(additionalInfoCategories[position]);

        // Image
        buttonImageView =  (ImageView) grid.findViewById(R.id.buttonImage);

        try {
            InputStream ims = context.getAssets().open("placeHolder" + additionalInfoCategories[position] + ".png");
            Drawable d = Drawable.createFromStream(ims, null);
            buttonImageView.setImageDrawable(d);
            ims.close();
        } catch(IOException e) {
            e.printStackTrace();
        }


        return grid;
    }

}
