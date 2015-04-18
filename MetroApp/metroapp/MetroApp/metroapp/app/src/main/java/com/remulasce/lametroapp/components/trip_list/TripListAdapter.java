package com.remulasce.lametroapp.components.trip_list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.remulasce.lametroapp.display.AndroidDisplay;

public class TripListAdapter extends ArrayAdapter <AndroidDisplay> {
    public TripListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AndroidDisplay item1 = getItem(position);

        if (item1 instanceof AndroidDisplay) {
            return item1.getView( parent, getContext(), convertView);
        }

        return null;
    }
}
