package com.molps.recentquery;


import android.provider.BaseColumns;

public class RecentQueryContract {

    public static final class RecentQueryEntry implements BaseColumns {
        public static final String TABLE_NAME = "recent";
        public static final String COLUMN_QUERY = "query";
    }
}
