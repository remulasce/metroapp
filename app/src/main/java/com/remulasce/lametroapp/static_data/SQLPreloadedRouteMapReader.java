package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Shape;
import com.remulasce.lametroapp.java_core.basic_types.ShapePoint;
import com.remulasce.lametroapp.java_core.basic_types.ShapePoints;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Reads the routemap database file preloaded in assets.
 *
 * Extracts all the good parts out of the sql
 */
public class SQLPreloadedRouteMapReader extends SQLiteAssetHelper implements RouteMapFiller{
    private static final int DATABASE_VERSION = 2;
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

    public class ShapeDistance {
        public ShapeDistance(Shape shape, double dst) {
            this.shape = shape;
            this.dst = dst;
        }

        public Shape shape;
        public double dst;
    }

    /**
     * Gets all shapes of trips that service this stop.
     *
     * Include the distance this stop is into the shape (dst), so you can tell where to begin
     * drawing the shape.
     */
    @Override
    public Collection<ShapeDistance> getShapeIndexesForStop(Stop stop) {
        Collection<ShapeDistance> ret = new ArrayList<>();

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM stopshapes" +
                    " WHERE stopid = '" + stop.getStopID() + "'", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int shapeIdColumnIndex = cursor.getColumnIndexOrThrow("shapeid");

                String shapeId = cursor.getString(shapeIdColumnIndex);

                // TODO: Replace 0 with actual distance. VTA didn't supply these...
                ret.add(new ShapeDistance(new Shape(shapeId), 0));

                cursor.moveToNext();
            }

            cursor.close();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Get the shape points for a shape.
     * @param shapeId The shape to get points for
     * @param dst The distance into the shape after which to start getting points. Unused for now.
     *
     * @return The ShapePoints structure which represents the known shape trip will take.
     */
    @Override
    public ShapePoints getShapePoints(String shapeId, double dst) {
        ShapePoints ret = new ShapePoints();
        ret.shapeId = shapeId;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM shapepoints" +
                    " WHERE shapeid = '" + shapeId + "'" +
                    " ORDER BY distanceintoshapeft ASC", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int distanceCol = cursor.getColumnIndexOrThrow("distanceintoshapeft");
                int latituteCol = cursor.getColumnIndexOrThrow("latitude");
                int longitudeCol = cursor.getColumnIndexOrThrow("longitude");

                double distance = cursor.getDouble(distanceCol);
                double latitude = cursor.getDouble(latituteCol);
                double longitude = cursor.getDouble(longitudeCol);

                // TODO: Consider the original distance. Not yet provided to us....
                ret.points.add(new ShapePoint(new BasicLocation(latitude, longitude), distance));

                cursor.moveToNext();
            }

            cursor.close();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
