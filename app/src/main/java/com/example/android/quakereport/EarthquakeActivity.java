/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.quakereport.adapter.EarthQuakeAdapter;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {
    private EarthQuakeAdapter adapter;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyStateTextView;
    private ProgressBar progressBar;
    private final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        //Set up the app bar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //instantiate variables
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.list_bar);
        ListView earthquakeListView = findViewById(R.id.list);
        LoaderManager loaderManager = getLoaderManager();

        //Check connectivity of phone
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected)
        {
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_string);
        }
        earthquakeListView.setEmptyView(mEmptyStateTextView);
        adapter = new EarthQuakeAdapter(this, new ArrayList<EarthQuake>());

        // Set the adapter on the {@link earthquakeListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                EarthQuake currentEarthquake = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getURL());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //Refreshing the list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadTask refreshTask = new LoadTask();
                ConnectivityManager cm =
                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected)
                {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
                    String minMagnitude = sharedPrefs.getString(
                            getString(R.string.settings_min_magnitude_key),
                            getString(R.string.settings_min_magnitude_default));

                    String orderBy  = sharedPrefs.getString(
                            getString(R.string.settings_order_by_key),
                            getString(R.string.settings_order_by_default)
                    );

                    // parse breaks apart the URI string that's passed into its parameter
                    Uri baseUri = Uri.parse(USGS_URL);

                    // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
                    Uri.Builder uriBuilder = baseUri.buildUpon();

                    // Append query parameter and its value. For example, the `format=geojson`
                    uriBuilder.appendQueryParameter("format", "geojson");
                    uriBuilder.appendQueryParameter("limit", "100");
                    uriBuilder.appendQueryParameter("minmag", minMagnitude);
                    uriBuilder.appendQueryParameter("orderby", orderBy);
                    refreshTask.execute(uriBuilder.toString());
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_string);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "25");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", "time");

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
        return new EarthQuakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<EarthQuake>> loader, List<EarthQuake> earthQuakes) {
        progressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.empty_list_string);
        adapter.clear();
        if(earthQuakes!=null && !earthQuakes.isEmpty())
            adapter.addAll(earthQuakes);
    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuake>> loader) {
        adapter.clear();
    }

    //AsyncTask for SwipeRefreshing
    public class LoadTask extends AsyncTask<String, Void, List<EarthQuake>> {
        @Override
        protected List<EarthQuake> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            // Create a list of earthquake locations.
            return QueryUtils.fetchEarthquakeData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<EarthQuake> earthQuakes) {
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.empty_list_string);
            adapter.clear();
            if(earthQuakes!=null && !earthQuakes.isEmpty())
                adapter.addAll(earthQuakes);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //Handle the menu of the app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                // User chose the "Settings" item, show the app settings UI...
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
