package org.sociam.koalahero;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoCMSActivity;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoForParentsActivity;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoMapViewActivity;
import org.sociam.koalahero.additionalInfoActivities.AdditionalInfoTrackersActivity;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.gridAdapters.AdditionalInformationAdapter;
import org.sociam.koalahero.gridAdapters.SelectionAdapter;

public class AppSelectorActivity extends AppCompatActivity {

   AppModel appModel;
   SelectionAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Selection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appModel = AppModel.getInstance();
        final Context context = this;

        updateGrid();

        GridView gridview = (GridView) findViewById(R.id.selectionGridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                appModel.getAllInstalledApps().get(position).setIsSelectedToDisplay(!appModel.getAllInstalledApps().get(position).isSelectedToDisplay());
                appModel.saveSelectedApps();
                appModel.index();
                sa.notifyDataSetChanged();
            }
        });
    }

    private void updateGrid(){
        GridView gridview = (GridView) findViewById(R.id.selectionGridView);
        sa = new SelectionAdapter( this, appModel, this);
        gridview.setAdapter(sa);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
