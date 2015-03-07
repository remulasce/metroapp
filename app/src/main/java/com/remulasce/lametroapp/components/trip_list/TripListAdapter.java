package com.remulasce.lametroapp.components.trip_list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.remulasce.lametroapp.display.AndroidMultiArrivalDisplay;
import com.remulasce.lametroapp.dynamic_data.types.MultiArrivalTrip;
import com.remulasce.lametroapp.dynamic_data.types.Trip;

public class TripListAdapter extends ArrayAdapter <Trip> {
    public TripListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AndroidMultiArrivalDisplay multiArrivalDisplay = new AndroidMultiArrivalDisplay((MultiArrivalTrip)getItem(position));
        return multiArrivalDisplay.getView(parent, getContext(), convertView);
    }
}
