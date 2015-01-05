package com.remulasce.lametroapp.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.types.ServiceRequest;

import java.util.List;

/**
 * Created by Remulasce on 12/16/2014.
 */
public class ServiceRequestListAdapter extends ArrayAdapter{
    public ServiceRequestListAdapter(Context context, int resource, List<ServiceRequest> values) {
        super(context, resource, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_request_item, parent, false);

        TextView t = (TextView) rowView.findViewById(R.id.service_request_text);
        ImageButton b = (ImageButton) rowView.findViewById(R.id.service_request_cancel);

        t.setText(((ServiceRequest)getItem(position)).getDisplayName());

        return rowView;
    }

}
