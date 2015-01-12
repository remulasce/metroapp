package com.remulasce.lametroapp.components;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.static_data.BasicLocation;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;
import com.remulasce.lametroapp.types.Stop;

import java.util.concurrent.locks.Lock;

/**
 * Created by Remulasce on 1/7/2015.
 */
public class MetroLocationRetriever implements LocationRetriever {
    static String TAG = "LocationRetriever";

    StopLocationTranslator locationTranslator;
    LocationManager locationManager;

    Location lastRetrievedLocation;
    Lock curLocationLock;

    public MetroLocationRetriever(Context c, StopLocationTranslator locations) {
        this.locationTranslator = locations;

        initLocationProvider(c);
    }

    private void initLocationProvider(Context c) {
        Log.d(TAG, "Initializing location provider");
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        //Test location
        Location location = getLocation(locationManager);
        if (location != null) {
            Log.d(TAG, "Location initialized, current location " + location.toString());
        } else {
            Log.w(TAG, "Location unavailable");
        }
    }

    private synchronized Location getLocation(LocationManager manager) {
        if (lastRetrievedLocation != null && lastRetrievedLocation.getTime() + 200000 > System.currentTimeMillis()) {
            return lastRetrievedLocation;
        }

        if (lastRetrievedLocation != null) {
            Log.i(TAG, lastRetrievedLocation.getTime() + ", " + System.currentTimeMillis());
        }

        Log.i(TAG, "LocationRetriever getting new location");
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        lastRetrievedLocation = manager.getLastKnownLocation(provider);
        return lastRetrievedLocation;
    }

    @Override
    public double getCurrentDistanceToStop(Stop stop) {
        Log.d(TAG, "Getting distance to stop "+stop);
        long t = Tracking.startTime();

        Location currentLoc = getLocation(locationManager);
        if (currentLoc == null) {
            Log.d(TAG, "Current location unavailable");
            return -1;
        }

        BasicLocation stopRawLoc = stop.getLocation();
        double stopLatitude = Double.valueOf(stopRawLoc.latitude);
        double stopLongitude = Double.valueOf(stopRawLoc.longitude);

        Log.d(TAG, "Current loc: "+currentLoc.toString() + "\n" +
                "Stop loc: "+stopLatitude+ ", " + stopLongitude);

        float[] results = new float[4];
        Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(),
                stopLatitude, stopLongitude, results);

        Log.d(TAG, "getCurrentDistanceToStop took  "+Tracking.timeSpent(t));
        Log.d(TAG, "Returned distance: "+results[0]);

        return results[0];
    }
}
