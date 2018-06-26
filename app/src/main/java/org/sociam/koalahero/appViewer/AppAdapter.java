package org.sociam.koalahero.appViewer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.xray.XRayAPI;
import org.sociam.koalahero.xray.XRayAppInfo;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;

public class AppAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflator;

    //private XRayAppInfo[] xRayAppInfo;

    private AppModel appModel;

    public AppAdapter(Context c, AppModel appModel) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.appModel = appModel;

        System.out.println("No. Apps to Display In Grid: " + appModel.installedApps.size());

        // Convert the appModel HashMap into an array.
        //AppModel appModel = AppModel.getInstance();
//        xRayAppInfo = new XRayAppInfo[appModel.apps.size()];
//
//        int i = 0;
//        for (String key : appModel.apps.keySet()) {
//            xRayAppInfo[i] = appModel.apps.get(key);
//            i++;
//        }

    }

    public int getCount() {
        return appModel.installedApps.size();
    }

    public Object getItem(int position) {
        String appName = appModel.appNames[position];
        return appModel.installedApps.get(appName);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        XRayAppInfo xRayAppInfo = appModel.get(position);

        ImageView appIconView;
        TextView appNameView;

        View grid;
        if( convertView == null) {
            grid = layoutInflator.inflate(R.layout.grid_item, null);
        }else{
            grid = convertView;
        }


        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(xRayAppInfo.app,0);

            // Name
            appNameView =  (TextView) grid.findViewById(R.id.appName);
            if( xRayAppInfo.appStoreInfo.title.equals("Unknown"))
                appNameView.setText( context.getPackageManager().getApplicationLabel(appInfo) );
            else
                appNameView.setText( xRayAppInfo.appStoreInfo.title );

            // Icon
            appIconView =  (ImageView) grid.findViewById(R.id.appIcon);
            Drawable icon = context.getPackageManager().getApplicationIcon(appInfo);
            appIconView.setImageDrawable(icon);

        }
        catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }


        return grid;
    }

}
