package com.molps.recentquery;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SQLiteDatabase mDb;
    private CustomRecAdapter mAdapter;
    private RecyclerView recyclerViewResults;
    private RecyclerView recyclerViewQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecentQueryDbHelper queryDbHelper = new RecentQueryDbHelper(this);
        mDb = queryDbHelper.getWritableDatabase();
        mAdapter = new CustomRecAdapter(this, getRecentQuery());
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
                    mAdapter.swapCursor(getQueryWithWildCards(newText));
                } else {
                    mAdapter.swapCursor(getRecentQuery());
                }

                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    recyclerViewResults.setVisibility(View.GONE);
                } else {
                    recyclerViewResults.setVisibility(View.VISIBLE);
                }
            }
        });

        return true;
    }


    private void saveRecentQuery(String query) {
        ContentValues values = new ContentValues();
        values.put(RecentQueryEntry.COLUMN_QUERY, query);
        long insertResult = mDb.insert(RecentQueryEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, "inserResult: " + insertResult);
    }

    private Cursor getQueryWithWildCards(String newQuery) {
        String[] args = new String[]{newQuery + "%"};
        return mDb.query(
                RecentQueryEntry.TABLE_NAME,
                null,
                RecentQueryEntry.COLUMN_QUERY + " LIKE ?",
                args,
                null,
                null,
                RecentQueryEntry._ID + " DESC");

    }

    private Cursor getRecentQuery() {
        return mDb.query(
                RecentQueryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                RecentQueryEntry._ID + " DESC");

    }

}
