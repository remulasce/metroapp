package com.remulasce.lametroapp.static_data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.analytics.Tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    private Context context;

    public StopNameSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void initialize() {
        Log.d(TAG, "StopName table forcing initialization");

        // Getting the database should force its creation via onCreate if it has not yet been
        // created.
        this.getReadableDatabase();
        Log.d(TAG, "StopName table initialized");
    }

    @Override
    public String getStopName(String stopID) {
        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(makeStopNameRequest(stopID), null);
        cursor.moveToFirst();

        int nameColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
        String stopname = cursor.getString(nameColumnIndex);

        Tracking.sendTime("SQL", "StopNames", "getStopName", t);
        Log.d(TAG,"Got stopname for "+stopID+", "+ stopname);

        return stopname;
    }

    @Override
    public String getStopID(String stopName) {
        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(makeStopIDRequest(stopName), null);
        cursor.moveToFirst();

        int idColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPID);
        String stopID = cursor.getString(idColumnIndex);

        Tracking.sendTime("SQL", "StopNames", "getStopID", t);
        Log.d(TAG,"Got stopID for "+stopName+", "+ stopID);

        return stopID;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        long start = Tracking.startTime();
        Log.d(TAG, "Creating stopname database table");
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

        try {
            BufferedReader file = getStopsFileReader();

            String line;
            int entries = 0;
            while ( (line = file.readLine()) != null ) {
                // stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,stop_url,location_type,paren
                String[] split = line.split(",");

                String stopID = split[0];
                String stopName = split[2];

                putNewStopDef(sqLiteDatabase, stopID, stopName);
                entries++;
            }

            long time = Tracking.timeSpent(start);
            Tracking.sendTime("SQL", "StopNames", "Initial Setup", start);
            Log.i(TAG, "Finished parsing stopnames list, "+entries+" entries took "+time+" ms");
            file.close();

        } catch (IOException e) {
            Log.e(TAG, "Failed to create stops database; file IO exception");
            e.printStackTrace();
        }
    }

    // Request for a stopname, given stopid
    private String makeStopNameRequest(String stopID) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + StopNameEntry.COLUMN_NAME_STOPID +
                " LIKE \'" + stopID + "\'";
    }
    // Request for a stopid, given stopname
    private String makeStopIDRequest(String stopName) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + StopNameEntry.COLUMN_NAME_STOPNAME +
                " LIKE \'" + stopName + "\'";
    }

    private void putNewStopDef(SQLiteDatabase sqLiteDatabase, String stopID, String stopName) {
        Log.v(TAG, "Putting new stop def, "+stopID+", "+stopName);

        ContentValues values = new ContentValues();
        values.put(StopNameEntry.COLUMN_NAME_STOPID, stopID);
        values.put(StopNameEntry.COLUMN_NAME_STOPNAME, stopName);

        long newRowId = sqLiteDatabase.insert(
                StopNameEntry.TABLE_NAME,
                null,
                values);

        Log.v(TAG, "New stop rowid is "+newRowId);
    }

    private BufferedReader getStopsFileReader() throws IOException {
        InputStream inputStream = context.getAssets().open("stops.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
