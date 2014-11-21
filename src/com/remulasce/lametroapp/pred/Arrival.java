package com.remulasce.lametroapp.pred;

import types.Destination;
import types.Route;
import types.Stop;
import types.Vehicle;

import com.remulasce.lametroapp.LaMetroUtil;

/**
 * One arrival is one route-direction's arrival at one stop. This is the
 * building block of all information because it's the only way to get realtime
 * predictions.
 */
public class Arrival {

    Route route;
    Destination destination;
    Stop stop;
    Vehicle vehicle;
    
    protected boolean isInScope = false;

    // Nextrip's most recent prediction for when this thing arrives
    // both in ms since epoch, for sub-second refresh frequency
    long lastPrediction;
    long lastUpdate;

    Trip firstTrip;

    public Arrival() {
        firstTrip = new ArrivalTrip( this );
        
        route = new Route();
        destination = new Destination();
        stop = new Stop();
        vehicle = new Vehicle();
    }

    /** In seconds from now */
    public int getEstimatedArrivalSeconds() {
        return (int) Math.max( 0, ( lastPrediction - System.currentTimeMillis() ) / 1000 );
    }

    public void setEstimatedArrivalSeconds( int secondsTillArrival ) {
        lastPrediction = System.currentTimeMillis() + secondsTillArrival * 1000;
        lastUpdate = System.currentTimeMillis();
    }

    public void setRoute( Route route ) {
        this.route = route;
    }

    public void setVehicle( Vehicle veh ) {
        this.vehicle = veh;
    }

    // In ms
    public long getTimeSinceLastEstimation() {
        return System.currentTimeMillis() - lastUpdate;
    }

    public void updated() {

    }

    public void setDestination( Destination d ) {
        this.destination = d;
    }

    public void setStop( Stop stop ) {
        if ( !stop.isValid() ) {
            return;
        }
        this.stop = stop;
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

    public Vehicle getVehicleNum() {
        return vehicle;
    }

    public Trip getFirstTrip() {
        return firstTrip;
    }
    
    public int hashCode() {
        String h = "";
        if (route.isValid()) h += route.getString();
        if (stop.isValid()) h += stop.getString();
        if (vehicle.isValid()) h += vehicle.getString();
        
        return h.hashCode();
    }

    public void setScope(boolean inScope) {
        this.isInScope = inScope;
    }
    public boolean isInScope() {
        return isInScope;
    }
}
