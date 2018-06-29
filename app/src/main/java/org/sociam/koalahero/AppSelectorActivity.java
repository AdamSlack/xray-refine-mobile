package org.sociam.koalahero;

import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.appsInspector.AppDisplayMode;
import org.sociam.koalahero.appsInspector.AppModel;
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
        appModel.setDisplayMode(AppDisplayMode.SELECTED);
        final Context context = this;



        GridView gridview = (GridView) findViewById(R.id.selectionGridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                App app = appModel.getApp(appModel.getAlphabeticalIndex()[position]);

                boolean newToDisplay = !app.isSelectedToDisplay();

                // Only allow 10 to be selected at once
                if( appModel.countApps(AppDisplayMode.SELECTED) < 10 || !newToDisplay) {
                    app.setSelectedToDisplay(newToDisplay);
                    appModel.saveSelectedApps();
                    appModel.index();

                    updateGrid();
                } else {

                    // Color change to indicate selection already full
                    ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.info_bar);
                    cl.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    TextView num = (TextView) findViewById(R.id.number_selected_indicator);
                    num.setTextColor(getResources().getColor(R.color.colorWhite));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.info_bar);
                            cl.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
                            TextView num = (TextView) findViewById(R.id.number_selected_indicator);
                            num.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    }, 150);


                }
            }
        });

        sa = new SelectionAdapter( this, appModel, this);
        gridview.setAdapter(sa);

        updateGrid();
    }

    private void updateGrid(){

        int count = appModel.countApps(AppDisplayMode.SELECTED);
        TextView num = (TextView) findViewById(R.id.number_selected_indicator);
        num.setText( count + " of 10 Selected");

        sa.notifyDataSetChanged();
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
