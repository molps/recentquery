package com.molps.recentquery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.molps.recentquery.RecentQueryContract.RecentQueryEntry;


public class RecentQueryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recentquery.db";
    private static final int DATABASE_VERSION = 1;

    public RecentQueryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "
                + RecentQueryEntry.TABLE_NAME + " ("
                + RecentQueryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecentQueryEntry.COLUMN_QUERY + " TEXT UNIQUE ON CONFLICT REPLACE NOT NULL COLLATE NOCASE);";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecentQueryEntry.TABLE_NAME);
        onCreate(db);

    }
}
