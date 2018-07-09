package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    ProgressBar mProgressBar;

    // "show-tags=contributor" requests output of info about article author
    public static String GUARDIAN_API_REQUEST_URL = "https://content.guardianapis.com/search?" +
            "api-key=fa4dbc95-3100-4976-8490-c197816d1a1b&show-tags=contributor&q=";

    private static String USER_QUERY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        USER_QUERY = extras.get("query").toString();
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
        return new NewsLoader(this, GUARDIAN_API_REQUEST_URL + USER_QUERY);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        Log.i("TEST " + LOG_TAG, "onLoadFinished called");
        // Clear the adapter of previous news data
        clearAdapter();

        mProgressBar = findViewById(R.id.loading_indicator_news);
        if (!news.isEmpty()) { // news != null is always true
            mProgressBar.setVisibility(View.GONE);
            mAdapter.addItems(news);
            mAdapter.notifyDataSetChanged();
        } else {
            RecyclerView recyclerView = findViewById(R.id.list);
            recyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);

            mEmptyStateTextView.setVisibility(View.VISIBLE);
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
}
