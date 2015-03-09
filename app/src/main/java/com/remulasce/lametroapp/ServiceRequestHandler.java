package com.remulasce.lametroapp;

import com.remulasce.lametroapp.analytics.Log;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.display.PredictionUI;
import com.remulasce.lametroapp.dynamic_data.types.Trip;
import com.remulasce.lametroapp.dynamic_data.types.TripUpdateCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by nighelles on 3/7/2015.
 */

public class ServiceRequestHandler {
    private static final String TAG = "ServiceRequestHandler";

    private final static int UPDATE_INTERVAL = 1000;
    private final Object waitLock = new Object();

    private final List<Trip> activeTrips = new CopyOnWriteArrayList< Trip >();

    private UpdateRunner updateRunner;
    private Thread updateThread;
    private boolean running = false;

    private final List< ServiceRequest > serviceRequests = new CopyOnWriteArrayList< ServiceRequest >();

    public ServiceRequestHandler( ) {

    }

    final Comparator<Trip> tripPriorityComparator = new Comparator<Trip>() {
        @Override
        public int compare(Trip lhs, Trip rhs) {
            return (lhs.getPriority() < rhs.getPriority()) ? 1 : -1;
        }
    };

    List<Trip> sortTrips(Collection<Trip> trips) {
        // Ugh.
        // But, we used to do this literally inside the UI update thread.
        // So we're coming out a little ahead.
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

    public void StartPopulating() {
        if ( running ) {
            Log.e( TAG, "Started an already-populating populator" );
            return;
        }
        Log.d( TAG, "Starting TripPopulator" );
        running = true;

        updateRunner = new UpdateRunner();
        updateThread = new Thread( updateRunner, "UpdateRunner" );

        updateThread.start();
    }

    public void StopPopulating() {
        Log.d( TAG, "Stopping TripPopulator" );

        if (!running) {
            Log.e( TAG, "Stopping an already-stopped populator");
            return;

        }
        updateRunner.run = false;
        running = false;
    }

    void rawSetServiceRequests(Collection<ServiceRequest> requests) {
        Log.d(TAG, "Setting service requests");

        serviceRequests.clear();
        serviceRequests.addAll(requests);

        synchronized (waitLock) {
            waitLock.notify();
        }
    }

    public void SetServiceRequests( Collection<ServiceRequest> requests) {
        Log.d(TAG, "SetServiceRequests on "+requests.size()+" requests");
        rawSetServiceRequests(requests);
    }

    /* UpdateRunner checks our (stops) list every couple seconds to remove old stops and update the display.
    * It removes stops that are no longer active, according to the stop.
    * Then it pushes the Trips it has received asynchronously in the tripUpdateCallback to the ListView.
    *
    * Relevant structures:
    * stops -List of what should be tracked set by the TripPopulater / user
    * trackedMap -Map linking each stop to what is actually tracked by that stop.
    *
    * The "Map" part gets used to directly tell the Prediction to stop tracking.
    *
    * The real deal is when a new stop from stops is not found ind trackedMap. When it gets added, it gets
    * activated and given the tripUpdateCallback.
    *
    * */
    protected class UpdateRunner implements Runnable {
        boolean run = true;

        final Map<ServiceRequest, Collection<PredictionUI> > trackedMap = new HashMap< ServiceRequest, Collection<PredictionUI> >();

        // Track timing
        long timeSpentUpdating = 0;
        long numberOfUpdates = 0;

        long timeSpentUpdatingUI = 0;

        @Override
        public void run() {
            Log.i( TAG, "UpdateRunner starting" );

            while ( run ) {
                // Don't update while an item is dismissing.

                long t = Tracking.startTime();

                updateTrackedMap();
                cullInvalidTrips();

                timeSpentUpdating += Tracking.timeSpent(t);
                numberOfUpdates++;

                if (numberOfUpdates > 50) {
                    long timeSpent = timeSpentUpdating / numberOfUpdates;
                    Tracking.sendRawUITime("TripPopulator", "Averaged update time", timeSpent);

                    long timeSpentUI = timeSpentUpdatingUI / numberOfUpdates;
                    Tracking.sendRawUITime("TripPopulator", "Averaged UI update time", timeSpentUI);
                    numberOfUpdates = 0;
                    timeSpentUpdating = 0;
                    timeSpentUpdatingUI = 0;
                }

                try {
                    synchronized (waitLock) {
                        waitLock.wait(UPDATE_INTERVAL);
                    }
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
            Log.i( TAG, "UpdateRunner ending" );
        }

        // If we have new stops, set them to track and add them to the trackedMap.
        // If stops have been removed, do the opposite.
        void updateTrackedMap() {
            Log.v(TAG, "Updating Tracked Map");

            removeOldStops();
            addNewStops();
        }

        private void addNewStops() {
            Collection<ServiceRequest> newRequests = new ArrayList<ServiceRequest>();
            // Add new stops
            for ( ServiceRequest request : serviceRequests) {
                if ( !trackedMap.containsKey( request ) ) {
                    newRequests.add(request);
                }
            }
        }

        private void removeOldStops() {
            // Remove stops that are no longer tracked
            ArrayList< ServiceRequest > rem = new ArrayList< ServiceRequest >();
            // check what stops we have mapped that are no longer in UI
            for ( Entry< ServiceRequest, Collection<PredictionUI> > t : trackedMap.entrySet() ) {
                boolean stillTracked = false;
                for ( ServiceRequest s : serviceRequests) {
                    if ( s == t.getKey() ) {
                        stillTracked = true;
                        break;
                    }
                }
                if ( !stillTracked ) {
                    rem.add( t.getKey() );
                }
            }
        }

        // The Trip will know when its parent request has been removed.
        void cullInvalidTrips() {
            List< Trip > inactiveTrips = new ArrayList< Trip >();
            for ( Trip t : activeTrips ) {
                if ( !t.isValid() ) {
                    inactiveTrips.add( t );
                }
            }
            activeTrips.removeAll( inactiveTrips );
        }

        private boolean couldServiceRequestsHavePending() {
            for (ServiceRequest r : serviceRequests) {
                if (r.hasTripsToDisplay()) {
                    return true;
                }
            }

            return false;
        }


        // Tracked requests send us this when they get or update data.
        // In here, new Trips are added to our list of active Trips
        final TripUpdateCallback tripUpdateCallback = new TripUpdateCallback() {
            @Override
            public void tripUpdated( final Trip trip ) {
                if ( !trip.isValid() ) {
                    Log.d(TAG, "Skipped invalid trip " + trip.getInfo());
                    activeTrips.remove( trip );
                    return;
                }
                if ( !activeTrips.contains( trip ) ) {
                    activeTrips.add( trip );
                    Log.d(TAG, "Adding trip to activetrips");
                } else {
                    Log.v(TAG, "Active trip updated");
                }

            }
        };
    }


}
