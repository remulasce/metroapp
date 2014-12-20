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
public class StopNameSQLHelper extends SQLiteOpenHelper implements StopNameTranslator {
    private static final String TAG = "StopNameSQLHelper";

    private static final String DATABASE_NAME = "StopNames.db";
    private static final int DATABASE_VERSION = 1;
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

    public static abstract class StopNameEntry implements BaseColumns {
        public static final String TABLE_NAME = "stopnames";
        public static final String COLUMN_NAME_STOPID = "stopid";
        public static final String COLUMN_NAME_STOPNAME = "stopname";
    }
    private static String[] projection = {
            StopNameEntry.COLUMN_NAME_STOPID,
            StopNameEntry.COLUMN_NAME_STOPNAME
    };


    private boolean initialized = false;
    private boolean initializing = false;


    public StopNameSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void initialize() {
        if (!isInitializing() && !isInitialized()) {
            Log.d(TAG, "Initializing stopname table from file");
            // Start initializing
        }
        else {
            Log.d(TAG, "Stopname table already initialized");
            return;
        }
        // If we haven't initialized the database, do so.
        // Doing so starts a new thread that reads from file and inserts entries.
        // When that thread finishes, it marks initialized as true.
    }

    @Override
    public String getStopName(String stopID) {
        // If we're initialized, return the mapping.
        // Else, return null.
        return null;
    }

    @Override
    public String getStopID(String stopName) {
        // If we're initialized, return the mapping.
        // Else, return null.
        return null;
    }

    private boolean isInitializing() {
        return initializing;
    }
    private boolean isInitialized() {
        return isTableExists(StopNameEntry.TABLE_NAME);
    }

    private class SQLInitializationRunner implements Runnable {

        @Override
        public void run() {
            // Do some file-read stuff.
        }
    }

    public boolean isTableExists(String tableName) {
        Log.d(TAG, "Checking if stopname-id table is already started");

        SQLiteDatabase db;
        db = getReadableDatabase();

        if(!db.isReadOnly()) {
            db.close();
            db = getReadableDatabase();
        }

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            int entryCount = cursor.getCount();
            Log.d(TAG, "Stopname-id table has "+ entryCount +" entries");

            if(entryCount > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Creating stopname database table");
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

        //Do all file-reading here, I guess. It probably expects to be fully initialized
        // when the function returns.

        //Database reads elsewhere will just wait on this guy, then.
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}



//SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(StopNameEntry.COLUMN_NAME_STOPID, "10848");
//        values.put(StopNameEntry.COLUMN_NAME_STOPNAME, "Harbor Transitway");
//
//        long newRowId;
//        newRowId = db.insert(
//                StopNameEntry.TABLE_NAME,
//                null,
//                values);
//        Log.d("TEST", "newRowId "+newRowId);
//
//        Cursor cursor = db.rawQuery("select * from "+StopNameEntry.TABLE_NAME, null);
//
//        cursor.moveToFirst();
//        int columnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
//        Log.d("TEST", cursor.getString(columnIndex));
