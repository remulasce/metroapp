package com.remulasce.lametroapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import types.Destination;
import types.Route;
import types.Stop;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.remulasce.lametroapp.pred.StopPrediction;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.pred.TripUpdateCallback;

public class TripPopulator {
    private static final String TAG = "TripPopulator";

    enum TripDriver {
        NONE, ROUTE, STOP
    };

    protected final static int UPDATE_INTERVAL = 2000;

    protected ListView list;
    protected ArrayAdapter< Trip > adapter;
    protected List< Trip > activeTrips = new ArrayList< Trip >();
    protected List< Trip > inactiveTrips = new ArrayList< Trip >();

    protected Handler uiHandler;
    protected UpdateRunner updateRunner;
    protected Thread updateThread;
    protected boolean running = false;

    // These should be replaced by Lists of valid-only routes.
    // protected String routeName;
    // protected String stopName;

    // These should be set to only valid routes.
    protected List< Route > routes = new ArrayList< Route >();
    protected List< Stop > stops = new ArrayList< Stop >();

    public TripPopulator( ListView list ) {
        this.list = list;
        this.uiHandler = new Handler( Looper.getMainLooper() );

        adapter = new ArrayAdapter< Trip >( list.getContext(), android.R.layout.simple_list_item_1 );
        list.setAdapter( adapter );
    }

    public void StartPopulating() {
        if ( running ) {
            Log.w( TAG, "Started an already-populating populator" );
            return;
        }
        running = true;

        updateRunner = new UpdateRunner();
        updateThread = new Thread( updateRunner );

        updateThread.start();
    }

    public void StopPopulating() {
        updateRunner.run = false;
    }

    protected void setRoutes( String rawRoutes ) {
        if ( rawRoutes == null ) {
            return;
        }
        String[] split = rawRoutes.split( " " );

        routes.clear();
        for ( String s : split ) {
            Route route = new Route( s );
            if ( LaMetroUtil.isValidRoute( route ) ) {
                routes.add( route );
            }
        }
    }

    protected void setStops( String rawStops ) {
        if ( rawStops == null ) {
            return;
        }
        String[] split = rawStops.split( " " );

        stops.clear();
        for ( String s : split ) {
            Stop stop = new Stop( s );
            if ( LaMetroUtil.isValidStop( stop ) ) {
                stops.add( stop );
            }
        }
    }

    public void RouteSelectionChanged( String routeName ) {
        Log.d( TAG, "Route changed: " + routeName );

        // this.routeName = routeName;
        setRoutes( routeName );
    }

    public void StopSelectionChanged( String stopName ) {
        Log.d( TAG, "Stop changed: " + stopName );

        // this.stopName = stopName;
        setStops( stopName );
    }

    protected class UpdateRunner implements Runnable {
        protected boolean run = false;

        protected Semaphore updateAvailable = new Semaphore( 1 );

        // protected StopPrediction stopPrediction;
        // protected List<StopPrediction> stopPredictions = new
        // ArrayList<StopPrediction>();

        Map< Stop, StopPrediction > stopMap = new HashMap< Stop, StopPrediction >();

        // protected RoutePrediction routePrediction

        protected void updateList() {
            Log.d( TAG, "Updating predictions" );

            /*
             * for (Entry t : stopMap.entrySet()) {
             * ((StopPrediction)t.getValue()).stopPredicting(); }
             * activeTrips.clear(); stopMap.clear();
             */
            for ( Stop stop : stops ) {
                if ( !stopMap.containsKey( stop ) ) {
                    StopPrediction stopPrediction = new StopPrediction( stop, null );
                    stopPrediction.setTripCallback( callback );

                    stopMap.put( stop, stopPrediction );
                    stopPrediction.startPredicting();
                }
            }

            ArrayList< Stop > rem = new ArrayList< Stop >();
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
            for ( Stop s : rem ) {
                activeTrips.removeAll( stopMap.get( s ).getAllSentTrips() );
                inactiveTrips.addAll( stopMap.get( s ).getAllSentTrips() );
                stopMap.remove( s );
            }
            /*
             * if (stopPrediction != null) { if
             * (stopName.equals(stopPrediction.getStop())) { if
             * (!LaMetroUtil.isValidRoute(routeName)) { return; } if
             * (routeName.equals(stopPrediction.getRouteName())) { return; }
             * 
             * } Log.d(TAG, "Updating stop prediction"); activeTrips.clear();
             * 
             * stopPrediction.stopPredicting(); }
             * 
             * stopPrediction = new StopPrediction( stopName, routeName );
             * stopPrediction.setTripCallback(callback);
             * 
             * stopPrediction.startPredicting();
             */

            Log.d( TAG, "Updating based on stop" );

        }

        protected TripUpdateCallback callback = new TripUpdateCallback() {
            @Override
            public void tripUpdated( final Trip trip ) {
                if ( inactiveTrips.contains( trip ) ) {
                    Log.d( TAG, "Skipped old trip callback" );
                    return;
                }

                if ( !activeTrips.contains( trip ) ) {
                    activeTrips.add( trip );
                }

                updateAvailable.release();
            }
        };

        @Override
        public void run() {
            run = true;
            Log.d( TAG, "UpdateRunner starting" );

            while ( run ) {
                updateList();
                /*
                 * if (LaMetroUtil.isValidRoute(routeName) ||
                 * LaMetroUtil.isValidStop(stopName)) { updateList(); }
                 */

                uiHandler.post( new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        adapter.addAll( activeTrips );
                        adapter.notifyDataSetChanged();
                    }
                } );

                try {
                    Thread.sleep( UPDATE_INTERVAL );
                } catch ( InterruptedException e ) {}

            }
            Log.d( TAG, "UpdateRunner ending" );

        }

    }
}
