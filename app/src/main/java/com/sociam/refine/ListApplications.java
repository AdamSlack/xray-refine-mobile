package com.sociam.refine;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListApplications extends AppCompatActivity {

    AppInfoAdapter appInfoAdapter;
    ArrayList<AppInfo> apps;
    ArrayList<AppInfo> filteredApps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_applications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        ListView appListView = (ListView) findViewById(R.id.appListView);

        apps = getAppInfo();
        filteredApps = new ArrayList<AppInfo>(apps);

        appInfoAdapter = new AppInfoAdapter(this, filteredApps);
        appListView.setAdapter(appInfoAdapter);

        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo app = (AppInfo) appInfoAdapter.getItem(position);

                Intent viewApp = new Intent(getApplicationContext(),AppDetailView.class);
                viewApp.putExtra("appPackageName", app.getAppPackageName());
                startActivity(viewApp);
            }
        });

        initialiseSearchBar();

    }

    private void initialiseSearchBar() {
        final EditText appSearchEditText = (EditText) findViewById(R.id.appSearchEditText);

        appSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredApps = new ArrayList<AppInfo>();
                String searchString = appSearchEditText.getText().toString();
                for(AppInfo app : apps) {
                    if(app.getAppName().toLowerCase()
                            .contains(searchString.toLowerCase())){
                        filteredApps.add(app);
                    }
                    else if(app.getAppPackageName().toLowerCase()
                            .contains(searchString.toLowerCase())) {
                        filteredApps.add(app);
                    }
                }
                appInfoAdapter.clear();
                appInfoAdapter.addAll(filteredApps);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<ResolveInfo> getAppList() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        return pkgAppsList;
    }

    private ArrayList<AppInfo> getAppInfo() {
        List<ResolveInfo> appList = getAppList();
        Set<AppInfo> appSet = new HashSet<AppInfo>();
        PackageManager pm = getPackageManager();
        for(ResolveInfo app : appList) {
            appSet.add(new AppInfo(app, pm));
        }
        return new ArrayList<AppInfo>(appSet);
    }
}
