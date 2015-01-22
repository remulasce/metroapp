package com.remulasce.lametroapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.trip_list.TripListAdapter;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.Trip;
import com.remulasce.lametroapp.dynamic_data.types.TripUpdateCallback;
import com.remulasce.lametroapp.basic_types.ServiceRequest;

import org.w3c.dom.Text;

public class TripPopulator {
    private static final String TAG = "TripPopulator";

    protected final static int UPDATE_INTERVAL = 2000;

    protected ListView list;
    protected TextView hint;
    protected ArrayAdapter< Trip > adapter;
    protected final List< Trip > activeTrips = new CopyOnWriteArrayList< Trip >();
    
    protected Handler uiHandler;
    protected UpdateRunner updateRunner;
    protected Thread updateThread;
    protected boolean running = false;

    protected final List< ServiceRequest > serviceRequests = new CopyOnWriteArrayList< ServiceRequest >();

    public TripPopulator( ListView list, TextView hint ) {
        this.list = list;
        this.hint = hint;
        this.uiHandler = new Handler( Looper.getMainLooper() );

//        adapter = new ArrayAdapter< Trip >( list.getContext(), R.layout.trip_item );
        adapter = new TripListAdapter( list.getContext(), R.layout.trip_item);
        list.setAdapter(adapter);
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

    protected void rawSetServiceRequests( Collection<ServiceRequest> requests) {
        Log.d(TAG, "Setting service requests");

        synchronized (serviceRequests) {
            serviceRequests.clear();
            serviceRequests.addAll(requests);
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
        protected boolean run = true;

        Map<ServiceRequest, Prediction > trackedMap = new HashMap< ServiceRequest, Prediction >();

        @Override
        public void run() {
            Log.i( TAG, "UpdateRunner starting" );

            while ( run ) {
                updateTrackedMap();
                cullInvalidTrips();

                updateListView();

                try {
                    Thread.sleep( UPDATE_INTERVAL );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
            Log.i( TAG, "UpdateRunner ending" );
        }

        // If we have new stops, set them to track and add them to the trackedMap.
        // If stops have been removed, do the opposite.
        protected void updateTrackedMap() {
            Log.v( TAG, "Updating Tracked Map" );

            removeOldStops();
            addNewStops();
        }

        private void addNewStops() {
            Collection<ServiceRequest> newRequests = new ArrayList<ServiceRequest>();
            // Add new stops
            synchronized (serviceRequests) {
                for ( ServiceRequest request : serviceRequests) {
                    if ( !trackedMap.containsKey( request ) ) {

                        newRequests.add(request);
                    }
                }
            }

            for (ServiceRequest request: newRequests) {
                Prediction prediction = request.makePrediction();
                prediction.setTripCallback(tripUpdateCallback);

                prediction.startPredicting();
                trackedMap.put(request, prediction);
            }
        }

        private void removeOldStops() {
            synchronized (serviceRequests) {
                // Remove stops that are no longer tracked
                ArrayList< ServiceRequest > rem = new ArrayList< ServiceRequest >();
                // check what stops we have mapped that are no longer in UI
                for ( Entry< ServiceRequest, Prediction > t : trackedMap.entrySet() ) {
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

                // deactivate and remove out-scoped stops
                for ( ServiceRequest s : rem ) {
                    Prediction p = trackedMap.get( s );
                    p.stopPredicting();
                    trackedMap.remove(s);
                }
            }
        }

        // The Trip will know when its parent request has been removed.
        protected void cullInvalidTrips() {
            synchronized ( activeTrips ) {
                List< Trip > inactiveTrips = new ArrayList< Trip >();
                for ( Trip t : activeTrips ) {
                    if ( !t.isValid() ) {
                        inactiveTrips.add( t );
                    }
                }
                activeTrips.removeAll( inactiveTrips );
            }
        }

        // Actually push what happened to the user
        private void updateListView() {
            uiHandler.post( new Runnable() {
                @Override
                public void run() {
                    long start = Tracking.startTime();

                    adapter.clear();
                    adapter.addAll( activeTrips );
                    adapter.sort( tripPriorityComparator );
                    adapter.notifyDataSetChanged();

                    if (activeTrips.size() == 0) {
                        hint.setVisibility(View.VISIBLE);
                    } else {
                        hint.setVisibility(View.INVISIBLE);
                    }

                    Tracking.sendUITime( "TripPopulator", "Refresh TripList", start );
                }
            } );
        }

        Comparator<Trip> tripPriorityComparator = new Comparator<Trip>() {
            @Override
            public int compare(Trip lhs, Trip rhs) {
                return (lhs.getPriority() < rhs.getPriority()) ? 1 : -1;
            }
        };


        // Tracked requests send us this when they get or update data.
        // In here, new Trips are added to our list of active Trips
        protected TripUpdateCallback tripUpdateCallback = new TripUpdateCallback() {
            @Override
            public void tripUpdated( final Trip trip ) {
                if ( !trip.isValid() ) {
                    Log.v( TAG, "Skipped invalid trip " + trip.getInfo() );
                    synchronized ( activeTrips ) {
                        activeTrips.remove( trip );
                    }
                    return;
                }
                synchronized ( activeTrips ) {
                    if ( !activeTrips.contains( trip ) ) {
                        activeTrips.add( trip );
                    }
                }
            }
        };
    }
}
