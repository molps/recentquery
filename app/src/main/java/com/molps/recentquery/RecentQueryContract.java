package com.molps.recentquery;


import android.net.Uri;
import android.provider.BaseColumns;

public class RecentQueryContract {

    public static final String CONTENT_AUTHORITY = "com.molps.recentquery";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USERNAMES = "usernames";

    public static final class RecentQueryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERNAMES).build();

        public static final String TABLE_NAME = "recent";
        public static final String COLUMN_QUERY = "query";
    }
}
