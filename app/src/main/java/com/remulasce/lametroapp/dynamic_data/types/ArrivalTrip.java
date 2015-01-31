package com.remulasce.lametroapp.dynamic_data.types;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
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
import com.remulasce.lametroapp.components.location.GlobalLocationProvider;
import com.remulasce.lametroapp.components.location.LocationRetriever;

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
        String time = LaMetroUtil.timeToDisplay(seconds);
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
        TextView prediction_text_minutes = (TextView) rowView.findViewById(R.id.prediction_time_minutes);
        TextView prediction_text_seconds = (TextView) rowView.findViewById(R.id.prediction_time_seconds);
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
        prediction_text_minutes.setText(LaMetroUtil.standaloneTimeToDisplay(seconds));
        prediction_text_seconds.setText(LaMetroUtil.standaloneSecondsRemainderTime(seconds));
        vehicle_text.setText(vehicle);

        return rowView;
    }

    public void executeAction( final Context context ) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notify_confirmation_title))
                .setMessage(context.getString(R.string.notify_confirmation_text))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotifyServiceManager.SetNotifyService(parentArrival.stop, parentArrival.route,
                                parentArrival.destination, parentArrival.vehicle, context);
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
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
        // 1.0 priority is equivalent to one arriving-now or one current-stop.
        // Current implementation prioritizes mainly distance on arrivals 10m away
        // Then it's a combination of distance/time.

        // 10 minutes away is where you start getting good priority.
        // After that you just get chump change up to 2 hours.
        float eta = parentArrival.getEstimatedArrivalSeconds();
        float time =  Math.max(0, .9f * (1.0f - eta / 600f ) );

        time += Math.max( 0, .1f * (1 - eta / (60f * 60 * 2) ) );

        // 20 miles away you start, you get more at 1 mile.
        float proximity = 0;

        LocationRetriever retriever = GlobalLocationProvider.getRetriever();
        if (retriever != null) {
            double distance = retriever.getCurrentDistanceToStop(parentArrival.getStop());

            // ~20 miles
            proximity += Math.max(0,
                    .2f * (float) (1 - (distance / 32000)));
            // ~1 mile
            proximity += Math.max(0,
                    .8f * (float) (1 - (distance / 1600)));
            proximity = Math.max(proximity, 0);
        } else {
            proximity = 0;
        }
        float overallPriority = time + proximity;
        return overallPriority;
    }
    
    @Override
    public boolean isValid() {
//        return parentArrival.getEstimatedArrivalSeconds() > 0;
        return parentArrival.isInScope() && parentArrival.getEstimatedArrivalSeconds() > 0;
    }
}
