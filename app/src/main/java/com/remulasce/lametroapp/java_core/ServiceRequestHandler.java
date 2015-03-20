package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Trip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by nighelles on 3/7/2015.
 *
 * At this point it's basically just a list of Requests, plus some lifecycle handling.
 * It just also lets you get all total Trips made by those requests.
 *
 */

public class ServiceRequestHandler {
    private static final String TAG = "ServiceRequestHandler";


    private boolean running = false;
    private final List< ServiceRequest > serviceRequests = new CopyOnWriteArrayList< ServiceRequest >();


    final Comparator<Trip> tripPriorityComparator = new Comparator<Trip>() {
        @Override
        public int compare(Trip lhs, Trip rhs) {
            return (lhs.getPriority() < rhs.getPriority()) ? 1 : -1;
        }
    };

    List<Trip> sortTrips(Collection<Trip> trips) {
        List<Trip> sortedTrips = new ArrayList<Trip>(trips);
        Collections.sort(sortedTrips, tripPriorityComparator);

        return sortedTrips;
    }

    public List<Trip> GetSortedTripList() {
        List<Trip> ret = new ArrayList<Trip>();

        for (ServiceRequest request : serviceRequests) {
            ret.addAll(request.getTrips());
        }

        return sortTrips(ret);
    }

    public boolean isRunning() {
        return running;
    }
    public int numRequests() {
        return serviceRequests.size();
    }

    public void StartPopulating() {
        if ( running ) {
            Log.e( TAG, "Started an already-populating populator" );
            return;
        }
        Log.d( TAG, "Starting TripPopulator" );

        for (ServiceRequest r : serviceRequests) {
            r.startRequest();
        }

        running = true;
    }

    public void StopPopulating() {
        Log.d( TAG, "Stopping TripPopulator" );

        if (!running) {
            Log.e( TAG, "Stopping an already-stopped populator");
            return;
        }
        for (ServiceRequest r : serviceRequests) {
            r.pauseRequest();
        }
        running = false;
    }

    void rawSetServiceRequests(Collection<ServiceRequest> requests) {
        Log.d(TAG, "Setting service requests");

        for (ServiceRequest r : requests) {
            if (!serviceRequests.contains(r)) {
                r.startRequest();
            }
        }

        serviceRequests.clear();
        serviceRequests.addAll(requests);
    }

    // This helps with automated testing.
    public Collection<ServiceRequest> getRequests() {
        return serviceRequests;
    }

    public void SetServiceRequests( Collection<ServiceRequest> requests) {
        Log.d(TAG, "SetServiceRequests on "+requests.size()+" requests");
        rawSetServiceRequests(requests);
    }
}
