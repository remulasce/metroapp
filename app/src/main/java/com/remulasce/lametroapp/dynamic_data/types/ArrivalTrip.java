package com.remulasce.lametroapp.dynamic_data.types;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.NotifyServiceManager;
import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.analytics.Tracking;
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
        int seconds = (int) parentArrival.getEstimatedArrivalSeconds();
        
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
    public View getView(ViewGroup parent, Context context, View recycleView) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trip_item, parent, false);

        TextView stop_text = (TextView) rowView.findViewById(R.id.prediction_stop_name);
        TextView route_text = (TextView) rowView.findViewById(R.id.prediction_destination_name);
        TextView prediction_text_minutes = (TextView) rowView.findViewById(R.id.prediction_time_minutes);
        TextView prediction_text_seconds = (TextView) rowView.findViewById(R.id.prediction_time_seconds);
        TextView vehicle_text = (TextView) rowView.findViewById(R.id.prediction_vehicle);
        ImageView b = (ImageView) rowView.findViewById(R.id.service_request_cancel);

        Route route = parentArrival.getRoute();
        Stop stop = parentArrival.getStop();
        Destination dest = parentArrival.getDirection();

        String routeString = route.getString();
        String stopString = stop.getStopName();
        String destString = dest.getString();
        String vehicle = "Veh " + parentArrival.vehicle.getString() + " ";

        boolean destinationStartsWithNum = destString.startsWith( routeString );
        String routeDestString = (destinationStartsWithNum ? "" : routeString + ": " ) + destString ;
        int seconds = (int) parentArrival.getEstimatedArrivalSeconds();

        stop_text.setText(stopString);
        route_text.setText(routeDestString);
        prediction_text_minutes.setText(LaMetroUtil.standaloneTimeToDisplay(seconds));
        prediction_text_seconds.setText(LaMetroUtil.standaloneSecondsRemainderTime(seconds));
        vehicle_text.setText(vehicle);

        return rowView;
    }

    public void executeAction( final Context context ) {
        final Tracker t = Tracking.getTracker(context);

        t.setScreenName("Notify Confirm Dialog");
        t.send(new HitBuilders.AppViewBuilder().build());

        final View dialogView = View.inflate(context, R.layout.arrival_notify_dialog, null);

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notify_confirmation_title))
                .setView( dialogView )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        t.setScreenName("Notify Confirm Accept");
                        t.send(new HitBuilders.AppViewBuilder().build());

                        EditText time = (EditText) dialogView.findViewById(R.id.notify_dialog_time);

                        int seconds = 120;

                        try {
                            seconds = Integer.valueOf(String.valueOf(time.getText())) * 60;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        NotifyServiceManager.SetNotifyService(parentArrival.stop, parentArrival.route,
                                parentArrival.destination, parentArrival.vehicle, seconds, context);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        t.setScreenName("Notify Confirm Decline");
                        t.send(new HitBuilders.AppViewBuilder().build());
                    }
                })
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

        // 20 minutes away is where you start getting good priority.
        // After that you just get chump change up to 45m.
        float eta = parentArrival.getEstimatedArrivalSeconds();
        float time =  Math.max(0, .9f * (1.0f - eta / 1200f ) );

        // Super-duper arrivals shouldn't really jump all the way up.
        time = Math.min(time, .8f);

        // Really late arrivals can reduce total priority a little
        time += Math.max( -.2f, .1f * (1 - eta / (60f * 60 * .66f) ) );
        time *= .7f;

        // 20 miles away you start, you get more at 1 mile.
        float proximity = 0;

        LocationRetriever retriever = GlobalLocationProvider.getRetriever();
        if (retriever != null) {
            double distance = retriever.getCurrentDistanceToStop(parentArrival.getStop());

            // ~20 miles
            proximity += Math.max(0,
                    .2f * (float) (1 - (distance / 32000)));
            // ~2 miles
            proximity += Math.max(0,
                    .8f * (float) (1 - (distance / 3200)));
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
