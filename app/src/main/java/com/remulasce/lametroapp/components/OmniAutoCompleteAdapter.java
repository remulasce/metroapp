package com.remulasce.lametroapp.components;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.remulasce.lametroapp.static_data.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.static_data.OmniAutoCompleteProvider;
import com.remulasce.lametroapp.static_data.StopNameTranslator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Remulasce on 1/5/2015.
 */
public class OmniAutoCompleteAdapter extends ArrayAdapter implements Filterable
{
    private ArrayList<OmniAutoCompleteEntry> resultList = new ArrayList<OmniAutoCompleteEntry>();
    private OmniAutoCompleteProvider autocomplete;

    public OmniAutoCompleteAdapter(Context context, int resource, int textView, OmniAutoCompleteProvider t) {
        super(context, resource, textView);
        resultList.add(new OmniAutoCompleteEntry("Test Autocomplete", .1f));
        autocomplete = t;
    }


    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public OmniAutoCompleteEntry getItem(int index) {
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
                    Collection<OmniAutoCompleteEntry> results = autocomplete.autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    ArrayList<OmniAutoCompleteEntry> n = new ArrayList<OmniAutoCompleteEntry>((Collection<OmniAutoCompleteEntry>)results.values);
                    Collections.sort(n, new Comparator<OmniAutoCompleteEntry>() {
                        @Override
                        public int compare(OmniAutoCompleteEntry omniAutoCompleteEntry, OmniAutoCompleteEntry omniAutoCompleteEntry2) {
                            if (omniAutoCompleteEntry.getPriority() < omniAutoCompleteEntry2.getPriority()) {
                                return 1;
                            } else if (omniAutoCompleteEntry.getPriority() > omniAutoCompleteEntry2.getPriority()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    resultList = n;
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }




}
