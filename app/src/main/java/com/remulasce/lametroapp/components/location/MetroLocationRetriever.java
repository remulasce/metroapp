package com.remulasce.lametroapp.components.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.BasicLocation;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;

/**
 * Gets location from Play Services.
 */
public class MetroLocationRetriever implements LocationRetriever {
    private static final String TAG = "LocationRetriever";

    private GoogleApiClient mGoogleApiClient;

    private Location lastRetrievedLocation;

    public MetroLocationRetriever(Context c, StopLocationTranslator locations) {
        setupLocation(c);
    }

    private void setupLocation(Context c) {
        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }

    // Because of power concerns, we only can do location when allowed to do so.
    public void startLocating(Context c) {
        Log.i(TAG, "Starting Location service");
        startLocationRequests(c);
    }

    public void stopLocating(Context c) {
        Log.i(TAG, "Stopping location service");
        mGoogleApiClient.disconnect();
    }

    synchronized void startLocationRequests(Context c) {
        mGoogleApiClient.connect();
        Log.d(TAG, "startLocationRequests");
    }

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "Location service connected");
                Tracking.sendEvent("Location Service", "OnConnected", "Location Available");

                lastRetrievedLocation = lastLocation;
            } else {
                Tracking.sendEvent("Location Service", "OnConnected", "No Last Location");
                Log.i(TAG, "location service connected, but no location available");
            }

            LocationRequest request = new LocationRequest();
            request.setInterval(60 * 1000);
            request.setFastestInterval(500);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Received new location "+location);
            lastRetrievedLocation = location;
        }
    };

    private final GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.w(TAG, "Location reading failed");
            Tracking.sendEvent("Location Service", "OnConnectionFailed");
        }
    };

    private Location getBestLocation() {
        return lastRetrievedLocation;
    }

    /** Location in meters */
    @Override
    public double getCurrentDistanceToStop(Stop stop) {
        long t = Tracking.startTime();

        if (stop == null || !stop.isValid()) {
            Log.w(TAG, "Invalid stop provided to get distance to");
            return -1;
        }

        Location currentLoc = getBestLocation();
        if (currentLoc == null) {
            Log.d(TAG, "Current location unavailable");
            return -1;
        }

        BasicLocation stopRawLoc = stop.getLocation();
        if (stopRawLoc == null) {
            Log.d(TAG, "Stop didn't have a location, can't provide distance to.");
            return -1;
        }

        double stopLatitude = stopRawLoc.latitude;
        double stopLongitude = stopRawLoc.longitude;

        float[] results = new float[4];
        Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(),
                stopLatitude, stopLongitude, results);

        Log.v(TAG, "____stop took "+Tracking.timeSpent(t) + "ms, Returned distance: "+results[0]);
        Tracking.averageUITime("MetroLocationRetriever", "getCurrentDistanceToStop", t);

        return results[0];
    }
}
