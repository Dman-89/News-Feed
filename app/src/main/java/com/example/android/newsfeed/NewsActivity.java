package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.newsfeed.BuildConfig;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news */
    private NewsAdapter mAdapter;

    /** ArrayList for adapter */
    private ArrayList<News> news = new ArrayList<>();

    private ProgressBar mProgressBar;

    // app API key for authentication
    private static String API_KEY = BuildConfig.ApiKey;

    // "show-tags=contributor" requests output of info about article author
    public static String GUARDIAN_API_REQUEST_URL = "https://content.guardianapis.com/search?show-tags=contributor";

    private static String USER_QUERY;

    private static final String PREFS_QUERY = "user query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        // when returning from SettingsActivity, extras will throw NullPointerException because
        // it's reloading and opened NOT from MainActivity
        if (extras != null) {
            // silently saving last user query to preferences
            USER_QUERY = extras.get("query").toString();
            if (USER_QUERY.length() == 0) {
                USER_QUERY = "latest news";
            }
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_QUERY, MODE_PRIVATE).edit();
            editor.putString("query", USER_QUERY);
            editor.apply();
        } else {
            // restore USER_QUERY from preferences
            SharedPreferences prefs = getSharedPreferences(PREFS_QUERY, MODE_PRIVATE);
            USER_QUERY = prefs.getString("query", null);
        }


        Log.i("TEST News Activity", USER_QUERY);

        // Find a reference to the {@link RecyclerView} in the layout
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Find a reference to the empty state TextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_textview);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new NewsAdapter(this, news);

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        recyclerView.setAdapter(mAdapter);

        // default 1dp line item divider in RecyclerView
        DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        Log.i("TEST NewsActivity", "loaderManager.initLoader called");
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Log.i("TEST " + LOG_TAG, "onCreateLoader called");

        SharedPreferences sharedPrefs = PreferenceManager .getDefaultSharedPreferences(this);

        String articlesToDisplay = sharedPrefs.getString(
                getString(R.string.settings_articles_to_display_key),
                getString(R.string.settings_articles_to_display_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_API_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("q", USER_QUERY);
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("page-size", articlesToDisplay);
        uriBuilder.appendQueryParameter("order-by", orderBy);


        return new NewsLoader(this, uriBuilder.toString()); //GUARDIAN_API_REQUEST_URL + USER_QUERY
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        Log.i("TEST " + LOG_TAG, "onLoadFinished called");
        // Clear the adapter of previous news data
        clearAdapter();

        mProgressBar = findViewById(R.id.loading_indicator_news);
        // news array might be null when extractNews() returns null (at the top of method)
        if (news != null && !news.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            mAdapter.addItems(news);
            mAdapter.notifyDataSetChanged();
        } else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            boolean isConnected = netInfo != null && netInfo.isConnected();
            RecyclerView recyclerView = findViewById(R.id.list);

            if (isConnected) {
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i("TEST " + LOG_TAG, "onLoaderReset called");
        clearAdapter();
    }

    public void clearAdapter() {
        news.clear();
        mAdapter.notifyDataSetChanged();
    }

    // inflates the Options Menu we specified in the XML when the EarthquakeActivity opens up.
    @Override
    // This method initializes the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // This method is where we can setup the specific action that occurs when any of the items
    // in the Options Menu are selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // open the SettingsActivity via an intent
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
