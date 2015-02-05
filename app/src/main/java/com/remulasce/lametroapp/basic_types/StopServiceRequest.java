package com.remulasce.lametroapp.basic_types;

import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.StopPrediction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/26/2015.
 *
 * It's a service request added to show what vehicles are arriving.
 */
public class StopServiceRequest extends ServiceRequest {

    Collection<Stop> stops;
    Collection<Prediction> predictions = new ArrayList<Prediction>();

    public StopServiceRequest() {}
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
    public Collection<Prediction> makePredictions() {
        // Assume Stop
        if (!isValid()) {
            return null;
        }

        if (predictions.isEmpty()) {
            for (Stop s : stops) {
                predictions.add(new StopPrediction(s, null));
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
//        return new ArrayList<String>(stops);
    }
}
