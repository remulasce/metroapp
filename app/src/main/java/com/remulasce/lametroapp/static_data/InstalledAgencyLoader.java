package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;

/**
 * Created by Remulasce on 8/23/2015.
 *
 * Given an agency filename, open up the agency overview table and actually make an Agency object for it.
 */
public class InstalledAgencyLoader extends SQLiteAssetHelper {
    /** Note: Must be changed in {@link SQLPreloadedStopsReader#DATABASE_VERSION} as well */
    private static final int DATABASE_VERSION = 16;
    public static final String INFOTABLE = "agencyinfo";
    public static final String TAG = "AgencyLoader";

    private String filename;

    public InstalledAgencyLoader(Context context, String fileName) {
        super(context, fileName, null, DATABASE_VERSION);

        this.filename = fileName;

        // Just rewrite the db when upgrading.
        setForcedUpgrade();
    }


    public Agency getAgency() {
        String query = makeAgencyQuery();

        try {
            Cursor cursor = getReadableDatabase().rawQuery(query, null);

            cursor.moveToFirst();
            if (cursor.isAfterLast()) {
                Log.w(TAG, "database "+filename+"had no agencyinfo table or row!");
                return null;
            }

            //fuckit hardcode
            int agencyidIndex = cursor.getColumnIndexOrThrow("agencyid");
            int agencynameIndex = cursor.getColumnIndexOrThrow("agencyname");
            int latminIndex = cursor.getColumnIndexOrThrow("latMin");
            int latmaxIndex = cursor.getColumnIndexOrThrow("latMax");
            int lonminIndex = cursor.getColumnIndexOrThrow("lonMin");
            int lonmaxIndex = cursor.getColumnIndexOrThrow("lonMax");

            String agencyId = cursor.getString(agencyidIndex);
            String agencyName = cursor.getString(agencynameIndex);
            Double latMin = cursor.getDouble(latminIndex);
            Double latMax = cursor.getDouble(latmaxIndex);
            Double lonMin = cursor.getDouble(lonminIndex);
            Double lonMax = cursor.getDouble(lonmaxIndex);

            Log.d(TAG, "Loaded agency information for "+filename+": "+agencyId+agencyName+latMin+latMax+lonMin+lonMax);

            Agency ret = new Agency(agencyId, agencyName, new BasicLocation(latMin, lonMin), new BasicLocation(latMax, lonMax));

            return ret;

        } catch (CursorIndexOutOfBoundsException e) {
            Log.w(TAG, "Couldn't load agency "+filename);
            return null;
        } catch (SQLiteException e) {
            Log.w(TAG, "Couldn't load agency "+filename);
            e.printStackTrace();
            return null;
        }
    }

    // Just, everything in the info table. Why not.
    private String makeAgencyQuery() {
        return "SELECT * FROM " + INFOTABLE;
    }
}
