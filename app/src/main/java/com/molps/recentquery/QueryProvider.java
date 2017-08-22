package com.molps.recentquery;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.molps.recentquery.RecentQueryContract.RecentQueryEntry;

public class QueryProvider extends ContentProvider {

    private static final String LOG_TAG = QueryProvider.class.getSimpleName();

    private RecentQueryDbHelper mDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CODE_NAMES = 100;
    private static final int CODE_NAME_ID = 101;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(RecentQueryContract.CONTENT_AUTHORITY, RecentQueryContract.PATH_USERNAMES, CODE_NAMES);
        matcher.addURI(RecentQueryContract.CONTENT_AUTHORITY, RecentQueryContract.PATH_USERNAMES + "/*", CODE_NAME_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RecentQueryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_NAMES:
                cursor = mDbHelper.getReadableDatabase().query(
                        RecentQueryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_NAME_ID:
                selection = RecentQueryEntry.COLUMN_QUERY + " LIKE ?";
                selectionArgs = new String[]{uri.getLastPathSegment() + "%"};
                sortOrder = RecentQueryEntry._ID + " DESC";
                cursor = mDbHelper.getReadableDatabase().query(
                        RecentQueryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        long rowId;

        switch (sUriMatcher.match(uri)) {
            case CODE_NAMES:
                rowId = mDbHelper.getWritableDatabase().insert(
                        RecentQueryEntry.TABLE_NAME,
                        null,
                        values
                );
                if (rowId != -1)
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                else
                    throw new SQLException("Failed to insert row into: " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Uknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.v(LOG_TAG, "ProviderTEST insert(); returnUri= " + returnUri);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
