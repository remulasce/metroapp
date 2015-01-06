package com.remulasce.lametroapp.components;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.remulasce.lametroapp.static_data.OmniAutoCompleteProvider;
import com.remulasce.lametroapp.static_data.StopNameTranslator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/5/2015.
 */
public class OmniAutoCompleteAdapter extends ArrayAdapter implements Filterable
{
    private ArrayList<String> resultList = new ArrayList<String>();
    private OmniAutoCompleteProvider autocomplete;

    public OmniAutoCompleteAdapter(Context context, int resource, OmniAutoCompleteProvider t) {
        super(context, resource);
        resultList.add("Test Autocomplete");
        autocomplete = t;
    }


    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    Collection<String> results = autocomplete.autocomplete(constraint.toString());
                    /*
                    if (results != null) {
                        resultList = new ArrayList<String>(results);
                    } */

                    // Assign the data to the FilterResults
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = new ArrayList<String>((Collection<String>)results.values);
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }




}
