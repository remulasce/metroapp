package com.remulasce.lametroapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.components.trip_list.TripListAdapter;
import com.remulasce.lametroapp.display.AndroidDisplay;
import com.remulasce.lametroapp.display.AndroidMultiArrivalDisplay;
import com.remulasce.lametroapp.dynamic_data.types.MultiArrivalTrip;
import com.remulasce.lametroapp.dynamic_data.types.Trip;
import com.remulasce.lametroapp.libraries.SwipeDismissListViewTouchListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripPopulator {
    private static final String TAG = "TripPopulator";

    private final static int UPDATE_INTERVAL = 1000;
    private final Object waitLock = new Object();
    // When the swipe-to-dismiss library is working, it really doesn't want the list
    private boolean dismissLock = false;
    // When scrolling, don't update, because that causes visible jitter.
    private boolean scrollLock = false;

    private final ServiceRequestHandler requests;

    private final ListView list;
    private final TextView hint;
    private final ProgressBar progress;
    private final ArrayAdapter< AndroidDisplay > adapter;

    private final Handler uiHandler;
    private UpdateRunner updateRunner;
    private Thread updateThread;
    private boolean running = false;

    private long lastDismissTutorialShow = 0;
    private final SwipeDismissListViewTouchListener dismissListener;
    private final AbsListView.OnScrollListener scrollListener;
    private final AdapterView.OnItemClickListener itemClickListener;

    private final Context c;

    public TripPopulator( ServiceRequestHandler requests, ListView list, TextView hint, ProgressBar progress, Context c ) {
        this.requests = requests;
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
                                    try {
                                        AndroidDisplay item = adapter.getItem(position);
                                        Trip t = item.getTrip();

                                        t.dismiss();
                                        adapter.remove(item);

                                        dismissLock = false;

                                        if (System.currentTimeMillis() > lastDismissTutorialShow + 60000) {
                                            Toast.makeText(context, "Trip Dismissed.\nTap the stop name in the top window to restore trips", Toast.LENGTH_LONG).show();
                                            lastDismissTutorialShow = System.currentTimeMillis();
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        Log.w(TAG, "Tried to dismiss out-of-bounds trip");
                                        Tracking.sendEvent("TripPopulator", "Dismiss Trip", "Index out of bounds");
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onBeginDismiss(ListView listView) {
                                dismissLock = true;
                            }
                        });

        final AbsListView.OnScrollListener dismissScroll = dismissListener.makeScrollListener();
        scrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                dismissScroll.onScrollStateChanged(absListView, i);

                if (i == SCROLL_STATE_IDLE) {
                    scrollLock = false;
                } else {
                    scrollLock = true;
                }

                Log.v(TAG, "Scroll state: "+i);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                dismissScroll.onScroll(absListView, i, i2, i3);
            }
        };

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AndroidDisplay item = (AndroidDisplay) adapterView.getItemAtPosition(i);
                item.executeAction(context);
            }
        };

        list.setOnTouchListener(dismissListener);
        list.setOnScrollListener(scrollListener);
        list.setOnItemClickListener(itemClickListener);
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

        // Track timing
        long timeSpentUpdating = 0;
        long numberOfUpdates = 0;

        long timeSpentUpdatingUI = 0;

        @Override
        public void run() {
            Log.i( TAG, "UpdateRunner starting" );

            while ( run ) {
                // Don't update while an item is dismissing.
                if (dismissLock || scrollLock) {
                    continue;
                }

                long t = Tracking.startTime();

                updateListView();

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


        // Actually push what happened to the user
        private void updateListView() {
            // We literally used to do this inside the ui update thread.
            final List<Trip> sorted = requests.GetSortedTripList();

            uiHandler.post( new Runnable() {
                @Override
                public void run() {
                    long start = Tracking.startTime();

                    adapter.clear();
                    for (Trip t : sorted) {
                        if (t.isValid()) {
                            adapter.add(new AndroidMultiArrivalDisplay( (MultiArrivalTrip) t) );
                        }
                    }

                    if (sorted.size() == 0 ) {
                        hint.setVisibility(View.VISIBLE);

                        /*
                        if (serviceRequests.size() != 0) {// && couldServiceRequestsHavePending()) {
                            progress.setVisibility(View.VISIBLE);
                            progress.setProgress(1);
                        }
                        else {
                            progress.setVisibility(View.INVISIBLE);
                        }
                        */

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
                            uiHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (TripPopulator.this.running) {
                                        Toast.makeText(c, "Swipe an arrival to dismiss it", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, 5000);
                        }

                        progress.setVisibility(View.INVISIBLE);
                    }

                    long l = Tracking.timeSpent(start);
                    timeSpentUpdatingUI += l;
                }
            } );
        }
    }
}
