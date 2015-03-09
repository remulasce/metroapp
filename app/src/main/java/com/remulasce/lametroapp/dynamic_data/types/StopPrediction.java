package com.remulasce.lametroapp.dynamic_data.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;

/* You make a stop prediction. It gets all the arrivals at a stop, based
 * on input. Etc.
 */
public class StopPrediction extends Prediction {
    private final int MINIMUM_UPDATE_INTERVAL = 5000;
    private final int INTERVAL_INCREASE_PER_SECOND = 50;

    private final Stop stop;
    private final Route route;
    private TripUpdateCallback callback;
    
    private boolean inScope = false;

//    final Map< Destination, Arrival > trackedArrivals = new HashMap< Destination, Arrival >();
    private final Collection<Arrival> trackedArrivals = new ArrayList<Arrival>();

    private final Arrival firstArrival;
    private final Trip firstTrip;

    private long lastUpdate;
    private boolean inUpdate = false;

    public StopPrediction( Stop stop, Route route ) {
        this.stop = stop;
        this.route = route;
        this.firstArrival = new Arrival();
        this.firstTrip = new Trip();
    }

    @Override
    public void startPredicting() {
        synchronized ( trackedArrivals ) {
            inScope = true;
            PredictionManager.getInstance().startTracking( this );
            
//            for (Entry<Destination, Arrival> e : trackedArrivals.entrySet()) {
//            for (Arrival e : trackedArrivals) {
//                e.setScope( true );
//            }
        }
    }

    @Override
    public void stopPredicting() {
        inScope = false;
        PredictionManager.getInstance().stopTracking( this );
        
//        for (Arrival e : trackedArrivals) {
//            e.setScope( false );
//        }
    }

    @Override
    public void restoreTrips() {
        firstArrival.setScope(true);
    }

    @Override
    public void cancelTrips() {
        for (Arrival e : trackedArrivals) {
            e.setScope( false );
        }
    }

    @Override
    public boolean isInScope() {
        return inScope;
    }

    @Override
    public boolean hasAnyPredictions() {
        return firstArrival.isInScope();
    }

    @Override
    public void setTripCallback( TripUpdateCallback callback ) {
        this.callback = callback;

    }

    @Override
    public String getRequestString() {
        return LaMetroUtil.makePredictionsRequest( stop, route );
    }

    public Stop getStop() {
        return stop;
    }

    @Override
    public long getTimeSinceLastUpdate() {
        if ( inUpdate ) {
            return 0;
        }
        return System.currentTimeMillis() - lastUpdate;
    }

    boolean arrivalTracked(Arrival a) {
        if ( !LaMetroUtil.isValidRoute( route ) ) {
            return true;
        }
        if ( a.getRoute().equals( route ) ) {
            return true;
        }

        return false;
    }

    @Override
    public void handleResponse( String response ) {
        lastUpdate = System.currentTimeMillis();

        List< Arrival > arrivals = LaMetroUtil.parseAllArrivals( response );

        for ( Arrival newA : arrivals ) {
            newA.setScope( inScope );
            if ( arrivalTracked( newA ) ) {
                Arrival a = null;

                synchronized ( trackedArrivals ) {
                    for (Arrival arrival : trackedArrivals) {
                        if (arrival.getDirection().equals( newA.getDirection() ) &&
                                arrival.getStop().equals( newA.getStop() ) &&
                                arrival.getVehicleNum().equals( newA.getVehicleNum() )) {
                            a = arrival;
                            break;
                        }
                    }
                }

                if ( a == null ) {
                    synchronized ( trackedArrivals ) {
                        trackedArrivals.add( newA );
                    }
                    a = newA;
                }
                else {
                    a.setEstimatedArrivalSeconds( newA.getEstimatedArrivalSeconds() );
                }
                callback.tripUpdated( a.getFirstTrip() );
            }
        }
    }

    @Override
    public void setUpdated() {
        synchronized ( this ) {
            inUpdate = false;
            this.lastUpdate = System.currentTimeMillis();
        }
    }

    public Route getRoute() {
        return route;
    }

    Arrival firstArrival() {
        Arrival first = null;
        synchronized ( trackedArrivals ) {
            for ( Arrival a : trackedArrivals ) {
                if ( first == null
                        || a.getEstimatedArrivalSeconds() < first.getEstimatedArrivalSeconds() )
                {
                    if ( a.getEstimatedArrivalSeconds() != -1 ) {
                        first = a;
                    }
                }
            }
        }
        return first;
    }

    @Override
    public int getRequestedUpdateInterval() {
        Arrival first = firstArrival();
        int firstTime;

        if ( first == null ) {
            firstTime = 15;
        } else {
            firstTime = (int) first.getEstimatedArrivalSeconds();
        }

        return Math.max( MINIMUM_UPDATE_INTERVAL, firstTime * INTERVAL_INCREASE_PER_SECOND );
    }

    @Override
    public void setGettingUpdate() {
        synchronized ( this ) {
            inUpdate = true;
        }

    }

    public int hashCode() {
        return ( stop.getString() + route.getString() ).hashCode();
    }
}
