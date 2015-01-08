package com.remulasce.lametroapp.components;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.remulasce.lametroapp.static_data.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.static_data.OmniAutoCompleteProvider;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;
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
    private String TAG = "OmniAutoCompleteAdapter";

    private ArrayList<OmniAutoCompleteEntry> resultList = new ArrayList<OmniAutoCompleteEntry>();
    private OmniAutoCompleteProvider autocomplete;
    private LocationRetriever locations;

    public OmniAutoCompleteAdapter(Context context, int resource, int textView, OmniAutoCompleteProvider t,
                                   LocationRetriever locations) {
        super(context, resource, textView);
        resultList.add(new OmniAutoCompleteEntry("Test Autocomplete", .1f));
        autocomplete = t;
        this.locations = locations;
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

                    // Prioritize them based on stuff
                    try {
                        prioritizeNearbyStops(results);
                    } catch (Exception e) {e.printStackTrace();};

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

    private void prioritizeNearbyStops(Collection<OmniAutoCompleteEntry> results) {
        Log.d(TAG, "Prioritizing nearby stops");
        for (OmniAutoCompleteEntry entry : results) {
            if (entry.hasLocation()) {
                if (entry.hasStop() && entry.getStop() != null) {
                    Log.v(TAG, "Getting distance to "+entry.getStop().getStopID());
                    double distance = locations.getCurrentDistanceToStop(entry.getStop());
                    Log.v(TAG, "Distance to "+entry.getStop().getStopID()+", " +distance);

                    if (distance > 0) {
                        // Two stage priority:
                        // One applies up to .2 for distances up to 20 miles away
                        // The other prioritizes things in walking distance, 1 mi away
                        float priority = 0;
                        // ~20 miles
                        priority += Math.max( 0,
                                .2f * (float)(1 - (distance / 32000)));
                        // ~1 mile
                        priority += Math.max( 0,
                                .8f * (float)(1 - (distance / 1600)));

                        priority = Math.max(priority, 0);

                        if (priority > 0) {
                            Log.v(TAG, "Adding priority " + priority);
                            entry.addPriority(priority);
                        }
                    }
                }
            }
        }
    }


}
