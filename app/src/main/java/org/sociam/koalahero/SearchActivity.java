package org.sociam.koalahero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;

public class SearchActivity extends AppCompatActivity {

    SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search for an App.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.searchBar = (SearchView) findViewById(R.id.searchBar);

        this.searchBar.setQueryHint("Search for an App.");

        this.searchBar.setIconified(false);
        this.searchBar.setIconifiedByDefault(false);

        this.searchBar.setSubmitButtonEnabled(true);

    }
}
