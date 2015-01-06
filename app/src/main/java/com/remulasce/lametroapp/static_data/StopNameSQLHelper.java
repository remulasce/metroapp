package com.remulasce.lametroapp.static_data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.remulasce.lametroapp.analytics.Tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

/**
 * Created by Remulasce on 12/17/2014.
 */
public class StopNameSQLHelper extends SQLiteOpenHelper implements StopNameTranslator, OmniAutoCompleteProvider {
    private static final String TAG = "StopNameSQLHelper";

    private static final int MINIMUM_AUTOCOMPLETE_PROMPT = 3;

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
    public Collection<OmniAutoCompleteEntry> autocomplete(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        HashMap<String, OmniAutoCompleteEntry> ret = new HashMap<String, OmniAutoCompleteEntry>();

        String[] split = input.split(" ");

        for (String s : split) {
            HashMap<String, OmniAutoCompleteEntry> tmp = new HashMap<String, OmniAutoCompleteEntry>();

            if (s.length() < MINIMUM_AUTOCOMPLETE_PROMPT) {
                Log.d(TAG, "Autocomplete component "+s+" shorter than min chars "+MINIMUM_AUTOCOMPLETE_PROMPT);
                continue;
            }
            try {
                Log.d(TAG, "Autocomplete searching for "+s);
                String request = makeAutoCompleteNameRequest(s);
                Cursor cursor = db.rawQuery(request, null);
                cursor.moveToFirst();
                Log.d(TAG, "Autocomplete returned "+cursor.getCount()+" entries for "+s);

                while (!cursor.isAfterLast()) {
                    int nameColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
                    int idColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPID);

                    String stopName = cursor.getString(nameColumnIndex);
                    String stopID = cursor.getString(idColumnIndex);

                    // Each station entrance in Metro has its own stopID.
                    // Duplicates have letters at the end; originals are straight digits.
                    // Only add the originals
                    if (stopID.matches("\\d+$")) {
                        // Try to only put stuff in once
                        if (!tmp.containsKey(stopName)) {
                            tmp.put(stopName, new OmniAutoCompleteEntry(stopName, 1));
                        }
                    }
                    cursor.moveToNext();
                }
            } catch (CursorIndexOutOfBoundsException e) {
                ret = null;
            }

            for (Map.Entry<String, OmniAutoCompleteEntry> entry : tmp.entrySet()) {
                if (ret.containsKey(entry.getKey())) {
                    ret.get(entry.getKey()).addPriority(1.0f);
                    Log.v(TAG, "Added priority: "+entry.getKey()+" to "+ret.get(entry.getKey()).getPriority() + " from " + s);
                } else {
                    ret.put(entry.getKey(), entry.getValue());
                }

            }

        }

        Tracking.sendTime("SQL", "StopNames", "getAutocomplete", t);
        Log.d(TAG,"Got autocomplete for "+input+", "+ ret.size()+" matches");

        return ret.values();
    }

    @Override
    public String getStopName(String stopID) {
        if (stopID == null || stopID.isEmpty()) {
            return "Bad stopname request";
        }

        String ret = null;

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        Collection<String> matching = getStringsFromSQL(makeStopNameRequest(stopID), db, StopNameEntry.COLUMN_NAME_STOPNAME);

        if (matching.size() > 0) {
            ret = matching.iterator().next();
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

        ret = getStringsFromSQL(makeStopIDRequest(stopName), db, StopNameEntry.COLUMN_NAME_STOPID);

        cleanStopIDs(ret);

        Tracking.sendTime("SQL", "StopNames", "getStopID", t);
        Log.d(TAG,"Got stopID for "+stopName+", "+ ret);

        return ret;
    }

    // Metro labels individual station entrances with their own stopids
    // These stopids end with a letter, eg 80213A, B etc.
    // We don't want these duplicates, so remove anything that isn't just a straight number.
    private void cleanStopIDs(Collection<String> ret) {
        ArrayList<String> rem = new ArrayList<String>();
        for (String s : ret) {
            if (!s.matches("\\d+$")) {
                rem.add(s);
            }
        }
        ret.removeAll(rem);
    }

    private Collection<String> getStringsFromSQL(String query, SQLiteDatabase db, String columnName) {
        Collection<String> ret = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(columnName);
                String lookedUp = cursor.getString(idColumnIndex);

                ret.add(lookedUp);

                cursor.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            ret = null;
        }
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
