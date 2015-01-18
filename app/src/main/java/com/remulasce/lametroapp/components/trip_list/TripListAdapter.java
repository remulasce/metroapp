package com.remulasce.lametroapp.components.trip_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.dynamic_data.types.Trip;

import java.util.List;

/**
 * Created by Fintan on 1/17/2015.
 */
public class TripListAdapter extends ArrayAdapter <Trip> {
    public TripListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return ((Trip)getItem(position)).getView( parent, getContext() );
    }
}
