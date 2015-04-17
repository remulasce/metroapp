package com.remulasce.lametroapp.java_core.dynamic_data.types;

import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Remulasce on 2/9/2015.
 *
 * This is all the arrivals of one route to one stop with the same destination.
 * It produces a Trip that shows all arrivals.
 * All the things have to match. So red and purple lines to Union Station from 7th/Metro
 *  require two of these.
 */
public class StopRouteDestinationArrival implements Serializable {
    private final int MINIMUM_UPDATE_INTERVAL = 10000;
    private final int MAXIMUM_UPDATE_INTERVAL = 60000;
    private final int INTERVAL_INCREASE_PER_SECOND = 400;

    private static final String TAG = "SRDArrival";
    final Stop stop;
    final Route route;
    final Destination destination;

    private final Collection<Arrival> arrivals;

    private final Trip trip;

    private boolean isInScope = false;

    public StopRouteDestinationArrival(Stop s, Route r, Destination d) {
        this.stop = s;
        this.route = r;
        this.destination = d;

        arrivals = new CopyOnWriteArrayList<Arrival>();
        trip = new MultiArrivalTrip(this);

        Log.d(TAG, "New StopRouteDestinationArrival: " + s + " " + r + " " + d);
    }

    // In seconds
    public float getRequestedUpdateInterval() {
        Arrival first = null;
        float firstTime;
        float interval;

        // We find the soonest arrival and use the interval for that to make sure it gets
        // updated as often as it needs.
        for ( Arrival a : arrivals ) {
            if ( first == null
                    || a.getEstimatedArrivalSeconds() < first.getEstimatedArrivalSeconds() )
            {
                if ( a.getEstimatedArrivalSeconds() >= 0 ) {
                    first = a;
                }
            }
        }

        if ( first == null ) {
            firstTime = 15;
        } else {
            firstTime = first.getEstimatedArrivalSeconds();
        }

        interval = Math.max( MINIMUM_UPDATE_INTERVAL, firstTime * INTERVAL_INCREASE_PER_SECOND );
        interval = Math.min (MAXIMUM_UPDATE_INTERVAL, interval);

        return interval;
    }

    public void updateArrivalTimes(Collection<Arrival> updatedArrivals) {
        Log.d(TAG, "Updating SRDArrival times from "+updatedArrivals.size()+" arrivals");

        
        List<Arrival> arrivalsToDelete = new ArrayList<Arrival>();
        
        for (Arrival update : updatedArrivals) {
            if (update.getDirection().equals(destination) &&
                    update.getRoute().equals(route) &&
                    update.getStop().equals(stop)){


                Arrival a = null;

                // Find an existing arrival to update
                for (Arrival arrival : arrivals) {
                    if (arrival.getVehicleNum().equals(update.getVehicleNum())) {
                        // We lose a little precision here
                        a = arrival;
                        break;
                    }
                }
                // Saving Remulasce From himself - Nighelles
                if (a != null && update.getEstimatedArrivalSeconds() <= 0)
                {
                    arrivalsToDelete.add(a);
                } else {

                    // If there was none, then make one.
                    if (a == null) {
                        a = new Arrival();
                        a.setRoute(route);
                        a.setStop(stop);
                        a.setDestination(destination);
                        a.setVehicle(update.getVehicleNum());
                        a.setScope(isInScope);

                        arrivals.add(a);
                    }
                    
                    a.setEstimatedArrivalSeconds(update.getEstimatedArrivalSeconds());
                }
            }
        }
        for (Arrival arrival : arrivals) {
            if (arrival.getEstimatedArrivalSeconds() <= 0)
            {
                arrivalsToDelete.add(arrival);
            }
        }
        arrivals.removeAll(arrivalsToDelete);
    }

    private Collection<Arrival> sortedArrivals() {

        final List<Arrival> sorted = new ArrayList<Arrival>(arrivals);
        Collections.sort(sorted, new Comparator<Object>() {
            @Override
            public int compare(Object o, Object o2) {

                Arrival a = (Arrival) o;
                Arrival b = (Arrival) o2;

                if (a.getEstimatedArrivalSeconds() < b.getEstimatedArrivalSeconds()) {
                    return -1;
                } else if (a.getEstimatedArrivalSeconds() > b.getEstimatedArrivalSeconds()) {
                    return 1;
                }

                return 0;
            }
        });

        return sorted;
    }

    public Collection<Arrival> getArrivals() {
        return sortedArrivals();
    }

    public Route getRoute() {
        return route;
    }

    public Stop getStop() {
        return stop;
    }

    public Destination getDirection() {
        return destination;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setScope(boolean inScope) {
        this.isInScope = inScope;

        for (Arrival a : arrivals) {
            a.setScope(inScope);
        }

        Log.d(TAG, "Setting scopes: "+inScope);
    }
    public boolean isInScope() {
        return isInScope;
    }
}
