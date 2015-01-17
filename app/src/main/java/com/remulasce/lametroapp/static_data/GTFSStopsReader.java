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
import com.remulasce.lametroapp.types.BasicLocation;
import com.remulasce.lametroapp.types.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.types.Stop;

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
public class GTFSStopsReader extends SQLiteOpenHelper
        implements StopNameTranslator, OmniAutoCompleteProvider, StopLocationTranslator {
    private static final String TAG = "StopNameSQLHelper";

    private static final int MINIMUM_AUTOCOMPLETE_PROMPT = 3;

    private static final String DATABASE_NAME = "StopNames.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StopNameEntry.TABLE_NAME + " (" +
                    StopNameEntry._ID + " INTEGER PRIMARY KEY," +
                    StopNameEntry.COLUMN_NAME_STOPID + TEXT_TYPE + COMMA_SEP +
                    StopNameEntry.COLUMN_NAME_STOPNAME + TEXT_TYPE + COMMA_SEP +
                    StopNameEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    StopNameEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StopNameEntry.TABLE_NAME;

    public static abstract class StopNameEntry implements BaseColumns {
        public static final String TABLE_NAME = "stopnames";
        public static final String COLUMN_NAME_STOPID = "stopid";
        public static final String COLUMN_NAME_STOPNAME = "stopname";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
    private static String[] projection = {
            StopNameEntry.COLUMN_NAME_STOPID,
            StopNameEntry.COLUMN_NAME_STOPNAME,
            StopNameEntry.COLUMN_NAME_LATITUDE,
            StopNameEntry.COLUMN_NAME_LONGITUDE
    };

    private class SQLEntry {
        public String stopID;
        public String stopName;
        public String latitude;
        public String longitude;
    }

    private Context context;

    public GTFSStopsReader(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void initialize() {
        Log.d(TAG, "StopName table forcing initialization check");

        // Getting the database should force its creation via onCreate if it has not yet been
        // created.
        this.getReadableDatabase();
        Log.d(TAG, "StopName table initialization checked");
    }

    @Override
    public BasicLocation getStopLocation(Stop stop) {
        if (stop == null) {
            return null;
        }

        BasicLocation ret = null;
        Long t = Tracking.startTime();

        Log.d(TAG, "StopLocation searching for "+stop);
        Collection<SQLEntry> entries = getMatchingEntriesRaw(makeStopLocationRequest(stop.getStopID()), getReadableDatabase());
        Log.d(TAG, "StopLocation found "+entries.size()+" for "+stop);

        if (entries.size() > 0 && entries.iterator().hasNext()) {
            SQLEntry firstLoc = entries.iterator().next();
            String latitude = firstLoc.latitude;
            String longitude = firstLoc.longitude;

            ret = new BasicLocation(latitude, longitude);
        }

        Tracking.sendTime("SQL", "StopNames", "getLocation", t);
        Log.d(TAG,"Got location for "+stop+", "+ ret.latitude + ", " + ret.longitude);

        return ret;
    }

    private boolean badQueryInput(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        // We rely upon the rest of the thing for real checking.
        // Check for % just prevents ridiculously large return sets
        if (input.contains("\'") || input.contains("%")) {
            return true;
        }

        return false;
    }

    private Collection<SQLEntry> getAutoCompleteEntries(SQLiteDatabase db, String stopName) {
        return getMatchingEntries(StopNameEntry.TABLE_NAME, makeAutoCompleteNameParameterizedSelection(),
                new String[] { "%"+stopName+"%" }, db);
    }

    // Gets all of the name-based autocomplete results.
    // Should read all available data off the SQL table so we don't have to come back later.
    @Override
    public Collection<OmniAutoCompleteEntry> autocomplete(String input) {
        if (badQueryInput(input)) {
            return new ArrayList<OmniAutoCompleteEntry>();
        }

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        HashMap<String, OmniAutoCompleteEntry> ret = new HashMap<String, OmniAutoCompleteEntry>();

        String[] split = input.split(" ");

        for (String s : split) {
            HashMap<String, OmniAutoCompleteEntry> tmp = new HashMap<String, OmniAutoCompleteEntry>();

            if (s.length() < MINIMUM_AUTOCOMPLETE_PROMPT) {
                Log.d(TAG, "Autocomplete component " + s + " shorter than min chars " + MINIMUM_AUTOCOMPLETE_PROMPT);
                continue;
            }

            Log.d(TAG, "Autocomplete searching for " + s);
//            Collection<SQLEntry> matchingEntries = getMatchingEntriesRaw(makeAutoCompleteNameRequest(s), db);
            Collection<SQLEntry> matchingEntries = getAutoCompleteEntries(db, s);
            Log.d(TAG, "Autocomplete returned " + matchingEntries.size() + " entries for " + s);

            for (SQLEntry entry : matchingEntries) {
                // Each station entrance in Metro has its own stopID.
                // Duplicates have letters at the end; originals are straight digits.
                // Only add the originals
                if (entry.stopID.matches("\\d+$")) {
                    // Try to only put stuff in once
                    if (!tmp.containsKey(entry.stopName)) {
                        OmniAutoCompleteEntry newEntry = new OmniAutoCompleteEntry(entry.stopName, 1);
                        Stop newStop = new Stop(entry.stopID);
                        newStop.setLocation(new BasicLocation(entry.latitude, entry.longitude));
                        newEntry.setStop(newStop);
                        tmp.put(entry.stopName, newEntry);
                    }
                }
            }

            for (Map.Entry<String, OmniAutoCompleteEntry> entry : tmp.entrySet()) {
                if (ret.containsKey(entry.getKey())) {
                    ret.get(entry.getKey()).addPriority(1.0f);
                    Log.v(TAG, "Added priority: " + entry.getKey() + " to " + ret.get(entry.getKey()).getPriority() + " from " + s);
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
        if (badQueryInput(stopID)) {
            return null;
        }

        String ret = null;

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

//        Collection<String> matching = getStringsFromSQL(makeStopNameRequest(stopID), db, StopNameEntry.COLUMN_NAME_STOPNAME);
        Collection<SQLEntry> matching = getMatchingEntries(StopNameEntry.TABLE_NAME, makeStopNameParameterizedSelection(),
                new String[] {stopID}, db);

        if (matching.size() > 0) {
            ret = matching.iterator().next().stopName;
        }
        Tracking.sendTime("SQL", "StopNames", "getStopName", t);
        Log.d(TAG,"Got stopname for "+stopID+", "+ ret);

        return ret;
    }

    @Override
    public Collection<String> getStopID(String stopName) {
        if (badQueryInput(stopName)) {
            return null;
        }

        Long t = Tracking.startTime();
        SQLiteDatabase db = getReadableDatabase();

        Collection<String> ret = new ArrayList<String>();
        Collection<SQLEntry> matching;

        matching = getMatchingEntries(StopNameEntry.TABLE_NAME, makeStopIDParameterizedSelection(),
                new String[] {stopName}, db);

        for (SQLEntry each : matching) {
            ret.add(each.stopID);
        }

        cleanStopIDs(ret);

        Tracking.sendTime("SQL", "StopNames", "getStopID", t);
        Log.d(TAG,"Got stopID for "+stopName+", "+ ret);

        return ret;
    }

    // Metro labels individual station entrances with their own stopids
    // These stopids end with a letter, eg 80213A, B etc.
    // We don't want these duplicates, so remove anything that isn't just a straight number.
    private boolean isCleanStopID(String stopID) {
         return (stopID.matches("\\d+$"));
    }

    private void cleanStopIDs(Collection<String> ret) {
        ArrayList<String> rem = new ArrayList<String>();
        for (String s : ret) {
            if (!isCleanStopID(s)) {
                rem.add(s);
            }
        }
        ret.removeAll(rem);
    }

    // Get all matching SQL entries, using injection-safe queries.
    private Collection<SQLEntry> getMatchingEntries(String table, String selection, String[] args, SQLiteDatabase db) {
        Collection<SQLEntry> ret = new ArrayList<SQLEntry>();

        try {
            Cursor cursor = db.query(table, null, selection, args, null, null, null);

            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {

                int idColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPID);
                int nameColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
                int latitudeColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_LATITUDE);
                int longitudeColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_LONGITUDE);

                String stopName = cursor.getString(nameColumnIndex);
                String stopID = cursor.getString(idColumnIndex);
                String latitude = cursor.getString(latitudeColumnIndex);
                String longitude = cursor.getString(longitudeColumnIndex);

                SQLEntry add = new SQLEntry();

                add.stopID = stopID;
                add.stopName = stopName;
                add.latitude = latitude;
                add.longitude = longitude;

                ret.add(add);

                cursor.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            ret = null;
        }
        return ret;
    }

    // General "Give us all we've got" entry retrieval, using unsafe raw queries
    private Collection<SQLEntry> getMatchingEntriesRaw(String query, SQLiteDatabase db) {
        Collection<SQLEntry> ret = new ArrayList<SQLEntry>();

        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {

                int idColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPID);
                int nameColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_STOPNAME);
                int latitudeColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_LATITUDE);
                int longitudeColumnIndex = cursor.getColumnIndexOrThrow(StopNameEntry.COLUMN_NAME_LONGITUDE);

                String stopName = cursor.getString(nameColumnIndex);
                String stopID = cursor.getString(idColumnIndex);
                String latitude = cursor.getString(latitudeColumnIndex);
                String longitude = cursor.getString(longitudeColumnIndex);

                SQLEntry add = new SQLEntry();

                add.stopID = stopID;
                add.stopName = stopName;
                add.latitude = latitude;
                add.longitude = longitude;

                ret.add(add);

                cursor.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            ret = null;
        }
        return ret;
    }

    // Only returns strings from this one column.
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
                String latitude = split[4];
                String longitude = split[5];

                putNewStopDef(sqLiteDatabase, stopID, stopName, latitude, longitude);
                entries++;

                if (entries % 1000 == 0) {
                    Log.d(TAG, "Still updating database; "+ entries+ " entries so far");
                }
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
    private String makeStopLocationRequest(String stopID) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + StopNameEntry.COLUMN_NAME_STOPID +
                " LIKE \'" + stopID + "\'";
    }
    // Request for a stopname, given stopid
    private String makeStopNameRequest(String stopID) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + StopNameEntry.COLUMN_NAME_STOPID +
                " LIKE \'" + stopID + "\'";
    }
    private String makeStopNameParameterizedSelection() {
        return StopNameEntry.COLUMN_NAME_STOPID + " LIKE ?";
    }

    // Request for a stopid, given stopname
    private String makeStopIDRequest(String stopName) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + StopNameEntry.COLUMN_NAME_STOPNAME +
                " LIKE \'" + stopName + "\'";
    }
    private String makeStopIDParameterizedSelection() {
        return StopNameEntry.COLUMN_NAME_STOPNAME + " LIKE ?";
    }
    // Request for matching stopnames
    private String makeAutoCompleteNameRequest(String stopName) {
        return "SELECT * FROM " + StopNameEntry.TABLE_NAME +
                " WHERE " + makeAutoCompleteNameSelection(stopName);
    }
    private String makeAutoCompleteNameSelection(String stopName) {
        return StopNameEntry.COLUMN_NAME_STOPNAME +
                " LIKE \'%" + stopName + "%\'";
    }
    private String makeAutoCompleteNameParameterizedSelection() {
        return StopNameEntry.COLUMN_NAME_STOPNAME + " LIKE ?";
    }

    private void putNewStopDef(SQLiteDatabase sqLiteDatabase, String stopID, String stopName, String latitude, String longitude) {
        Log.v(TAG, "Putting new stop def, "+stopID+", "+stopName);

        ContentValues values = new ContentValues();
        values.put(StopNameEntry.COLUMN_NAME_STOPID, stopID);
        values.put(StopNameEntry.COLUMN_NAME_STOPNAME, stopName);
        values.put(StopNameEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(StopNameEntry.COLUMN_NAME_LONGITUDE, longitude);

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
