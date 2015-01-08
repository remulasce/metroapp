package com.remulasce.lametroapp.components;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.remulasce.lametroapp.static_data.BasicLocation;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;
import com.remulasce.lametroapp.types.Stop;

/**
 * Created by Remulasce on 1/7/2015.
 */
public class MetroLocationRetriever implements LocationRetriever {
    static String TAG = "LocationRetriever";

    StopLocationTranslator locationTranslator;
    LocationManager locationManager;


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

    private Location getLocation(LocationManager manager) {
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        return manager.getLastKnownLocation(provider);
    }

    @Override
    public double getCurrentDistanceToStop(Stop stop) {
        Log.d(TAG, "Getting distance to stop "+stop);

        BasicLocation stopRawLoc = locationTranslator.getStopLocation(stop);
        double stopLatitude = Double.valueOf(stopRawLoc.latitude);
        double stopLongitude = Double.valueOf(stopRawLoc.longitude);

        Location currentLoc = getLocation(locationManager);

        Log.d(TAG, "Current loc: "+currentLoc.toString() + "\n" +
                "Stop loc: "+stopLatitude+ ", " + stopLongitude);

        float[] results = new float[4];
        Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(),
                stopLatitude, stopLongitude, results);

        Log.d(TAG, "Returned distance: "+results[0]);

        return results[0];
    }
}
