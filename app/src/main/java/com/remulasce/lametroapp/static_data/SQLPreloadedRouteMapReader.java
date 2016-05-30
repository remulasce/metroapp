package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Reads the routemap database file preloaded in assets.
 *
 * Extracts all the good parts out of the sql
 */
public class SQLPreloadedRouteMapReader extends SQLiteAssetHelper implements RouteMapFiller{
    private static final int DATABASE_VERSION = 1;
    public static final double ROUGHLY_ONE_MILE_NEAR_SF = 0.010;
    private final String filename;
    private final Agency agency;
    private final Context context;


    public SQLPreloadedRouteMapReader(Context context, String filename, Agency agency) {
        super(context, filename, null, DATABASE_VERSION);

        this.filename = filename;
        this.agency = agency;
        this.context = context;

        // Just rewrite the db when upgrading.
        setForcedUpgrade();
    }

    @Override
    public Collection<Stop> getNearbyStops(BasicLocation loc) {

        ArrayList<Stop> ret = new ArrayList<>();

        double minLat = loc.latitude - ROUGHLY_ONE_MILE_NEAR_SF;
        double maxLat = loc.latitude + ROUGHLY_ONE_MILE_NEAR_SF;
        double minLon = loc.longitude - ROUGHLY_ONE_MILE_NEAR_SF;
        double maxLon = loc.longitude + ROUGHLY_ONE_MILE_NEAR_SF;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM stops" +
                    " WHERE latitude BETWEEN "+minLat+" AND "+maxLat +
                    " AND longitude BETWEEN "+minLon+" AND "+maxLon, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow("stopid");
                int latitudeColumnIndex = cursor.getColumnIndexOrThrow("latitude");
                int longitudeColumnIndex = cursor.getColumnIndexOrThrow("longitude");

                String stopID = cursor.getString(idColumnIndex);
                Double latitude = cursor.getDouble(latitudeColumnIndex);
                Double longitude = cursor.getDouble(longitudeColumnIndex);

                Stop stop = new Stop(stopID);
                stop.setLocation(new BasicLocation(latitude, longitude));

                ret.add(stop);

                cursor.moveToNext();
            }

            cursor.close();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
