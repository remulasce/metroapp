package com.remulasce.lametroapp.display;

import com.remulasce.lametroapp.dynamic_data.types.StopRouteDestinationArrival;
import com.remulasce.lametroapp.dynamic_data.types.StopRouteDestinationPrediction;
import com.remulasce.lametroapp.dynamic_data.types.TripUpdateCallback;

import java.util.Collection;

/**
 * Created by Remulasce on 3/5/2015.
 *
 * Takes a StopRouteDestinationPrediction. Makes Trips from it.
 */
public class SRDPUI implements PredictionUI {

    StopRouteDestinationPrediction prediction;

    TripUpdateCallback tripUpdateCallback;

    public SRDPUI( StopRouteDestinationPrediction prediction) {
        this.prediction = prediction;
    }

    @Override
    public void setTripUpdateCallback(TripUpdateCallback t) {
        tripUpdateCallback = t;
    }

    @Override
    public void stopPredicting() {
        prediction.stopPredicting();
    }

    @Override
    public void predictionUpdated() {
        Collection<StopRouteDestinationArrival> arrivals = prediction.getArrivals();
        for (StopRouteDestinationArrival a : arrivals) {

            tripUpdateCallback.tripUpdated(a.getTrip());
        }
    }
}
