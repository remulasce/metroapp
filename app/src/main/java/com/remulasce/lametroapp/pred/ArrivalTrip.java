package com.remulasce.lametroapp.pred;

import android.content.Context;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.MainActivity;
import com.remulasce.lametroapp.types.Destination;
import com.remulasce.lametroapp.types.Route;
import com.remulasce.lametroapp.types.Stop;

public class ArrivalTrip extends Trip {

    protected Arrival parentArrival;

    public ArrivalTrip( Arrival parentArrival ) {
        this.parentArrival = parentArrival;
    }

    public String toString() {
        if ( parentArrival == null ) {
            return "Invalid parent";
        }

        Route route = parentArrival.getRoute();
        Stop stop = parentArrival.getStop();
        Destination dest = parentArrival.getDirection();
        
        String routeString = route.getString();
        String stopString = stop.getStopName();
        String destString = dest.getString();
        int seconds = parentArrival.getEstimatedArrivalSeconds();
        
        boolean destinationStartsWithNum = destString.startsWith( routeString );
        
        String destination = (destinationStartsWithNum ? "" : routeString + ": " ) + destString + " \n";
        String stop_ = stopString + "\n";
        String vehicle = "Vehicle " + parentArrival.vehicle.getString() + " "; 
        String time = LaMetroUtil.secondsToDisplay( seconds );
        String raw = " (" + seconds + "s)";
        
        return stop_ 
                + destination
                + vehicle
                + time
                + raw;
    }

    public void executeAction( Context context ) {
        MainActivity.SetNotifyService( parentArrival.stop, parentArrival.route,
                                       parentArrival.destination, parentArrival.vehicle, context );
    }
    
    public int hashCode() {
        return parentArrival.hashCode();
    }
    public boolean equals( Object obj ) {
        if (!(obj instanceof ArrivalTrip))
            return false;
        if (obj == this)
            return true;
        
        int ourCode = this.hashCode();
        int theirCode = obj.hashCode();
        return ourCode == theirCode; 
    }
    
    @Override
    public float getPriority() {
        return 75 - parentArrival.getEstimatedArrivalSeconds() * 2;
    }
    
    @Override
    public boolean isValid() {
//        return parentArrival.getEstimatedArrivalSeconds() > 0;
        return parentArrival.isInScope() && parentArrival.getEstimatedArrivalSeconds() > 0;
    }
}
