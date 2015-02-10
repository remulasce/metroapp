package com.remulasce.lametroapp.dynamic_data.types;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* This thing makes all the StopRouteDestinationArrivals each stop could need.
That means it's named slightly wrong. This thing does more than one stoproutedestinationprediction.
 */
public class StopRouteDestinationPrediction extends Prediction {
    protected final int MINIMUM_UPDATE_INTERVAL = 5000;
    protected final int INTERVAL_INCREASE_PER_SECOND = 50;

    protected Stop stop;
    protected Route route;
    protected TripUpdateCallback callback;

    protected boolean inScope = false;

//    final Map< Destination, Arrival > trackedArrivals = new HashMap< Destination, Arrival >();
//    final Collection<Arrival> trackedArrivals = new ArrayList<Arrival>();
    final Collection<StopRouteDestinationArrival> trackedArrivals = new ArrayList<StopRouteDestinationArrival>();

    Arrival firstArrival;
    Trip firstTrip;

    long lastUpdate;
    boolean inUpdate = false;

    public StopRouteDestinationPrediction(Stop stop, Route route) {
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
            for (StopRouteDestinationArrival e : trackedArrivals) {
                e.setScope( true );
            }
        }
    }

    @Override
    public void stopPredicting() {
        inScope = false;
        PredictionManager.getInstance().stopTracking( this );
        
        for (StopRouteDestinationArrival e : trackedArrivals) {
            e.setScope( false );
        }
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

        List<Arrival> arrivals = LaMetroUtil.parseAllArrivals(response);

        // First, add new destinations if we find any.
        for (Arrival newA : arrivals) {
            newA.setScope(inScope);
            if (arrivalTracked(newA)) {
                StopRouteDestinationArrival a = null;

                synchronized (trackedArrivals) {
                    for (StopRouteDestinationArrival arrival : trackedArrivals) {
                        if (arrival.getDirection().equals(newA.getDirection()) &&
                                arrival.getStop().equals(newA.getStop())) {
                            a = arrival;
                            break;
                        }
                    }
                }

                if (a == null) {
                    synchronized (trackedArrivals) {
                        StopRouteDestinationArrival newSRDA = new StopRouteDestinationArrival(
                                newA.getStop(), newA.getRoute(), newA.getDirection());
                        trackedArrivals.add(newSRDA);

                        a = newSRDA;
                    }
                }
            }
        }
        //Then update all the destinations we have
        for (StopRouteDestinationArrival a : trackedArrivals) {
            a.updateArrivalTimes(arrivals);
//            callback.tripUpdated(a.getTrip());
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

    @Override
    public int getRequestedUpdateInterval() {
        Arrival first = null;
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
