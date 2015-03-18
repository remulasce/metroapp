package com.remulasce.lametroapp.dynamic_data.types;

import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.components.location.GlobalLocationProvider;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;

public class MultiArrivalTrip extends Trip {

    public final StopRouteDestinationArrival parentArrival;

    private long lastLocationUpdate = 0;
    private double lastDistanceToStop = 0;

    public MultiArrivalTrip(StopRouteDestinationArrival parentArrival) {
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

        boolean destinationStartsWithNum = destString.startsWith( routeString );
        
        String destination = (destinationStartsWithNum ? "" : routeString + ": " ) + destString + " \n";
        String stop_ = stopString + "\n";

        return stop_ + destination;
    }

    public int hashCode() {
        return parentArrival.hashCode();
    }
    public boolean equals( Object obj ) {
        if (!(obj instanceof MultiArrivalTrip))
            return false;
        if (obj == this)
            return true;
        
        int ourCode = this.hashCode();
        int theirCode = obj.hashCode();
        return ourCode == theirCode; 
    }
    
    @Override
    public float getPriority() {
        // Priority is just how close the stop is.
        // Extra bonus points for being very close
        // It used to matter when we also prioritized arrival time, but no longer changes anything.
        // 20 miles away you start, you get more at 1 mile.
        float proximity = 0;

        LocationRetriever retriever = GlobalLocationProvider.getRetriever();
        if (retriever != null && System.currentTimeMillis() > lastLocationUpdate + 30000) {
            lastLocationUpdate = System.currentTimeMillis();
            lastDistanceToStop = retriever.getCurrentDistanceToStop(parentArrival.getStop());
        }

        double distance = lastDistanceToStop;

        // ~20 miles
        proximity += Math.max(0,
                .2f * (float) (1 - (distance / 32000)));
        // ~2 miles
        proximity += Math.max(0,
                .8f * (float) (1 - (distance / 3200)));
        proximity = Math.max(proximity, 0);

        return proximity;
    }
    
    @Override
    public boolean isValid() {
        return parentArrival.isInScope();// && parentArrival.getEstimatedArrivalSeconds() > 0;
    }

    public void dismiss() {
        parentArrival.setScope(false);
    }
}
