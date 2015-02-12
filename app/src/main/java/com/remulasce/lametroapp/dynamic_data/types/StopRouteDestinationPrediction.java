package com.remulasce.lametroapp.dynamic_data.types;

import android.util.Log;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* This thing makes all the StopRouteDestinationArrivals each stop could need.
That means it's named slightly wrong. This thing does more than one stoproutedestinationprediction.
 */
public class StopRouteDestinationPrediction extends Prediction {
    public static final String TAG = "SRDPrediction";
    protected final int MINIMUM_UPDATE_INTERVAL = 5000;
    protected final int INTERVAL_INCREASE_PER_SECOND = 50;

    protected Stop stop;
    protected Route route;
    protected TripUpdateCallback callback;

    protected boolean inScope = false;

//    final Map< Destination, Arrival > trackedArrivals = new HashMap< Destination, Arrival >();
//    final Collection<Arrival> trackedArrivals = new ArrayList<Arrival>();
    Collection<StopRouteDestinationArrival> trackedArrivals = new ArrayList<StopRouteDestinationArrival>();

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
    public void restoreTrips() {
        for ( StopRouteDestinationArrival arrival : trackedArrivals ) {
            arrival.setScope(true);
        }
    }

    @Override
    public void cancelTrips() {
        inScope = false;
        for (StopRouteDestinationArrival e : trackedArrivals) {
            e.setScope( false );
        }
    }

    @Override
    public void startPredicting() {
        Log.d(TAG, "StartPredicting SRDP");
        synchronized ( trackedArrivals ) {
            inScope = true;
            PredictionManager.getInstance().startTracking( this );

            for (StopRouteDestinationArrival arrival : trackedArrivals ) {
//                arrival.setScope(true);
            }
        }
    }

    @Override
    public void stopPredicting() {
        inScope = false;
        PredictionManager.getInstance().stopTracking( this );

        for (StopRouteDestinationArrival arrival : trackedArrivals ) {
//            arrival.setScope(false);
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
                                arrival.getRoute().equals(newA.getRoute()) &&
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
                        newSRDA.setScope(inScope);
                        trackedArrivals.add(newSRDA);

                        a = newSRDA;
                    }
                }
            }
        }
        //Then update all the destinations we have
        for (StopRouteDestinationArrival a : trackedArrivals) {
            a.updateArrivalTimes(arrivals);
            callback.tripUpdated(a.getTrip());
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
        StopRouteDestinationArrival first = null;

        float firstTime;
        float interval = 0;

        // We find the soonest arrival and use the interval for that to make sure it gets
        // updated as often as it needs.
        for ( StopRouteDestinationArrival a : trackedArrivals ) {
            if ( first == null
                    || a.getRequestedUpdateInterval() < first.getRequestedUpdateInterval() )
            {
                if ( a.getRequestedUpdateInterval() != -1 ) {
                    first = a;
                }
            }
        }


        if ( first == null ) {
            interval = 15 * INTERVAL_INCREASE_PER_SECOND;
        } else {
            interval = first.getRequestedUpdateInterval();
        }

        Log.v(TAG, "GetRequestedUpdateInterval SRDArrival "+interval);
        return (int) Math.max( MINIMUM_UPDATE_INTERVAL, interval );
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

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        // default serialization
//        oos.defaultWriteObject();
        // write the object
        oos.writeObject(stop);
        oos.writeObject(route);
        oos.writeObject(trackedArrivals);
        oos.writeBoolean(inScope);
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        // default deserialization
//        ois.defaultReadObject();

        stop = (Stop) ois.readObject();
        route = (Route) ois.readObject();
        trackedArrivals = (Collection<StopRouteDestinationArrival>) ois.readObject();
        inScope = ois.readBoolean();
    }
}
