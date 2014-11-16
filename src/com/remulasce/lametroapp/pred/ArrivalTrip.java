package com.remulasce.lametroapp.pred;

import android.content.Context;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.MainActivity;

public class ArrivalTrip extends Trip {

    protected Arrival parentArrival;

    public ArrivalTrip( Arrival parentArrival ) {
        this.parentArrival = parentArrival;
    }

    public String toString() {
        if ( parentArrival == null ) {
            return "Invalid parent";
        }

        int seconds = parentArrival.getEstimatedArrivalSeconds();

        return parentArrival.getRoute().getString() + ": " + parentArrival.getDirection().getString() + " \n"
                + "Vehicle " + parentArrival.vehicle.getString()
                + LaMetroUtil.secondsToDisplay( seconds )
                + " (" + seconds + "s)";
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
        return parentArrival.getEstimatedArrivalSeconds() > 0;
    }
}
