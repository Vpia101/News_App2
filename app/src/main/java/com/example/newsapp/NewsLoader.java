package com.example.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    /**
     * Member variables
     */
    private static final String LOG_TAG = NewsLoader.class.getSimpleName();
    private String mUrl;

    /**
     * Constructor
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * Overridden methods
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        if (mUrl == null)
            return null;
        return QueryUtils.fetchNewsData(mUrl);
    }
}
