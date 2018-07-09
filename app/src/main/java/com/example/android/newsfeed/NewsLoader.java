package com.example.android.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
    Log.i("TEST " + LOG_TAG, "NewsLoader constructor called");
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i("TEST " + LOG_TAG, "onStartLoading called");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        Log.i("TEST " + LOG_TAG, "loadInBackground called");
        if (mUrl == null) {
            return null;
        }
    // Perform the network request, parse the response, and extract a list of earthquakes.
        List<News> news = QueryUtils.fetchNewsData(mUrl);
        Log.i("TEST " + LOG_TAG, mUrl); // ok
        return news;
    }
}
