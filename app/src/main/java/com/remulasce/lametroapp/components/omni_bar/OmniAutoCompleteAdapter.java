package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.static_data.AutoCompleteCombinedFiller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Adapter for the autocomplete drop-down-list on stop entri field.
 */
public class OmniAutoCompleteAdapter extends ArrayAdapter implements Filterable
{
    private final String TAG = "OmniAutoCompleteAdapter";

    private ArrayList<OmniAutoCompleteEntry> resultList = new ArrayList<OmniAutoCompleteEntry>();
    private final AutoCompleteCombinedFiller autoCompleteCombinedFiller;
    private final LocationRetriever locations;

    private FilterTaskCompleteListener completeListener;
    private final AutoCompleteFiller autoCompleteFiller;

    public OmniAutoCompleteAdapter(Context context, int resource, int textView, AutoCompleteCombinedFiller t,
                                   LocationRetriever locations) {
        super(context, resource, textView);
        autoCompleteCombinedFiller = t;
        this.locations = locations;

        autoCompleteFiller = new MetroAutoCompleteFiller(autoCompleteCombinedFiller, autoCompleteCombinedFiller, locations);
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

                    Collection<OmniAutoCompleteEntry> results = autoCompleteFiller.getAutoCompleteEntries(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0 ) {
                    try {
                        ArrayList<OmniAutoCompleteEntry> n = new ArrayList<OmniAutoCompleteEntry>((Collection<OmniAutoCompleteEntry>) results.values);
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
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Results of omniautocomplete publish results not expected");
                    }
                }
                else {
                    notifyDataSetInvalidated();
                }
                if (constraint != null) {
                    completeListener.filterCompletionDetails(constraint.toString());
                } else {
                    completeListener.filterCompletionDetails(null);
                }
            }};
        return filter;
    }

    public void setCompleteListener(FilterTaskCompleteListener completeListener) {
        this.completeListener = completeListener;
    }
}
