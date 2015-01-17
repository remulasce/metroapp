package com.remulasce.lametroapp.components.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.BasicLocation;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;
import com.remulasce.lametroapp.basic_types.Stop;

/**
 * Created by Remulasce on 1/7/2015.
 */
public class MetroLocationRetriever implements LocationRetriever {
    static String TAG = "LocationRetriever";

    StopLocationTranslator locationTranslator;
    LocationManager locationManager;

    GoogleApiClient mGoogleApiClient;

    Location lastRetrievedLocation;

    public MetroLocationRetriever(Context c, StopLocationTranslator locations) {
        this.locationTranslator = locations;

        startLocationRequests(c);
    }

    // Because of power concerns, we only can do location when allowed to do so.
    public void startLocating(Context c) {
        Log.i(TAG, "Starting Location service");
        startLocationRequests(c);
    }

    public void stopLocating(Context c) {
        Log.i(TAG, "Stopping location service");
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, locationListener);
        } catch (IllegalStateException e) {
            Log.w(TAG, "Stopping unstarted location service");
        }
    }

    protected synchronized void startLocationRequests(Context c) {
        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        Log.d(TAG, "startLocationRequests");
    }

    protected GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                lastRetrievedLocation = lastLocation;
            }

            LocationRequest request = new LocationRequest();
            request.setInterval(15 * 1000);
            request.setFastestInterval(1000);
            request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, locationListener);
            } catch (Exception e) {
                Log.w(TAG, "Location failed in onConnected");
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.w(TAG, "OnConnectionSuspended");

        }
    };

    protected LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Received new location "+location);
            lastRetrievedLocation = location;
        }
    };

    protected GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.w(TAG, "Location reading failed");
        }
    };

    private Location getLocation(LocationManager manager) {
        Log.i(TAG, "LocationRetriever getting new location");
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        return manager.getLastKnownLocation(provider);
    }

    private Location getBestLocation() {
        return lastRetrievedLocation;
    }

    @Override
    public double getCurrentDistanceToStop(Stop stop) {
        long t = Tracking.startTime();

        Location currentLoc = getBestLocation();
        if (currentLoc == null) {
            Log.d(TAG, "Current location unavailable");
            return -1;
        }

//        Log.v(TAG, "__time1 "+Tracking.timeSpent(t));

        BasicLocation stopRawLoc = stop.getLocation();
        double stopLatitude = Double.valueOf(stopRawLoc.latitude);
        double stopLongitude = Double.valueOf(stopRawLoc.longitude);

//        Log.v(TAG, "__time2 "+Tracking.timeSpent(t));

        float[] results = new float[4];
        Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(),
                stopLatitude, stopLongitude, results);

        Log.v(TAG, "____stop took "+Tracking.timeSpent(t) + "ms, Returned distance: "+results[0]);

        return results[0];
    }
}
