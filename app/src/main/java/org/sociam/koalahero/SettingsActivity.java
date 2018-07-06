package org.sociam.koalahero;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.sociam.koalahero.appsInspector.AppDisplayMode;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.appsInspector.Interval;
import org.sociam.koalahero.audio.AudioRecorder;

public class SettingsActivity extends AppCompatActivity {

    AppModel appModel;
    private RadioGroup radioDisplayModeGroup;
    private RadioGroup radioSortModeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appModel = AppModel.getInstance();

        AudioRecorder.getINSTANCE(this).updateRecordingUI(this);

        // Set the radio button to the current displaymode
        radioDisplayModeGroup = (RadioGroup) findViewById(R.id.settings_display_mode);
        AppDisplayMode displayMode = appModel.getDisplayMode();
        RadioButton b;
        switch (displayMode){
            case All:
                b = (RadioButton) findViewById(R.id.view_all);
                break;
            case TOP_TEN:
                b = (RadioButton) findViewById(R.id.view_top_10);
                b.setChecked(true);
                break;
            case SELECTED:
                b = (RadioButton) findViewById(R.id.view_selected);
                b.setChecked(true);
                break;
        }

        // Set the radio button to the current sort mode
        radioSortModeGroup = (RadioGroup) findViewById(R.id.settings_sort_mode);
        isSortModeGroupEnabled(displayMode == AppDisplayMode.TOP_TEN);
        Interval sortMode = appModel.getSortMode();
        switch (sortMode){
            case DAY:
                b = (RadioButton) findViewById(R.id.sort_daily);
                b.setChecked(true);
                break;
            case WEEK:
                b = (RadioButton) findViewById(R.id.sort_weekly);
                b.setChecked(true);
                break;
            case MONTH:
                b = (RadioButton) findViewById(R.id.sort_monthly);
                b.setChecked(true);
                break;
        }

        setupDisplayModeListeners();
    }

    public void setupDisplayModeListeners(){

        // Setup listeners
        radioDisplayModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if( group.equals(radioDisplayModeGroup)) {

                    switch( checkedId ){
                        case R.id.view_top_10:
                            appModel.setDisplayMode(AppDisplayMode.TOP_TEN);
                            break;
                        case R.id.view_all:
                            appModel.setDisplayMode(AppDisplayMode.All);
                            break;
                        case R.id.view_selected:
                            appModel.setDisplayMode(AppDisplayMode.SELECTED);
                            break;

                    }

                    isSortModeGroupEnabled(checkedId == R.id.view_top_10);

                }

            }
        });


        radioSortModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch( checkedId ){
                    case R.id.sort_daily:
                        appModel.setSortMode(Interval.DAY);
                        break;
                    case R.id.sort_weekly:
                        appModel.setSortMode(Interval.WEEK);
                        break;
                    case R.id.sort_monthly:
                        appModel.setSortMode(Interval.MONTH);
                        break;

                }
            }
        });

    }


    private void isSortModeGroupEnabled( boolean bool ){
        radioSortModeGroup.setEnabled(bool);
        Button b = (RadioButton) findViewById(R.id.sort_daily);
        b.setEnabled(bool);
        b = (RadioButton) findViewById(R.id.sort_weekly);
        b.setEnabled(bool);
        b = (RadioButton) findViewById(R.id.sort_monthly);
        b.setEnabled(bool);
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
