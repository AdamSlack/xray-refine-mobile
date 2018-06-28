package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.DropBoxManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.AppSelectorActivity;
import org.sociam.koalahero.R;
import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.xray.XRayAppInfo;

import java.io.IOException;
import java.io.InputStream;

public class SelectionAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflator;

    private AppModel appModel;
    private AppSelectorActivity activity;

    public SelectionAdapter(Context c, AppModel appModel, AppSelectorActivity activity ) {
        context = c;
        layoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.appModel = appModel;
        this.activity = activity;

        try {
            imsTick = context.getAssets().open("circleTick.png");
            dTick = Drawable.createFromStream(imsTick, null);
            imsNoTick = context.getAssets().open("circleNoTick.png");
            dNoTick = Drawable.createFromStream(imsNoTick, null);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private InputStream imsTick;
    private InputStream imsNoTick;
    private Drawable dTick;
    private Drawable dNoTick;

    public int getCount() {
        return appModel.getTotalNumberApps();
    }

    public Object getItem(int position) {
        return appModel.getAllInstalledApps().get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        App app = appModel.getAllInstalledApps().get(position);
        XRayAppInfo xRayAppInfo = app.getxRayAppInfo();

        ImageView appIconView, selectedIcon;
        TextView appNameView;
        CheckBox selectedBox;

        View grid;
        if( convertView == null) {
            grid = layoutInflator.inflate(R.layout.app_selection_grid_item, null);
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

            // Tick
            selectedIcon =  (ImageView) grid.findViewById(R.id.selected_icon);

            if( app.isSelectedToDisplay() )
                selectedIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_check_box_checked));
            else
                selectedIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_check_box_outline_blank));


        }
        catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }


        return grid;
    }

}
