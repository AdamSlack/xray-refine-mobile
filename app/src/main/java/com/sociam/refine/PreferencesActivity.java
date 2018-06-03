package com.sociam.refine;

import android.app.DialogFragment;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

        final ToggleButton studyOptInToggleBtn = (ToggleButton) findViewById(R.id.studyOptInToggleBtn);
        studyOptInToggleBtn.setChecked(AppPreferences.studyOptedIn);

        studyOptInToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyOptInToggleBtn.setChecked(AppPreferences.studyOptedIn);
                DialogFragment studyTerms = new AcceptStudyTermsFragment();
                studyTerms.show(getFragmentManager(),  "acceptStudyTerms");
            }
        });
    }

    public void onStudyOptInSelection(boolean isOptingIn) {
        AppPreferences.studyOptedIn = isOptingIn;
        AppPreferences.saveAppPreferences(getApplicationContext());
        ToggleButton studyOptInToggleBtn = (ToggleButton) findViewById(R.id.studyOptInToggleBtn);
        studyOptInToggleBtn.setChecked(AppPreferences.studyOptedIn);
    }
}
