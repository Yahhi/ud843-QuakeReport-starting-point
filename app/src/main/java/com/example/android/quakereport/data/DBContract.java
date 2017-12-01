package com.example.android.quakereport.data;

/**
 * Created by Konis on 10/20/2017.
 */

import android.provider.BaseColumns;

/**
 * Created by Konis on 10/18/2017.
 */

public final class DBContract {
    public static abstract class EqEntry implements BaseColumns{
        public static final String TABLE_NAME = "earthquakes";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_MAG = "mag";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_FELT = "felt";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_DEPTH = "depth";
        public static final String COLUMN_DISTANCE = "distance";
    }
}