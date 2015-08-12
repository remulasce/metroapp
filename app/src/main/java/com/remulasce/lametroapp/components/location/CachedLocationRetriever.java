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
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Remulasce on 4/4/2015.
 *
 * CachedLocationRetriever solves the problem of Trips using different current locations for
 *   their priorities, due to them checking in for updated locations indepentently.
 *
 * This was due to performance concerns of the previous location retriever, which would
 *   lag the system if every trip checked in at once every refresh.
 *
 * Here we cache distances to various locations, allowing trips to checkin from us every update.
 *
 * When we receive new locations from the system, we will update all of the cached distances ourselves.
 * That makes actual access faster and also ensures every distance is based off proximity to the
 *   same location.
 *
 * In-between app runs we persist our most recent location. This will probably be wrong between loads,
 *   but makes it more predictable what will appear first on load.
 *
 */
public class CachedLocationRetriever implements LocationRetriever {
    private static final String TAG = "LocationRetriever";
    public static final int CACHE_UPDATE_INTERVAL = 5000;

    private GoogleApiClient mGoogleApiClient;
    private Location lastRetrievedLocation;

    private long lastCacheUpdate;
    ConcurrentHashMap<BasicLocation, CachedProximity> cache = new ConcurrentHashMap<BasicLocation, CachedProximity>();


    private class CachedProximity {
        private CachedProximity(double distance) { this.distance = distance; }
        private double distance; // meters
    }

    public CachedLocationRetriever(Context c) {
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

                newLocationAvailable(lastLocation);
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

    private void newLocationAvailable(Location location) {
        if (System.currentTimeMillis() > lastCacheUpdate + CACHE_UPDATE_INTERVAL) {
            lastRetrievedLocation = location;

            updateCachedDistances();
        }
    }

    private void updateCachedDistances() {
        Log.d(TAG, "Updating cached proximities");
        lastCacheUpdate = System.currentTimeMillis();
        
        for (Map.Entry<BasicLocation, CachedProximity> entry : cache.entrySet()) {
            entry.getValue().distance = getRawCurrentDistance(entry.getKey());
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Received new location "+location);
            newLocationAvailable(location);
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

        BasicLocation stopRawLoc = stop.getLocation();
        double distance = getCachedDistanceTo(stopRawLoc);

        Log.v(TAG, "____stop took "+Tracking.timeSpent(t) + "ms, Returned distance: "+distance);
        Tracking.averageUITime("MetroLocationRetriever", "getCurrentDistanceToStop", t);

        return distance;
    }

    private boolean cacheHasLocation(BasicLocation location) {
        return cache.containsKey(location);
    }

    private double getCachedDistance(BasicLocation location) {
        return cache.get(location).distance;
    }

    private void addToCache(BasicLocation location, double distance) {
        cache.put(location, new CachedProximity(distance));
    }

    private double getCachedDistanceTo(BasicLocation stopRawLoc) {
        if (stopRawLoc == null) {
            Log.w(TAG, "Stop didn't have a location, can't provide distance to.");
            return -1;
        }


        if (cacheHasLocation(stopRawLoc)) {
            return getCachedDistance(stopRawLoc);
        }
        else {
            float distance = getRawCurrentDistance(stopRawLoc);

            addToCache(stopRawLoc, distance);

            return distance;
        }

    }

    private float getRawCurrentDistance(BasicLocation stopRawLoc) {
        Location currentLoc = getBestLocation();
        if (currentLoc == null) {
            Log.d(TAG, "Current location unavailable");
            return -1;
        }

        double stopLatitude = stopRawLoc.latitude;
        double stopLongitude = stopRawLoc.longitude;

        float[] results = new float[4];
        Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(),
                stopLatitude, stopLongitude, results);

        return results[0];
    }

    @Override
    public double getCurrentDistanceToLocation(BasicLocation location) {
        return getCachedDistanceTo(location);
    }

    // This just returns the last / best location we've retrieved.
    // Might not actually exist, you should check.
    @Override
    public BasicLocation getCurrentLocation() {
        if (lastRetrievedLocation == null) {
            Log.d(TAG, "Returning possibly bad (null) cached location");
            return null;
        }
        return new BasicLocation(lastRetrievedLocation.getLatitude(), lastRetrievedLocation.getLongitude());
    }
}
