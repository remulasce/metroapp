package com.remulasce.lametroapp.static_data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

/**
 * Created by Remulasce on 12/17/2014.
 */
public class StopNameSQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StopNames.db";
    private static final int DATABASE_VERSION = 1;

    private static String[] projection = {
            StopNameEntry.COLUMN_NAME_STOPID,
            StopNameEntry.COLUMN_NAME_STOPNAME
    };

    public StopNameSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);



        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StopNameEntry.COLUMN_NAME_STOPID, "10848");
        values.put(StopNameEntry.COLUMN_NAME_STOPNAME, "Harbor Transitway");

        long newRowId;
        newRowId = db.insert(
                StopNameEntry.TABLE_NAME,
                null,
                values);
        Log.d("TEST", "newRowId "+newRowId);

        Cursor cursor = db.rawQuery("select * from "+StopNameEntry.TABLE_NAME, null);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
        Log.d("TEST", cursor.getString(columnIndex));
    }

    /* Inner class that defines the table contents */
    public static abstract class StopNameEntry implements BaseColumns {
        public static final String TABLE_NAME = "stopnames";
        public static final String COLUMN_NAME_STOPID = "stopid";
        public static final String COLUMN_NAME_STOPNAME = "stopname";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StopNameEntry.TABLE_NAME + " (" +
                    StopNameEntry._ID + " INTEGER PRIMARY KEY," +
                    StopNameEntry.COLUMN_NAME_STOPID + TEXT_TYPE + COMMA_SEP +
                    StopNameEntry.COLUMN_NAME_STOPNAME + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StopNameEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
