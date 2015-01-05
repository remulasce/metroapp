package com.remulasce.lametroapp.static_data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.OmniAutoCompleteAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

/**
 * Created by Remulasce on 12/17/2014.
 */
public class StopNameSQLHelper extends SQLiteOpenHelper implements StopNameTranslator, OmniAutoCompleteProvider {
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
    public Collection<String> autocomplete(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        Collection<String> ret = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery(makeAutoCompleteNameRequest(input), null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                int nameColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
                int idColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPID);

                String stopName = cursor.getString(nameColumnIndex);
                String stopID = cursor.getString(idColumnIndex);

                // Each station entrance in Metro has its own stopID.
                // Duplicates have letters at the end; originals are straight digits.
                // Only add the originals
                if (stopID.matches("\\d+$")) {
                    ret.add(stopName);
                }
                cursor.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            ret = null;
        }

        Tracking.sendTime("SQL", "StopNames", "getStopID", t);
        Log.d(TAG,"Got stopID for "+input+", "+ ret);

        return ret;
    }

    @Override
    public String getStopName(String stopID) {
        if (stopID == null || stopID.isEmpty()) {
            return "Bad stopname request";
        }

        String ret = null;

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        try {

            Cursor cursor = db.rawQuery(makeStopNameRequest(stopID), null);
            cursor.moveToFirst();

            int nameColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
            ret = cursor.getString(nameColumnIndex);

        } catch (CursorIndexOutOfBoundsException e) {
            // This is an expected case, basically a check for existence.
            ret = null;
        }

        Tracking.sendTime("SQL", "StopNames", "getStopName", t);
        Log.d(TAG,"Got stopname for "+stopID+", "+ ret);

        return ret;
    }

    @Override
    public Collection<String> getStopID(String stopName) {
        if (stopName == null || stopName.isEmpty()) {
            return null;
        }

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        Collection<String> ret = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery(makeStopIDRequest(stopName), null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPID);
                String stopID = cursor.getString(idColumnIndex);

                // Each station entrance in Metro has its own stopID.
                // Duplicates have letters at the end; originals are straight digits.
                // Only add the originals
                if (stopID.matches("\\d+$")) {
                    ret.add(stopID);
                }
                cursor.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            ret = null;
        }

        Tracking.sendTime("SQL", "StopNames", "getStopID", t);
        Log.d(TAG,"Got stopID for "+stopName+", "+ ret);

        return ret;
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
    // Request for matching stopnames
    private String makeAutoCompleteNameRequest(String stopName) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + StopNameEntry.COLUMN_NAME_STOPNAME +
                " LIKE \'%" + stopName + "%\'";
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
