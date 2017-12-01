package com.example.android.quakereport.data;

/**
 * Created by Konis on 10/4/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.quakereport.data.DBContract.EqEntry;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDBName.db";
    //private HashMap hp;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_EQ_TABLE = "CREATE TABLE " + EqEntry.TABLE_NAME + " ("
                + EqEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EqEntry.COLUMN_MAG + " INTEGER NOT NULL, "
                + EqEntry.COLUMN_LOCATION + " TEXT, "
                + EqEntry.COLUMN_DATE + " INTEGER NOT NULL, "
                + EqEntry.COLUMN_URL + " TEXT, "
                + EqEntry.COLUMN_FELT + " INTEGER, "
                + EqEntry.COLUMN_LONGITUDE + " INTEGER, "
                + EqEntry.COLUMN_LATITUDE + " INTEGER, "
                + EqEntry.COLUMN_DEPTH + " INTEGER, "
                + EqEntry.COLUMN_DISTANCE + " INTEGER);";
        db.execSQL(SQL_CREATE_EQ_TABLE);
        //Toast.makeText(context, "db done", Toast.LENGTH_LONG).show();
//        db.execSQL(
//                "create table contacts " +
//                        "(id integer primary key, name text,phone text,email text, street text,place text)"
//        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
//        db.execSQL("DROP TABLE IF EXISTS contacts");
//        onCreate(db);
    }
}
