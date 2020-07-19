package com.example.newsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;



import com.google.android.material.navigation.NavigationView;



import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * URL to query the Guardian data for news information
     */
    private String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?";

    // Add your developer api-key here instead of "test"
    private static final String GUARDIAN_API_KEY = "test";

    private String mCurrentNewsSection;

    /**
     * Constant value for the news loader ID. We can choose any integer.
     */
    private static final int NEWS_LOADER_ID = 1;

    private DrawerLayout mDrawerLayout;

    /**
     * Adapter for the list of news
     */
    private NewsAdapter mNewsAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyTV;

    private LoaderManager mLoaderManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<NewsItem> newsList = new ArrayList<>();

        // Find a reference to the {@link ListView} in the layout
        ListView listView = findViewById(R.id.news_list);
        // Create a new adapter that takes an empty list of news as input
        mNewsAdapter = new NewsAdapter(this, newsList);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listView.setAdapter(mNewsAdapter);

        mEmptyTV = findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyTV);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        mCurrentNewsSection = getString(R.string.url_section_world);
        populateNewsListUi();

        // Set an item click listener on the ListView, which sends an intent to a web browser
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem clickedItem = (NewsItem) parent.getItemAtPosition(position);
                if (clickedItem.getWebUrl() != null && Patterns.WEB_URL.matcher(clickedItem.getWebUrl()).matches()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clickedItem.getWebUrl())));
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.news_url_not_provided_text), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLoaderManager == null)
                    populateNewsListUi();
                else
                    updateNewsListUi();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.nav_world: {
                        mCurrentNewsSection = getString(R.string.url_section_world);
                        if (mLoaderManager == null)
                            populateNewsListUi();
                        else
                            updateNewsListUi();
                        break;
                    }
                    case R.id.nav_science: {
                        mCurrentNewsSection = getString(R.string.url_section_science);
                        if (mLoaderManager == null)
                            populateNewsListUi();
                        else
                            updateNewsListUi();
                        break;
                    }
                    case R.id.nav_environment: {
                        mCurrentNewsSection = getString(R.string.url_section_environment);
                        if (mLoaderManager == null)
                            populateNewsListUi();
                        else
                            updateNewsListUi();
                        break;
                    }
                    case R.id.nav_technology: {
                        mCurrentNewsSection = getString(R.string.url_section_technology);
                        if (mLoaderManager == null)
                            populateNewsListUi();
                        else
                            updateNewsListUi();
                        break;
                    }
                    case R.id.nav_sport: {
                        mCurrentNewsSection = getString(R.string.url_section_sport);
                        if (mLoaderManager == null)
                            populateNewsListUi();
                        else
                            updateNewsListUi();
                        break;
                    }
                    case R.id.nav_business: {
                        mCurrentNewsSection = getString(R.string.url_section_business);
                        if (mLoaderManager == null)
                            populateNewsListUi();
                        else
                            updateNewsListUi();
                        break;
                    }
                    case R.id.nav_settings: {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    }
                    default:
                        break;
                }

                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.menu_refresh: {
                mSwipeRefreshLayout.setRefreshing(true);
                if (mLoaderManager == null)
                    populateNewsListUi();
                else
                    updateNewsListUi();
                return true;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String pageSize = sharedPreferences.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default)
        );

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("section", mCurrentNewsSection);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter(getString(R.string.settings_order_by_key), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.settings_page_size_key), pageSize);
        uriBuilder.appendQueryParameter("api-key", GUARDIAN_API_KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsList) {
        mSwipeRefreshLayout.setRefreshing(false);

        View loadingIndicator = findViewById(R.id.loading_bar);
        loadingIndicator.setVisibility(View.GONE);

        TextView emptyTV = findViewById(R.id.empty_view);
        emptyTV.setText(R.string.empty_list);

        mNewsAdapter.clear();
        if (newsList != null && !newsList.isEmpty())
            mNewsAdapter.addAll(newsList);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        mNewsAdapter.clear();
    }


    private void populateNewsListUi() {
        if (isNetworkAvailable()) {
            mLoaderManager = getLoaderManager();
            mLoaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_bar);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyTV.setText(R.string.no_internet_connection);
        }
    }

    private void updateNewsListUi() {
        if (isNetworkAvailable()) {
            mLoaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mEmptyTV.setText(getString(R.string.no_internet_connection));
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
