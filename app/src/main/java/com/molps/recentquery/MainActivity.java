package com.molps.recentquery;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.molps.recentquery.RecentQueryContract.RecentQueryEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_QUERY_KEY = "key 101";
    private CustomRecAdapter mAdapter;
    private RecyclerView recyclerViewResults;
    private RecyclerView recyclerViewQuery;
    private View dimView;
    private Bundle mArgs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArgs = new Bundle();
        dimView = findViewById(R.id.dim_layout);
        mAdapter = new CustomRecAdapter(null);
        recyclerViewResults = (RecyclerView) findViewById(R.id.results_recView);
        recyclerViewQuery = (RecyclerView) findViewById(R.id.recent_query_recView);
        recyclerViewQuery.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuery.setHasFixedSize(true);
        recyclerViewQuery.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchView_menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                saveRecentQuery(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    recyclerViewQuery.setVisibility(View.VISIBLE);
                    mArgs.putString(BUNDLE_QUERY_KEY, newText);
                    getLoaderManager().restartLoader(0, mArgs, MainActivity.this);
                } else {
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                }

                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int startColor = ContextCompat.getColor(MainActivity.this, R.color.startColor);
                int endColor = ContextCompat.getColor(MainActivity.this, R.color.endColor);
                if (hasFocus) {
                    recyclerViewResults.setVisibility(View.GONE);
                    recyclerViewQuery.setVisibility(View.VISIBLE);
                    animateColor(startColor, endColor);
                } else {
                    recyclerViewResults.setVisibility(View.VISIBLE);
                    recyclerViewQuery.setVisibility(View.GONE);
                    animateColor(endColor, startColor);
                }
            }
        });

        return true;
    }


    private void saveRecentQuery(String query) {
        ContentValues values = new ContentValues();
        values.put(RecentQueryEntry.COLUMN_QUERY, query);
        Uri insertResult = getContentResolver().insert(RecentQueryEntry.CONTENT_URI, values);

        Log.v(LOG_TAG, "inserResult: " + insertResult);
    }


    private void animateColor(int startColor, int endColor) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimator.setDuration(200);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dimView.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        colorAnimator.start();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            String path = args.getString(BUNDLE_QUERY_KEY);
            Uri uri = RecentQueryEntry.CONTENT_URI.buildUpon().appendPath(path).build();
            Log.v(LOG_TAG, "ProviderTEST onCreateLoader() uri: " + uri);
            return new CursorLoader(
                    this,
                    uri,
                    null,
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(
                    this,
                    RecentQueryEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
