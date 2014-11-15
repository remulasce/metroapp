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
                + "Vehicle " + parentArrival.vehicle.getString() + " in "
                + LaMetroUtil.secondsToDisplay( seconds )
                + " (" + seconds + "s)";
    }

    public void executeAction( Context context ) {
        MainActivity.SetNotifyService( parentArrival.stop, parentArrival.route,
                                       parentArrival.destination, parentArrival.vehicle, context );
    }
}
