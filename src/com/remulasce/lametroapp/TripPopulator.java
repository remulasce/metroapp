package com.remulasce.lametroapp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

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
    protected List< Trip > activeTrips = new CopyOnWriteArrayList< Trip >();

    protected Handler uiHandler;
    protected UpdateRunner updateRunner;
    protected Thread updateThread;
    protected boolean running = false;

    // These should be replaced by Lists of valid-only routes.
    // protected String routeName;
    // protected String stopName;

    // These should be set to only valid routes.
    protected List< Route > routes = new CopyOnWriteArrayList< Route >();
    protected List< Stop > stops = new CopyOnWriteArrayList< Stop >();

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

        synchronized ( routes ) {
            // Remove old routes
            List< Route > rem = new ArrayList< Route >();
            for ( Route r : routes ) {
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
            routes.removeAll( rem );

            // Add new routes
            for ( String s : split ) {
                Route route = new Route( s );
                if ( LaMetroUtil.isValidRoute( route ) ) {

                    boolean alreadyIn = false;
                    for ( Route r : routes ) {
                        if ( route.getString().equals( r.getString() ) ) {
                            alreadyIn = true;
                            break;
                        }
                    }
                    if ( !alreadyIn ) {
                        routes.add( route );
                    }
                }
            }
        }
    }

    protected void setStops( String rawStops ) {
        if ( rawStops == null ) {
            return;
        }
        String[] split = rawStops.split( " " );

        synchronized ( stops ) {
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
                        adapter.clear();
                        adapter.addAll( activeTrips );
                        adapter.sort( new Comparator< Trip >() {
                            @Override
                            public int compare( Trip lhs, Trip rhs ) {
                                return ( lhs.getPriority() < rhs.getPriority() ) ? 1 : -1;
                            }
                        } );
                        adapter.notifyDataSetChanged();
                    }
                } );

                try {
                    Thread.sleep( UPDATE_INTERVAL );
                } catch ( InterruptedException e ) {}

            }
            Log.i( TAG, "UpdateRunner ending" );

        }

    }
}
