package com.sociam.refine;
/**
 * App Info Adapter
 *
 * ListView adapter for rendering app info objects in a list view.
*/
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppInfoAdapter extends ArrayAdapter {

    public AppInfoAdapter(Context context, ArrayList<AppInfo> appInfos) {
        super(context, 0, appInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AppInfo app = (AppInfo) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_item, parent, false);
        }

        TextView appNameTextView = (TextView) convertView.findViewById(R.id.appNameTextView);
        TextView appPackageNameTextView = (TextView) convertView.findViewById(R.id.appPackageNameTextView);
        ImageView appIconImageView = (ImageView) convertView.findViewById(R.id.appIconImageView);

        appNameTextView.setText(app.getAppName());
        appPackageNameTextView.setText(app.getAppPackageName());
        appIconImageView.setImageDrawable(app.getAppIcon());

        return convertView;
    }
}
