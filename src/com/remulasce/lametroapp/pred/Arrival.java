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
    Stop stopID;
    Vehicle vehicle;

    // Nextrip's most recent prediction for when this thing arrives
    // both in ms since epoch, for sub-second refresh frequency
    long lastPrediction;
    long lastUpdate;

    Trip firstTrip;

    public Arrival() {
        firstTrip = new ArrivalTrip( this );
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

    public void setStopID( Stop stopID ) {
        if ( !LaMetroUtil.isValidStop( stopID ) ) {
            return;
        }
        this.stopID = stopID;
    }

    public Route getRoute() {
        return route;
    }

    public Stop getStopID() {
        return stopID;
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
}
