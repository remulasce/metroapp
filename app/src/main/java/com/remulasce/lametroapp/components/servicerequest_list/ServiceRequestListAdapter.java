package com.remulasce.lametroapp.components.servicerequest_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;

import java.util.List;

public class ServiceRequestListAdapter extends ArrayAdapter{
    private final View.OnClickListener cancelButtonListener;

    public ServiceRequestListAdapter(Context context, int resource, List<ServiceRequest> values, View.OnClickListener cancelButtonListener) {
        super(context, resource, values);
        this.cancelButtonListener = cancelButtonListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_request_item, parent, false);

        TextView t = (TextView) rowView.findViewById(R.id.service_request_text);
        ImageView b = (ImageView) rowView.findViewById(R.id.service_request_cancel);

        b.setTag(getItem(position));
        b.setOnClickListener(cancelButtonListener);

        t.setText(((ServiceRequest)getItem(position)).getDisplayName());

        return rowView;
    }
}
