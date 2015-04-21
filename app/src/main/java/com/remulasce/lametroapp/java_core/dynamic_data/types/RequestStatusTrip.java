package com.remulasce.lametroapp.java_core.dynamic_data.types;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;
import com.remulasce.lametroapp.java_core.basic_types.StopServiceRequest;

/**
 * Created by Remulasce on 4/21/2015.
 *
 * This trip conveys special status about active ServiceRequests.
 * It lets the user know when the request is going to network to get predictions
 *   or when an error has occured.
 *
 * It should only be shown when those conditions arise.
 *
 * Conditions are:
 * - Fetching update from server
 * - Error occured from server.
 *
 * It is specific to the StopServiceRequest for now.
 *
 * Pass-through method to the platform-specific display:
 *   getRequestStatus() returns either NOTHING, SPINNER, or ERROR
 *   That denotes whether to display an error message, a progress spinner, or that
 *   actually this trip shouldn't be displayed at all.
 *
 * This kind of trip should probably not be added at all in the NOTHING case. But that's up
 *   to ServiceRequests to ensure.
 *
 * Circular dependency note:
 * It's weird currently to have StopServiceRequest make this kind of trip, then only show it
 *   sometimes.
 * In the future it would be better for this Trip to reference an interface like 'NetworkStatusable'
 *   or something, so this trip could be used to display the status of different kinds of servicerequests.
 *
 *
 * (Really this is just a thin pass-through on the underlying ServiceRequest state)
 */
public class RequestStatusTrip extends Trip{

    private StopServiceRequest parentRequest;

    public enum RequestDisplayType {
        NOTHING,
        SPINNER,
        ERROR
    }

    public RequestStatusTrip(StopServiceRequest parent) {
        this.parentRequest = parent;
    }

    public RequestDisplayType getRequestStatus() {
        switch (parentRequest.getNetworkStatus()) {
            case NOTHING:
                return RequestDisplayType.NOTHING;
            case SPINNER:
                return RequestDisplayType.SPINNER;
            case ERROR:
                return RequestDisplayType.ERROR;
            default:
                Log.w("RequestStatusTrip", "Unknown parent request status");
                return RequestDisplayType.NOTHING;
        }
    }
}
