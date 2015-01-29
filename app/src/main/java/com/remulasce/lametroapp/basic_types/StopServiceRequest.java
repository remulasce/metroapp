package com.remulasce.lametroapp.basic_types;

import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.StopPrediction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/26/2015.
 */
public class StopServiceRequest extends ServiceRequest {

    Collection<String> stops;
    Collection<Prediction> predictions = new ArrayList<Prediction>();

    public StopServiceRequest() {}
    public StopServiceRequest(Collection<String> stopIDs, String displayName) {
        this.stops = stopIDs;
        this.displayName = displayName;
    }
    public StopServiceRequest(String stopID, String displayName) {
        stops = new ArrayList<String>();
        stops.add(stopID);

        this.displayName = displayName;
    }

    //Returns if the service request makes any sense to fulfill
    public boolean isValid() {
        if ( stops == null || stops.isEmpty() || stops.contains( null )|| displayName == null || displayName.isEmpty()) { return false; }

        return true;
    }
    @Override
    public Collection<Prediction> makePredictions() {
        // Assume Stop
        if (!isValid()) {
            return null;
        }

        if (predictions.isEmpty()) {
            for (String raw : stops) {
                Stop s = new Stop(raw);
                predictions.add(new StopPrediction(s, null));
            }
        }

        return predictions;
    }

    @Override
    public Collection<String> getRaw() {
        return new ArrayList<String>(stops);
    }
}
