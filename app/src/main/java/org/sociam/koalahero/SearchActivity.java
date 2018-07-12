package org.sociam.koalahero;

import android.arch.core.util.Function;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import org.sociam.koalahero.appsInspector.App;
import org.sociam.koalahero.gridAdapters.SearchResultAdapter;
import org.sociam.koalahero.xray.XRayAPI;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchView searchBar;
    Button searchButton;
    ListView searchResultsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search for an App.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.initialiseSearchResultListView();
        this.initialiseSearchBar();
        this.initialiseSearchButton();


    }

    private void initialiseSearchResultListView() {
        this.searchResultsListView = (ListView) findViewById(R.id.searchResultListView);
    }

    private void setSearchResultsListViewAdapter(App[] results) {
        SearchResultAdapter adapter = new SearchResultAdapter(getApplicationContext(), results);
        this.searchResultsListView.setAdapter(adapter);
    }

    private void initialiseSearchBar() {
        this.searchBar = (SearchView) findViewById(R.id.searchBar);

        this.searchBar.setQueryHint("Enter the name of the app.");

        this.searchBar.setIconified(false);
        this.searchBar.setIconifiedByDefault(false);

//        this.searchBar.setSubmitButtonEnabled(true);

        this.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String s) {
                // don't do anything if query changes.
                // Could make use of XRay's Search Term for autocomplete suggestions.
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                XRayAPI xRayAPI = XRayAPI.getInstance();

                xRayAPI.executeAppSearchRequest(
                        query,
                        getApplicationContext(),
                        new Function<Void, Void>() {
                            @Override
                            public Void apply(Void input) {
                                // End Loading Anim, if there is one.
                                return null;
                            }
                        },
                        new Function<ArrayList<App>, Void>() {
                            @Override
                            public Void apply(ArrayList<App> input) {
                                // Refresh Array List
                                setSearchResultsListViewAdapter(input.toArray(new App[input.size()]));
                                return null;
                            }
                        }
                );

                return true;
            }
        });
    }

    private void initialiseSearchButton() {
        this.searchButton = (Button) findViewById(R.id.searchSubmitButton);

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setQuery(searchBar.getQuery(), true);
            }
        });
    }
}