package org.sociam.koalahero;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.sociam.koalahero.appsInspector.AppDisplayMode;
import org.sociam.koalahero.appsInspector.AppModel;
import org.sociam.koalahero.appsInspector.Interval;

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

        appModel = AppModel.getInstance();

        // Set the radio button to the current displaymode
        radioDisplayModeGroup = (RadioGroup) findViewById(R.id.settings_display_mode);
        AppDisplayMode displayMode = appModel.getDisplayMode();
        RadioButton b;
        switch (displayMode){
            case All:
                b = (RadioButton) findViewById(R.id.view_all);
                b.setChecked(true);
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

    public void save(View veiw){

        Intent intent = new Intent();

        // Display Mode
        int checkedId = radioDisplayModeGroup.getCheckedRadioButtonId();
        switch( checkedId ){
            case R.id.view_top_10:
                intent.putExtra("DISPLAY_MODE","TOP_10");
                setResult(RESULT_OK,intent);
                break;
            case R.id.view_all:
                intent.putExtra("DISPLAY_MODE","ALL");
                setResult(RESULT_OK,intent);
                break;
            case R.id.view_selected:
                intent.putExtra("DISPLAY_MODE","SELECTED");
                setResult(RESULT_OK,intent);
                break;

        }

        // Sort Mode
        checkedId = radioSortModeGroup.getCheckedRadioButtonId();
        switch( checkedId ){
            case R.id.sort_daily:
                intent.putExtra("SORT_MODE","DAY");
                setResult(RESULT_OK,intent);
                break;
            case R.id.sort_weekly:
                intent.putExtra("SORT_MODE","WEEK");
                setResult(RESULT_OK,intent);
                break;
            case R.id.sort_monthly:
                intent.putExtra("SORT_MODE","MONTH");
                setResult(RESULT_OK,intent);
                break;

        }



        finish();
    }




    public void setupDisplayModeListeners(){

        // Setup listeners
        radioDisplayModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if( group.equals(radioDisplayModeGroup)) {
                    isSortModeGroupEnabled(checkedId == R.id.view_top_10);

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
}
