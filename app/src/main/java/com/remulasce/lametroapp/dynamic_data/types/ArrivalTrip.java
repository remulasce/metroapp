package com.remulasce.lametroapp.dynamic_data.types;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.NotifyServiceManager;
import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.basic_types.Destination;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;

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
//        String raw = " (" + seconds + "s)";
        
        return stop_ 
                + destination
                + vehicle
                + time
                ;//+ raw;
    }

    @Override
    public View getView(ViewGroup parent, Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trip_item, parent, false);

        TextView stop_text = (TextView) rowView.findViewById(R.id.prediction_stop_name);
        TextView route_text = (TextView) rowView.findViewById(R.id.prediction_destination_name);
        TextView prediction_text = (TextView) rowView.findViewById(R.id.prediction_time_estimate);
        TextView vehicle_text = (TextView) rowView.findViewById(R.id.prediction_vehicle);
        ImageButton b = (ImageButton) rowView.findViewById(R.id.service_request_cancel);

        Route route = parentArrival.getRoute();
        Stop stop = parentArrival.getStop();
        Destination dest = parentArrival.getDirection();

        String routeString = route.getString();
        String stopString = stop.getStopName();
        String destString = dest.getString();
        String vehicle = "Veh " + parentArrival.vehicle.getString() + " ";

        boolean destinationStartsWithNum = destString.startsWith( routeString );
        String routeDestString = (destinationStartsWithNum ? "" : routeString + ": " ) + destString ;
        int seconds = parentArrival.getEstimatedArrivalSeconds();

        stop_text.setText(stopString);
        route_text.setText(routeDestString);
        prediction_text.setText(LaMetroUtil.standaloneSecondsToDisplay(seconds));
        vehicle_text.setText(vehicle);

        return rowView;
    }

    public void executeAction( Context context ) {
        NotifyServiceManager.SetNotifyService(parentArrival.stop, parentArrival.route,
                parentArrival.destination, parentArrival.vehicle, context);
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
