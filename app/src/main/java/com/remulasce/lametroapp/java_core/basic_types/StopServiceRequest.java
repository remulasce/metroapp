package com.remulasce.lametroapp.java_core.basic_types;

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
import java.util.Collection;
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

        if (statusTrip != null && statusTrip.isValid() // TripPopulator can't handle empty / null Trips
                && determineNetworkStatusState() == NetworkStatusState.ERROR || determineNetworkStatusState() == NetworkStatusState.SPINNER) {
//                ) { // Testing, keep the status up.
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
        ERROR
    }

    // Figure out if we should show an error message, progress bar, or nothing.
    NetworkStatusState determineNetworkStatusState() {
        boolean anyFetching = false;
        boolean anyGood = false;
        boolean anyCached = false;
        boolean anyBad = false;

        for (Prediction p : predictions) {
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

        // Otherwise don't display anything special.
        return NetworkStatusState.NOTHING;
    }

    public String getAgencyName() {
        return "Network";
    }

    public NetworkStatusState getNetworkStatus() {
        return determineNetworkStatusState();
    }
}
