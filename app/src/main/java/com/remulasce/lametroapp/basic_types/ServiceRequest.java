package com.remulasce.lametroapp.basic_types;

import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.StopPrediction;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Remulasce on 12/16/2014.
 */
public abstract class ServiceRequest implements Serializable {
    String displayName = "ServiceRequest";
    boolean inScope = true;


    public ServiceRequest() {}
    public ServiceRequest(String s) {
        this.displayName = s;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void descope() {
        inScope = false;
    }

    public abstract boolean hasTripsToDisplay();

    public boolean isInScope() {
        return inScope;
    }

    public String getDisplayName() { return displayName; }

    //Returns if the service request makes any sense to fulfill
    public boolean isValid() {
        if ( displayName == null || displayName.isEmpty()) { return false; }

        return true;
    }

    public abstract Collection<Prediction> makePredictions();
    public abstract void restoreTrips();
    public abstract void cancelRequest();

    public abstract boolean updateAvailable();
    public abstract void updateTaken();

    // This is used for serialization, because we didn't do an actual good job with serialization.
    public abstract Collection<String> getRaw();

    @Override
    public String toString() {
        return displayName;
    }
}
