package com.remulasce.lametroapp.java_core.basic_types;

import android.location.Location;

import com.remulasce.lametroapp.java_core.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.java_core.dynamic_data.types.RequestStatusTrip;
import com.remulasce.lametroapp.java_core.dynamic_data.types.StopRouteDestinationArrival;
import com.remulasce.lametroapp.java_core.dynamic_data.types.StopRouteDestinationPrediction;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Trip;
import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Remulasce on 1/26/2015.
 * <p>
 * It's a service request added to show what vehicles are arriving.
 */

public class StopServiceRequest extends ServiceRequest {

    private static final String TAG = "StopServiceRequest";
    private Collection<Stop> stops;
    private Collection<Prediction> predictions = new ArrayList<Prediction>();

    private boolean updateAvailable = true;
    private RequestStatusTrip statusTrip;

    public StopServiceRequest(Collection<Stop> stops, String displayName) {
        this.stops = stops;
        this.displayName = displayName;

        this.statusTrip = new RequestStatusTrip(this);
    }

    public StopServiceRequest(Stop stop, String displayName) {
        stops = new ArrayList<Stop>();
        stops.add(stop);

        this.displayName = displayName;

        this.statusTrip = new RequestStatusTrip(this);
    }

    //Returns if the service request makes any sense to fulfill
    public boolean isValid() {
        if (stops == null || stops.isEmpty() || stops.contains(null) || displayName == null || displayName.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public Collection<Trip> getTrips() {
        Collection<Trip> trips = new ArrayList<Trip>();

        // "Loading", "Empty", and "Error" states
        if (statusTrip != null && statusTrip.isValid()
                && determineNetworkStatusState() == NetworkStatusState.ERROR
                || determineNetworkStatusState() == NetworkStatusState.SPINNER
                || determineNetworkStatusState() == NetworkStatusState.EMPTY) {
            trips.add(statusTrip);
        }

        for (Prediction p : this.predictions) {
            if (p instanceof StopRouteDestinationPrediction) {
                for (StopRouteDestinationArrival srda : ((StopRouteDestinationPrediction) p).getArrivals()) {
                    trips.add(srda.getTrip());
                }
            }
        }

        return trips;
    }

    @Override
    public void startRequest() {
        super.startRequest();
        if (predictions.size() == 0) {
            makePredictions();
        }

        resumeRequest();
    }

    public void resumeRequest() {
        for (Prediction p : predictions) {
            p.startPredicting();
        }
    }

    @Override
    public void pauseRequest() {
        super.pauseRequest();

        for (Prediction p : predictions) {
            p.stopPredicting();
        }
    }

    @Override
    public void cancelRequest() {
        super.cancelRequest();
        for (Prediction p : predictions) {
            p.cancelTrips();
            p.stopPredicting();
        }

        predictions.clear();
    }

    @Override
    public Collection<BasicLocation> getInterestingLocations() {
        ArrayList<BasicLocation> ret = new ArrayList<>();

        for (Stop stop : stops) {
            ret.add(stop.getLocation());
        }

        return ret;
    }

    @Override
    public Collection<Stop> getStops() {
        return new ArrayList<>(stops);
    }

    @Override
    public void restoreTrips() {
        for (Prediction p : predictions) {
            p.restoreTrips();
        }
        statusTrip.restore();
    }

    @Override
    public boolean updateAvailable() {
        return updateAvailable;
    }

    @Override
    public boolean hasTripsToDisplay() {
        for (Prediction p : predictions) {
            if (p.hasAnyPredictions()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateTaken() {
        updateAvailable = false;
    }

    private void makePredictions() {
        // Assume Stop
        if (!isValid()) {
            Log.w(TAG, "Make predictions in invalid StopServiceRequest");
            return;
        }

        if (predictions.isEmpty()) {
            for (Stop s : stops) {
                StopRouteDestinationPrediction stopRouteDestinationPrediction = new StopRouteDestinationPrediction(s, null);
                predictions.add(stopRouteDestinationPrediction);
//                stopRouteDestinationPrediction.startPredicting();
            }
        }
    }

    @Override
    public Collection<String> getRaw() {
        ArrayList<String> strings = new ArrayList<String>();

        for (Stop s : stops) {
            strings.add(s.getStopID());
        }

        return strings;
    }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {

        oos.writeObject(stops);
        oos.writeObject(predictions);
        oos.writeObject(statusTrip);
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        try {
            stops = (Collection<Stop>) ois.readObject();
            predictions = (Collection<Prediction>) ois.readObject();
            statusTrip = (RequestStatusTrip) ois.readObject();
        } catch (Exception e) {
            stops = new ArrayList<Stop>();
            predictions = new ArrayList<Prediction>();
            statusTrip = new RequestStatusTrip(this);
            e.printStackTrace();
        }
    }

    // For testing purposes, manually set what predictions we have.
    public void testRawSetPredictions(List<Prediction> overridePredictions) {
        this.predictions = overridePredictions;
    }

    public enum NetworkStatusState {
        NOTHING,
        SPINNER,
        ERROR,
        EMPTY
    }

    // Figure out if we should show an error message, progress bar, or nothing.
    NetworkStatusState determineNetworkStatusState() {
        boolean anyFetching = false;    // Any with no arrivals, going to net for first time
        boolean anyGood = false;        // Meaning any successful network fetches
        boolean anyCached = false;      // Network failed, but we have older predictions
        boolean anyBad = false;         // Network failed, and we don't have any predictions.
        boolean anyTrips = false;       // Whether we picked up any time predictions at all. Could
                                        // be good, maybe cached. We care whether this request at
                                        // least has _any_ times to display.

        for (Prediction p : predictions) {
            if (p instanceof StopRouteDestinationPrediction) {
                for (StopRouteDestinationArrival srda : ((StopRouteDestinationPrediction) p).getArrivals()) {
                    anyTrips = true;
                }
            }

            switch (p.getPredictionState()) {
                case GOOD:
                    anyGood = true;
                    break;
                case CACHED:
                    anyCached = true;
                    break;
                case FETCHING:
                    anyFetching = true;
                    break;
                case BAD:
                    anyBad = true;
                    break;
                default:
                    Log.w(TAG, "Unknown prediction state");
                    break;
            }
        }

        // If any part is fetching, we should show spinner, even if we have an error.
        if (anyFetching) {
            return NetworkStatusState.SPINNER;
        }

        // Only if we have full on bad should we display an error.
        if (anyBad && !anyGood && !anyCached) {
            return NetworkStatusState.ERROR;
        }

        // If there's any useful info to display, we don't need anything special.
        if (anyTrips) {
            return NetworkStatusState.NOTHING;
        }

        // But if we really have nothing, we make sure to note that we're basically useless.
        return NetworkStatusState.EMPTY;
    }

    public String getAgencyName() {
        return "network";
    }

    public NetworkStatusState getNetworkStatus() {
        return determineNetworkStatusState();
    }
}
