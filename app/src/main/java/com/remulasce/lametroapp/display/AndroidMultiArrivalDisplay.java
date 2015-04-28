package com.remulasce.lametroapp.display;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.remulasce.lametroapp.components.tutorial.TutorialManager;
import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.NotifyServiceManager;
import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.Vehicle;
import com.remulasce.lametroapp.java_core.location.GlobalLocationProvider;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Arrival;
import com.remulasce.lametroapp.java_core.dynamic_data.types.MultiArrivalTrip;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Remulasce on 3/5/2015.
 *
 * UI layer turns Trips into Views for Android
 */
public class AndroidMultiArrivalDisplay implements AndroidDisplay{
    private MultiArrivalTrip trip;

    public AndroidMultiArrivalDisplay(MultiArrivalTrip t) {
        this.trip = t;
    }

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
        TextView distance_text = (TextView) rowView.findViewById(R.id.prediction_stop_distance);
        View color_box = rowView.findViewById(R.id.color_box);

        if (trip.parentArrival.getRoute().getColor() != null) {
            String color = trip.parentArrival.getRoute().getColor().color;
            try {
                color_box.setVisibility(View.VISIBLE);
                color_box.setBackgroundColor(Color.parseColor(color));
            } catch (IllegalArgumentException e) {
                color_box.setVisibility(View.INVISIBLE);
                color_box.setBackgroundColor(Color.parseColor("white"));
            }
        } else {
            color_box.setVisibility(View.INVISIBLE);
            color_box.setBackgroundColor(Color.parseColor("white"));
        }

        Route route = trip.parentArrival.getRoute();
        Stop stop = trip.parentArrival.getStop();
        Destination dest = trip.parentArrival.getDirection();

        String routeString = route.getString();
        String stopString = stop.getStopName();
        String destString = dest.getString();

        double distance = trip.getCurrentDistanceToStop();

        distance_text.setText(convertMeteresToDistanceDisplay(distance));

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
        for (Arrival a : trip.parentArrival.getArrivals()) {
            RelativeLayout updateTimeView;

            int seconds = (int) a.getEstimatedArrivalSeconds();
            String vehicle = "Veh " + a.getVehicleNum().getString() + " ";

            // If the bus is long gone, don't add it.
            // But keep displaying the "arrived" text for a little while, so you can
            // see the bus # once you've boarded.
            if (seconds <= -10) {
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

        if (trip.parentArrival.isInScope()) {
            rowView.setVisibility(View.VISIBLE);
        } else {
            rowView.setVisibility(View.INVISIBLE);
        }

        return rowView;
    }

    private String convertMeteresToDistanceDisplay(double distance) {
        double yards = distance * 1.09361;

        if (yards > 100) {
            double miles = distance * 0.000621371;

            if (miles < 10) {
                return (int) (miles * 10) / 10.0 + "mi";
            } else if (miles < 100){
                return (int) (miles + 0.5) + "mi";
            } else {
                return "really far!";
            }
        } else {
            return (int) yards + "yd";
        }
    }

    @Override
    public Trip getTrip() {
        return trip;
    }

    @Override
    public void executeAction( final Context context ) {
        Tracking.setScreenName("Notify Confirm Dialog");

        final View dialogView = View.inflate(context, R.layout.multi_arrival_notify_dialog, null);

        RadioGroup radios = (RadioGroup) dialogView.findViewById(R.id.trip_options_radio_group);


        for (Arrival a : trip.parentArrival.getArrivals()) {
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

        launchNotificationConfirmation(context, dialogView);
    }

    private void setTrackingEventListeners(EditText time, RadioGroup vehicleRadio) {
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

    private void launchNotificationConfirmation(final Context context, final View dialogView) {
        final EditText time = (EditText) dialogView.findViewById(R.id.notify_dialog_time);
        final RadioGroup vehicleRadio = (RadioGroup) dialogView.findViewById(R.id.trip_options_radio_group);

        time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
        });

        setTrackingEventListeners(time, vehicleRadio);

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notify_confirmation_title))
                .setView( dialogView )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tracking.setScreenName("Notify Confirm Accept");

                        Vehicle vehicle;
                        int seconds = 120;

                        seconds = getTime(seconds, time);
                        vehicle = getVehicle(vehicleRadio, dialogView);

                        NotifyServiceManager.SetNotifyService(trip.parentArrival.getStop(), trip.parentArrival.getRoute(),
                                trip.parentArrival.getDirection(), vehicle, seconds, context);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Tracking.setScreenName("Notify Confirm Decline");
                    }
                })
                .show();

        TutorialManager.getInstance().notifyServiceSet();
    }
}
