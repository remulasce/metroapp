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
    private static final String TAG = "SRDPrediction";
    private final int MINIMUM_UPDATE_INTERVAL = 5000;

    private Stop stop;
    private Route route;
    private PredictionUpdateCallback callback;

    private boolean inScope = false;

    private boolean needsQuickUpdate = false;

    private Collection<StopRouteDestinationArrival> trackedArrivals = new ArrayList<StopRouteDestinationArrival>();

    private long lastUpdate;
    private boolean inUpdate = false;

    public StopRouteDestinationPrediction(Stop stop, Route route) {
        this.stop = stop;
        this.route = route;
    }

    @Override
    public void restoreTrips() {
//        inScope = true;
        for ( StopRouteDestinationArrival arrival : trackedArrivals ) {
            arrival.setScope(true);
        }
        needsQuickUpdate = true;
    }

    @Override
    public void cancelTrips() {
        inScope = false;
        for (StopRouteDestinationArrival e : trackedArrivals) {
            e.setScope( false );
        }
        stopPredicting();
    }

    @Override
    public boolean isInScope() {
        return inScope;
    }

    @Override
    public boolean hasAnyPredictions() {
        for (StopRouteDestinationArrival arrival : trackedArrivals) {
            if (arrival.isInScope()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void startPredicting() {
        Log.d(TAG, "StartPredicting SRDP");
        synchronized ( trackedArrivals ) {
            inScope = true;
            PredictionManager.getInstance().startTracking( this );
        }
    }

    @Override
    public void stopPredicting() {
        inScope = false;
        PredictionManager.getInstance().stopTracking( this );
    }

    @Override
    public void setUpdateCallback(PredictionUpdateCallback callback) {
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
                    }
                }
            }
        }

        //Then update all the destinations we have
        for (StopRouteDestinationArrival a : trackedArrivals) {
            a.updateArrivalTimes(arrivals);
        }

        // Let the UI wrapper know we've been updated.
        callback.predictionUpdated(this);
    }
    @Override
    public void setUpdated() {
        synchronized ( this ) {
            inUpdate = false;
            needsQuickUpdate = false;

            this.lastUpdate = System.currentTimeMillis();
        }
    }

    public Route getRoute() {
        return route;
    }

    public Collection<StopRouteDestinationArrival> getArrivals() {
        return trackedArrivals;
    }

    @Override
    public int getRequestedUpdateInterval() {
        if (needsQuickUpdate) {
            return 0;
        }

        StopRouteDestinationArrival first = null;

        float interval;

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
            interval = 30 * 1000;
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

        oos.writeObject(stop);
        oos.writeObject(route);
        oos.writeObject(trackedArrivals);
        oos.writeBoolean(inScope);
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        stop = (Stop) ois.readObject();
        route = (Route) ois.readObject();
        try {
            trackedArrivals = (Collection<StopRouteDestinationArrival>) ois.readObject();
        } catch (Exception e) {
            trackedArrivals = new ArrayList<StopRouteDestinationArrival>();
            e.printStackTrace();
            Log.w(TAG, "Couldn't load tracked arrivals, making empty list");
        }
        inScope = ois.readBoolean();
    }
}
