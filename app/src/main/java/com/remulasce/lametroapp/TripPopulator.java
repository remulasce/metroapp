package com.remulasce.lametroapp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.pred.StopPrediction;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.pred.TripUpdateCallback;
import com.remulasce.lametroapp.types.Stop;

public class TripPopulator {
    private static final String TAG = "TripPopulator";

    protected final static int UPDATE_INTERVAL = 2000;

    protected ListView list;
    protected ArrayAdapter< Trip > adapter;
    protected final List< Trip > activeTrips = new CopyOnWriteArrayList< Trip >();
    
    Map< Stop, StopPrediction > stopMap = new HashMap< Stop, StopPrediction >();

    protected Handler uiHandler;
    protected UpdateRunner updateRunner;
    protected Thread updateThread;
    protected boolean running = false;

    protected final List< Stop > stops = new CopyOnWriteArrayList< Stop >();

    public TripPopulator( ListView list ) {
        this.list = list;
        this.uiHandler = new Handler( Looper.getMainLooper() );

        adapter = new ArrayAdapter< Trip >( list.getContext(), android.R.layout.simple_list_item_1 );
        list.setAdapter( adapter );
    }

    public void StartPopulating() {
        if ( running ) {
            Log.e( TAG, "Started an already-populating populator" );
            return;
        }
        Log.d( TAG, "Starting TripPopulator" );
        running = true;

        updateRunner = new UpdateRunner();
        updateThread = new Thread( updateRunner );

        updateThread.start();
    }

    public void StopPopulating() {
        Log.d( TAG, "Stopping TripPopulator" );
        updateRunner.run = false;
        running = false;
    }

    protected void setStops( String rawStops ) {
        if ( rawStops == null ) {
            return;
        }
        String[] split = rawStops.split( " " );

        long start = Tracking.startTime();
        synchronized ( stops ) {
            Tracking.sendTime( "Synchronization", "Acquire Delay" , "Stops", start );
            Tracking.sendUITime( "Sync Delay" , "SetStops", start );
            start = Tracking.startTime();
            
            // Remove old stops
            List< Stop > rem = new ArrayList< Stop >();
            for ( Stop r : stops ) {
                boolean stillTracked = false;
                for ( String s : split ) {
                    if ( r.getString().equals( s ) ) {
                        stillTracked = true;
                        break;
                    }
                }
                if ( !stillTracked ) {
                    rem.add( r );
                }
            }
            stops.removeAll( rem );

            // Add new Stops
            for ( String s : split ) {
                Stop Stop = new Stop( s );
                if ( LaMetroUtil.isValidStop( s ) ) {

                    boolean alreadyIn = false;
                    for ( Stop r : stops ) {
                        if ( Stop.getString().equals( r.getString() ) ) {
                            alreadyIn = true;
                            break;
                        }
                    }
                    if ( !alreadyIn ) {
                        stops.add( Stop );
                    }
                }
            }
        }
        Tracking.sendTime( "Synchronization", "Release Delay", "Stops", start );
    }

    public void StopSelectionChanged( String stopName ) {
        Log.d( TAG, "Stop changed: " + stopName );

        setStops(stopName);
    }

    protected class UpdateRunner implements Runnable {
        protected boolean run = false;

        protected Semaphore updateAvailable = new Semaphore( 1 );

        protected void updateList() {
            Log.v( TAG, "Updating predictions" );

            // Add new stops
            synchronized ( stops ) {
                for ( Stop stop : stops ) {
                    if ( !stopMap.containsKey( stop ) ) {
                        StopPrediction stopPrediction = new StopPrediction( stop, null );
                        stopPrediction.setTripCallback( callback );

                        stopMap.put( stop, stopPrediction );
                        stopPrediction.startPredicting();
                    }
                }
            }

            synchronized ( stops ) {
                // Remove stops that are no longer tracked
                ArrayList< Stop > rem = new ArrayList< Stop >();
                // check what stops we have mapped that are no longer in UI
                for ( Entry< Stop, StopPrediction > t : stopMap.entrySet() ) {
                    boolean stillTracked = false;
                    for ( Stop s : stops ) {
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
                for ( Stop s : rem ) {
                    StopPrediction p = stopMap.get( s );
                    p.stopPredicting();
                    stopMap.remove( s );
                }
            }
        }

        protected TripUpdateCallback callback = new TripUpdateCallback() {
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

                updateAvailable.release();
            }
        };

        protected void cullTrips() {

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

        @Override
        public void run() {
            run = true;
            Log.i( TAG, "UpdateRunner starting" );

            while ( run ) {
                updateList();
                cullTrips();

                uiHandler.post( new Runnable() {
                    @Override
                    public void run() {
                        long start = Tracking.startTime();

                        adapter.clear();
                        adapter.addAll( activeTrips );
                        adapter.sort( new Comparator< Trip >() {
                            @Override
                            public int compare( Trip lhs, Trip rhs ) {
                                return ( lhs.getPriority() < rhs.getPriority() ) ? 1 : -1;
                            }
                        } );
                        adapter.notifyDataSetChanged();

                        Tracking.sendUITime( "TripPopulator", "Refresh TripList", start );
                    }
                } );

                try {
                    Thread.sleep( UPDATE_INTERVAL );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

            }
            Log.i( TAG, "UpdateRunner ending" );
        }

    }
}
