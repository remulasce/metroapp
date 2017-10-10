package com.remulasce.lametroapp.display;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.dynamic_data.types.RequestStatusTrip;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Trip;

/**
 * Created by Remulasce on 4/21/2015.
 *
 * Display request status.
 *
 * Should either be nothing (empty view? No display? Null?), a spinning progress bar, or error.
 */
public class AndroidRequestStatusDisplay implements AndroidDisplay {

    RequestStatusTrip parentTrip;

    public AndroidRequestStatusDisplay(RequestStatusTrip trip) {
        parentTrip = trip;
    }

    @Override
    public View getView(ViewGroup parent, Context context, View recycleView) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;

        if (recycleView != null && recycleView.getId() == R.id.request_status_item) {
            rowView = (RelativeLayout)recycleView;
        } else {
            rowView = (RelativeLayout) inflater.inflate(R.layout.request_status_item, parent, false);
        }

        TextView title_text = (TextView) rowView.findViewById(R.id.status_title_text);
        TextView status_text = (TextView) rowView.findViewById(R.id.request_status_text);
        ProgressBar progres_spinner = (ProgressBar) rowView.findViewById(R.id.request_status_progress);

        switch (parentTrip.getRequestStatus()) {
            case NOTHING:
                progres_spinner.setVisibility(View.INVISIBLE);
                status_text.setVisibility(View.VISIBLE);

                status_text.setText("Update received");

                break;
            case SPINNER:
                progres_spinner.setVisibility(View.VISIBLE);
                status_text.setVisibility(View.VISIBLE);

                status_text.setText("Getting predictions from " + parentTrip.getAgencyName() + ".");
                break;
            case EMPTY:
                progres_spinner.setVisibility(View.INVISIBLE);
                status_text.setVisibility(View.VISIBLE);

                status_text.setText("No arrivals found :(");
                break;
            case ERROR:
                progres_spinner.setVisibility(View.INVISIBLE);
                status_text.setVisibility(View.VISIBLE);

                status_text.setText("Error contacting " + parentTrip.getAgencyName() + "!");
                break;
            default:
                // ???
                Log.w("AndroidRequestStatusDisplay", "Unknown servicerequest status");
                progres_spinner.setVisibility(View.INVISIBLE);
                status_text.setVisibility(View.INVISIBLE);

                status_text.setText("Status unknown?");

                break;

        }

        title_text.setText(parentTrip.getTitleText());

        return rowView;
    }

    @Override
    public Trip getTrip() {
        return parentTrip;
    }

    @Override
    public void executeAction(Context c) {

    }
}
