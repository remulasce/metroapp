package com.remulasce.lametroapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.trip_list.TripListAdapter;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.Trip;
import com.remulasce.lametroapp.dynamic_data.types.TripUpdateCallback;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.libraries.SwipeDismissListViewTouchListener;

public class TripPopulator {
    private static final String TAG = "TripPopulator";

    protected final static int UPDATE_INTERVAL = 1000;
    protected Object waitLock = new Object();
    // When the swipe-to-dismiss library is working, it really doesn't want the list
    protected boolean dismissLock = false;

    protected ListView list;
    protected TextView hint;
    protected ProgressBar progress;
    protected ArrayAdapter< Trip > adapter;
    protected final List< Trip > activeTrips = new CopyOnWriteArrayList< Trip >();
    
    protected Handler uiHandler;
    protected UpdateRunner updateRunner;
    protected Thread updateThread;
    protected boolean running = false;

    protected long lastDismissTutorialShow = 0;
    protected SwipeDismissListViewTouchListener dismissListener;

    // ugh.
    protected Context c;

    protected final List< ServiceRequest > serviceRequests = new CopyOnWriteArrayList< ServiceRequest >();

    public TripPopulator( ListView list, TextView hint, ProgressBar progress, Context c ) {
        this.list = list;
        this.progress = progress;
        this.hint = hint;
        this.uiHandler = new Handler( Looper.getMainLooper() );
        this.c = c;

        adapter = new TripListAdapter( list.getContext(), R.layout.trip_item);
        list.setAdapter(adapter);

        final Context context = c;

        dismissListener = new SwipeDismissListViewTouchListener(
                        list,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Trip t = adapter.getItem(position);
                                    t.dismiss();
                                    adapter.remove(t);
                                    dismissLock = false;

                                    if (System.currentTimeMillis() > lastDismissTutorialShow + 60000) {
                                        Toast.makeText(context, "Trip Dismissed.\nTap the stop name in the top window to restore trips", Toast.LENGTH_LONG).show();
                                        lastDismissTutorialShow = System.currentTimeMillis();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onBeginDismiss(ListView listView) {
                                dismissLock = true;
                            }
                        });
        list.setOnTouchListener(dismissListener);
        list.setOnScrollListener(dismissListener.makeScrollListener());
    }

    public void StartPopulating() {
        if ( running ) {
            Log.e( TAG, "Started an already-populating populator" );
            return;
        }
        Log.d( TAG, "Starting TripPopulator" );
        running = true;
        dismissLock = false;

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
        protected boolean run = true;

        Map<ServiceRequest, Collection<Prediction> > trackedMap = new HashMap< ServiceRequest, Collection<Prediction> >();

        // Track timing
        protected long timeSpentUpdating = 0;
        protected long numberOfUpdates = 0;

        protected long timeSpentUpdatingUI = 0;

        @Override
        public void run() {
            Log.i( TAG, "UpdateRunner starting" );

            while ( run ) {
                // Don't update while an item is dismissing.
                if (dismissLock) {
                    continue;
                }

                long t = Tracking.startTime();

                updateTrackedMap();
                cullInvalidTrips();

                updateListView();

                try {
                    synchronized (waitLock) {
                        waitLock.wait(UPDATE_INTERVAL);
                    }
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                timeSpentUpdating += Tracking.timeSpent(t);
                numberOfUpdates++;

                if (numberOfUpdates > 50) {
                    Tracking.sendRawUITime("TripPopulater", "Averaged update time", timeSpentUpdating / numberOfUpdates);
                    Tracking.sendRawUITime("TripPopulater", "Averaged UI update time", timeSpentUpdatingUI / numberOfUpdates);
                    numberOfUpdates = 0;
                    timeSpentUpdating = 0;
                    timeSpentUpdatingUI = 0;
                }

            }
            Log.i( TAG, "UpdateRunner ending" );
        }

        // If we have new stops, set them to track and add them to the trackedMap.
        // If stops have been removed, do the opposite.
        protected void updateTrackedMap() {
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

            for (ServiceRequest request: newRequests) {
                Collection<Prediction> predictions = request.makePredictions();

                trackedMap.put(request, predictions);

                for (Prediction prediction : predictions) {
                    prediction.setTripCallback(tripUpdateCallback);
                    prediction.startPredicting();
                }
            }
        }

        private void removeOldStops() {
            // Remove stops that are no longer tracked
            ArrayList< ServiceRequest > rem = new ArrayList< ServiceRequest >();
            // check what stops we have mapped that are no longer in UI
            for ( Entry< ServiceRequest, Collection<Prediction> > t : trackedMap.entrySet() ) {
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
                Collection<Prediction> predictions = trackedMap.get(s);
                for (Prediction p : predictions ) {
                    p.stopPredicting();
                }
                trackedMap.remove(s);
            }
        }

        // The Trip will know when its parent request has been removed.
        protected void cullInvalidTrips() {
            List< Trip > inactiveTrips = new ArrayList< Trip >();
            for ( Trip t : activeTrips ) {
                if ( !t.isValid() ) {
                    inactiveTrips.add( t );
                }
            }
            activeTrips.removeAll( inactiveTrips );
        }

        protected List<Trip> sortTrips(Collection<Trip> trips) {
            // Ugh.
            // But, we used to do this literally inside the UI update thread.
            // So we're coming out a little ahead.
            List<Trip> sortedTrips = new ArrayList<Trip>(trips);
            Collections.sort(sortedTrips, tripPriorityComparator);

            return sortedTrips;
        }

        private boolean couldServiceRequestsHavePending() {
            for (ServiceRequest r : serviceRequests) {
                if (r.hasTripsToDisplay()) {
                    return true;
                }
            }

            return false;
        }

        // Actually push what happened to the user
        private void updateListView() {
            // We literally used to do this inside the ui update thread.
            final List<Trip> sorted = sortTrips(activeTrips);

            uiHandler.post( new Runnable() {
                @Override
                public void run() {
                    long start = Tracking.startTime();

                    adapter.clear();
                    for (Trip t : sorted) {
                        if (t.isValid()) {
                            adapter.add(t);
                        }
                    }

                    if (activeTrips.size() == 0 ) {
                        hint.setVisibility(View.VISIBLE);

                        if (serviceRequests.size() != 0 && couldServiceRequestsHavePending()) {
                            progress.setVisibility(View.VISIBLE);
                            progress.setProgress(1);
                        }
                        else {
                            progress.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        if ( hint.getVisibility() == View.VISIBLE ) {
                            hint.setVisibility(View.INVISIBLE);

                            uiHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (TripPopulator.this.running) {
                                        Toast.makeText(c, "Tap an arrival to set a notification for it", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, 3000);
                        }

                        progress.setVisibility(View.INVISIBLE);
                    }
                    timeSpentUpdatingUI += Tracking.timeSpent(start);
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
