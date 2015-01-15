package com.remulasce.lametroapp.types;

import com.remulasce.lametroapp.pred.Prediction;
import com.remulasce.lametroapp.pred.StopPrediction;

/**
 * Created by Remulasce on 12/16/2014.
 */
public class ServiceRequest {
    String raw = "ServiceRequest";
    String displayName = "ServiceRequest";
    boolean inScope = true;

    public ServiceRequest() {}
    public ServiceRequest(String s) {
        this.raw = s;
        this.displayName = s;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void descope() {
        inScope = false;
    }

    public boolean isInScope() {
        return inScope;
    }

    public String toString() {
        return raw;
    }
    public String getDisplayName() { return displayName; }

    //Returns if the service request makes any sense to fulfill
    public boolean isValid() {
        if (raw == null ||raw.isEmpty() || displayName == null || displayName.isEmpty()) { return false; }

        return true;
    }

    public Prediction makePrediction() {
        // Assume Stop
        Stop s = new Stop(raw);
        StopPrediction prediction = new StopPrediction(s, null);

        return prediction;
    }

    public String getRaw() {
        return raw;
    }
}
