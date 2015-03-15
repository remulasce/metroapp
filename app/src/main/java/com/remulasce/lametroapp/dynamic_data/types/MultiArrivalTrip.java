package com.remulasce.lametroapp.dynamic_data.types;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Log;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.Destination;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.basic_types.Vehicle;
import com.remulasce.lametroapp.components.location.GlobalLocationProvider;
import com.remulasce.lametroapp.components.location.LocationRetriever;

public class MultiArrivalTrip extends Trip {

    public static final double M_TO_MI = 0.000621371;
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

    public int getTime(int seconds, EditText time) {
        try {
            // Add 60 for rounding.
            seconds = Integer.valueOf(String.valueOf(time.getText())) * 60 + 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }

    public Vehicle getVehicle(RadioGroup vehicleRadio, View dialogView) {
        Vehicle vehicle = null;

        if (vehicleRadio.getCheckedRadioButtonId() != -1) {
            try {
                int id = vehicleRadio.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) dialogView.findViewById(id);

                vehicle = ((Arrival) radioButton.getTag()).getVehicleNum();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vehicle;
    }

    public void setTrackingEventListeners(EditText time, RadioGroup vehicleRadio) {
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracking.sendEvent("Notify Confirmation", "Time Changed");
            }
        });

        vehicleRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Tracking.sendEvent("Notify Confirmation", "Vehicle Changed");
            }
        });
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
