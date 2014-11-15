package com.remulasce.lametroapp.pred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import types.Destination;
import types.Route;
import types.Stop;

import com.remulasce.lametroapp.LaMetroUtil;

/* You make a stop prediction. It gets all the arrivals at a stop, based
 * on input. Etc.
 */
public class StopPrediction extends Prediction {
    protected final int MINIMUM_UPDATE_INTERVAL = 5000;
    protected final int INTERVAL_INCREASE_PER_SECOND = 100;

    protected Stop stop;
    protected Route route;
    protected TripUpdateCallback callback;

    Map< Destination, Arrival > directionMap = new HashMap< Destination, Arrival >();

    Arrival firstArrival;
    Trip firstTrip;

    long lastUpdate;
    boolean inUpdate = false;

    public StopPrediction( Stop stop, Route route ) {
        this.stop = stop;
        this.route = route;
        this.firstArrival = new Arrival();
        this.firstTrip = new Trip();
    }

    @Override
    public void startPredicting() {
        PredictionManager.getInstance().startTracking( this );
    }

    @Override
    public void stopPredicting() {
        PredictionManager.getInstance().stopTracking( this );
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

    protected boolean arrivalTracked( Arrival a ) {
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
            if ( arrivalTracked( newA ) ) {
                Arrival a = directionMap.get( newA.getDirection() );
                if ( a == null ) {
                    directionMap.put( newA.getDirection(), newA );
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

    protected Arrival firstArrival() {
        Arrival first = null;
        for ( Arrival a : directionMap.values() ) {
            if ( first == null
                    || a.getEstimatedArrivalSeconds() < first.getEstimatedArrivalSeconds() )
            {
                if ( a.getEstimatedArrivalSeconds() != -1 ) {
                    first = a;
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
            firstTime = first.getEstimatedArrivalSeconds();
        }

        return Math.max( MINIMUM_UPDATE_INTERVAL, firstTime * INTERVAL_INCREASE_PER_SECOND );
    }

    @Override
    public void setGettingUpdate() {
        synchronized ( this ) {
            inUpdate = true;
        }

    }

    @Override
    public List< Trip > getAllSentTrips() {
        List< Trip > ret = new ArrayList< Trip >();

        for ( Entry< Destination, Arrival > e : directionMap.entrySet() ) {
            ret.add( e.getValue().getFirstTrip() );
        }

        return ret;
    }

}
