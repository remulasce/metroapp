package com.remulasce.lametroapp.dynamic_data.types;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.remulasce.lametroapp.basic_types.Vehicle;
import com.remulasce.lametroapp.components.location.GlobalLocationProvider;
import com.remulasce.lametroapp.components.location.LocationRetriever;

import java.util.ArrayList;
import java.util.List;

public class MultiArrivalTrip extends Trip {

    private final StopRouteDestinationArrival parentArrival;

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

    @Override
    public View getView(ViewGroup parent, Context context, View recycleView) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout rowView;

        if (recycleView != null && recycleView.getId() == R.id.multi_trip_item) {
            rowView = (RelativeLayout)recycleView;
        } else {
            rowView = (RelativeLayout) inflater.inflate(R.layout.multi_trip_item, parent, false);
        }

        TextView stop_text = (TextView) rowView.findViewById(R.id.prediction_stop_name);
        TextView route_text = (TextView) rowView.findViewById(R.id.prediction_destination_name);

        Route route = parentArrival.getRoute();
        Stop stop = parentArrival.getStop();
        Destination dest = parentArrival.getDirection();

        String routeString = route.getString();
        String stopString = stop.getStopName();
        String destString = dest.getString();

        boolean destinationStartsWithNum = destString.startsWith( routeString );
        String routeDestString = (destinationStartsWithNum ? "" : routeString + ": " ) + destString ;

        stop_text.setText(stopString);
        route_text.setText(routeDestString);


        LinearLayout timesLayout = (LinearLayout) rowView.findViewById(R.id.arrival_times);

        List<RelativeLayout> updateViews = new ArrayList<RelativeLayout>();
        // If we change the size of the view, we should invalidate it and redraw.
        boolean sizeChanged = false;
        // Find all the arrival rows we can reuse in this view.
        for (int i = 0; i < timesLayout.getChildCount(); i++) {
            View v = timesLayout.getChildAt(i);

            Object tag = v.getTag();
            if (tag instanceof Arrival) {
                updateViews.add((RelativeLayout) v);
            }
        }

        // Get all the Arrivals displayed
        for (Arrival a : parentArrival.getArrivals()) {

            RelativeLayout updateTimeView;

            int seconds = (int) a.getEstimatedArrivalSeconds();
            String vehicle = "Veh " + a.getVehicleNum().getString() + " ";

            // If the bus already arrived, don't add the display
            if (seconds <= 0) {
                continue;
            }
            // If there's recycled views to use
            if (updateViews.size() > 0) {
                updateTimeView = updateViews.get(0);
                updateViews.remove(0);
            }
            // If there's no recycled views left, make one.
            else {
                sizeChanged = true;
                updateTimeView = (RelativeLayout) inflater.inflate(R.layout.trip_arrival_vehicle_row, timesLayout, false);
                updateTimeView.setTag(a);

                timesLayout.addView(updateTimeView);
            }

            TextView prediction_text_minutes = (TextView) updateTimeView.findViewById(R.id.prediction_time_minutes);
            TextView prediction_text_seconds = (TextView) updateTimeView.findViewById(R.id.prediction_time_seconds);
            TextView vehicle_text = (TextView) updateTimeView.findViewById(R.id.prediction_vehicle);


            prediction_text_minutes.setText(LaMetroUtil.standaloneTimeToDisplay(seconds));
            prediction_text_seconds.setText(LaMetroUtil.standaloneSecondsRemainderTime(seconds));

            vehicle_text.setText(vehicle);

        }

        // Remove extra recycled arrivals
        for (RelativeLayout r : updateViews) {
            sizeChanged = true;
            timesLayout.removeView(r);
        }

        // This might not actually be necessary.
        if (sizeChanged) {
            timesLayout.requestLayout();
            timesLayout.invalidate();

            rowView.requestLayout();
            rowView.invalidate();
        }

        if (parentArrival.isInScope()) {
            rowView.setVisibility(View.VISIBLE);
        } else {
            rowView.setVisibility(View.INVISIBLE);
        }

        return rowView;
    }

    public void executeAction( final Context context ) {
        final Tracker t = Tracking.getTracker(context);

        t.setScreenName("Notify Confirm Dialog");
        t.send(new HitBuilders.AppViewBuilder().build());

        final View dialogView = View.inflate(context, R.layout.multi_arrival_notify_dialog, null);

        RadioGroup radios = (RadioGroup) dialogView.findViewById(R.id.trip_options_radio_group);


        for (Arrival a : parentArrival.getArrivals()) {
            if (a.isInScope() && a.getEstimatedArrivalSeconds() > 0) {
                RadioButton button = new RadioButton(context);
                button.setText("Vehicle " + a.getVehicleNum().getString() + " " + LaMetroUtil.timeToDisplay((int)a.getEstimatedArrivalSeconds()));
                button.setTag(a);

                radios.addView(button);
            }
        }

        RadioButton first = (RadioButton)radios.getChildAt(0);
        if (first != null) {
            radios.check(first.getId());
        }

        launchNotificationConfirmation(context, t, dialogView);
    }

    private void launchNotificationConfirmation(final Context context, final Tracker t, final View dialogView) {
        final EditText time = (EditText) dialogView.findViewById(R.id.notify_dialog_time);
        final RadioGroup vehicleRadio = (RadioGroup) dialogView.findViewById(R.id.trip_options_radio_group);

        setTrackingEventListeners(time, vehicleRadio, t);

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notify_confirmation_title))
                .setView( dialogView )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        t.setScreenName("Notify Confirm Accept");
                        t.send(new HitBuilders.AppViewBuilder().build());

                        Vehicle vehicle = null;
                        int seconds = 120;

                        seconds = getTime(seconds, time);
                        vehicle = getVehicle(vehicleRadio, dialogView);

                        NotifyServiceManager.SetNotifyService(parentArrival.stop, parentArrival.route,
                                parentArrival.destination, vehicle, seconds, context);
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

    private int getTime(int seconds, EditText time) {
        try {
            // Add 60 for rounding.
            seconds = Integer.valueOf(String.valueOf(time.getText())) * 60 + 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }

    private Vehicle getVehicle(RadioGroup vehicleRadio, View dialogView) {
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

    private void setTrackingEventListeners(EditText time, RadioGroup vehicleRadio, final Tracker t) {
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.send( new HitBuilders.EventBuilder()
                        .setCategory("Notify Confirmation")
                        .setAction("Time Changed")
                        .build() );
            }
        });

        vehicleRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                t.send( new HitBuilders.EventBuilder()
                        .setCategory("Notify Confirmation")
                        .setAction("Vehicle Changed")
                        .build() );
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
