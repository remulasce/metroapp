package com.remulasce.lametroapp.components.trip_list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.remulasce.lametroapp.dynamic_data.types.Trip;

/**
 * Created by Fintan on 1/17/2015.
 */
public class TripListAdapter extends ArrayAdapter <Trip> {
    public TripListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return ((Trip)getItem(position)).getView( parent, getContext(), convertView);
    }
}
