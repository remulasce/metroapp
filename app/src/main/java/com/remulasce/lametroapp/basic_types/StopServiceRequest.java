package com.remulasce.lametroapp.basic_types;

import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.StopRouteDestinationArrival;
import com.remulasce.lametroapp.dynamic_data.types.StopRouteDestinationPrediction;
import com.remulasce.lametroapp.dynamic_data.types.Trip;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/26/2015.
 *
 * It's a service request added to show what vehicles are arriving.
 */

public class StopServiceRequest extends ServiceRequest {

    private Collection<Stop> stops;
    private Collection<Prediction> predictions = new ArrayList<Prediction>();

    private boolean updateAvailable = true;

    public StopServiceRequest(Collection<Stop> stops, String displayName) {
        this.stops = stops;
        this.displayName = displayName;
    }
    public StopServiceRequest(Stop stop, String displayName) {
        stops = new ArrayList<Stop>();
        stops.add(stop);

        this.displayName = displayName;
    }

    //Returns if the service request makes any sense to fulfill
    public boolean isValid() {
        if ( stops == null || stops.isEmpty() || stops.contains( null ) || displayName == null || displayName.isEmpty()) { return false; }

        return true;
    }

    @Override
    public Collection<Trip> getTrips() {
        Collection<Trip> trips = new ArrayList<Trip>();

        for (Prediction p : this.makePredictions()) {
            if (p instanceof StopRouteDestinationPrediction) {

                for (StopRouteDestinationArrival srda : ((StopRouteDestinationPrediction)p).getArrivals()) {
                    trips.add(srda.getTrip());
                }
            }
        }

        return trips;
    }

    @Override
    public void cancelRequest() {
        for (Prediction p : predictions) {
            p.cancelTrips();
        }

        predictions.clear();
    }

    @Override
    public void restoreTrips() {
        for (Prediction p : predictions) {
            p.restoreTrips();
        }
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

//    @Override
    public Collection<Prediction> makePredictions() {
        // Assume Stop
        if (!isValid()) {
            return null;
        }

        if (predictions.isEmpty()) {
            for (Stop s : stops) {
                StopRouteDestinationPrediction stopRouteDestinationPrediction = new StopRouteDestinationPrediction(s, null);
                predictions.add(stopRouteDestinationPrediction);
                stopRouteDestinationPrediction.startPredicting();
            }
        }

        return predictions;
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
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        try {
            stops = (Collection<Stop>) ois.readObject();
            predictions = (Collection<Prediction>) ois.readObject();
        } catch (Exception e) {
            stops = new ArrayList<Stop>();
            predictions = new ArrayList<Prediction>();
            e.printStackTrace();
        }
    }
}
