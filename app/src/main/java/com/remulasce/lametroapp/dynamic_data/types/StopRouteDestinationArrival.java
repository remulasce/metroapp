package com.remulasce.lametroapp.dynamic_data.types;

import android.util.Log;

import com.remulasce.lametroapp.basic_types.Destination;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 2/9/2015.
 *
 * This is all the arrivals of one route to one stop with the same destination.
 * It produces a Trip that shows all arrivals.
 * All the things have to match. So red and purple lines to Union Station from 7th/Metro
 *  require two of these.
 */
public class StopRouteDestinationArrival {


    public static final String TAG = "SRDArrival";
    Stop stop;
    Route route;
    Destination destination;

    Collection<Arrival> arrivals;

    Trip trip;

    private boolean isInScope = false;

    public StopRouteDestinationArrival(Stop s, Route r, Destination d) {
        this.stop = s;
        this.route = r;
        this.destination = d;

        arrivals = new ArrayList<Arrival>();

        Log.d(TAG, "New StopRouteDestinationArrival: "+s+" "+r+" "+d);
    }

    // In seconds
    public float getRequestedUpdateInterval() {
        float interval = 0;
        Log.d(TAG, "GetRequestedUpdateInterval SRDArrival "+interval);
        return interval;
    }

    public void updateArrivalTimes(Collection<Arrival> updatedArrivals) {
        Log.d(TAG, "Updating SRDArrival times from "+updatedArrivals.size()+" arrivals");

        for (Arrival update : updatedArrivals) {
            if (update.getDirection().equals(destination) &&
                    update.getRoute().equals(route) &&
                    update.getStop().equals(stop) ){


                Arrival a = null;

                // Find an existing arrival to update
                for (Arrival arrival : arrivals) {
                    if (arrival.getVehicleNum().equals(update.getVehicleNum())) {
                        // We lose a little precision here
                        a = arrival;
                        break;
                    }
                }

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

    public Collection<Arrival> getArrivals() {
        Log.d(TAG, "GetArrivals");
        return null;
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
        Log.d(TAG, "Setting scopes: "+inScope);
    }
    public boolean isInScope() {
        return isInScope;
    }
}
