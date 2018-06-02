package com.sociam.refine;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class PreferencesActivity extends AppCompatActivity {

    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Preferences");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        appPreferences = AppPreferences.getInstance(getApplicationContext());

        ToggleButton studyOptInToggleBtn = (ToggleButton) findViewById(R.id.studyOptInToggleBtn);
        studyOptInToggleBtn.setChecked(AppPreferences.studyOptedIn);

        studyOptInToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.studyOptedIn = isChecked;
                AppPreferences.saveAppPreferences(getApplicationContext());
            }
        });
    }
}
