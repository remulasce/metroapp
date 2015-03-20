package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.static_data.AutoCompleteCombinedFiller;
import com.remulasce.lametroapp.static_data.AutoCompleteStopFiller;

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
    private final AutoCompleteCombinedFiller autocomplete;
    private final LocationRetriever locations;

    public OmniAutoCompleteAdapter(Context context, int resource, int textView, AutoCompleteCombinedFiller t,
                                   LocationRetriever locations) {
        super(context, resource, textView);
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
                    long t = Tracking.startTime();

                    // Retrieve the autocomplete results.
                    Collection<OmniAutoCompleteEntry> results = new ArrayList<OmniAutoCompleteEntry>();

                    Collection<OmniAutoCompleteEntry> historySuggestions = autocomplete.autocompleteHistorySuggestions(constraint.toString());
                    Collection<OmniAutoCompleteEntry> autocompleteSuggestions = autocomplete.autocompleteStopName(constraint.toString());

                    results.addAll(historySuggestions);

                    // N^2. Fantastic.
                    // If both history and text suggest the same entry, we need to combine the priorities.
                    for (OmniAutoCompleteEntry newEntry : autocompleteSuggestions) {
                        OmniAutoCompleteEntry matchingEntry = null;

                        for (OmniAutoCompleteEntry exstEntry : results) {
                            if (exstEntry.toString().equals(newEntry.toString())) {
                                matchingEntry = exstEntry;
                                break;
                            }
                        }

                        if (matchingEntry != null) {
                            matchingEntry.addPriority(newEntry.getPriority());
                        } else {
                            results.add(newEntry);
                        }
                    }


                    // Prioritize them based on stuff
                    try {
                        prioritizeNearbyStops(results);
                    } catch (Exception e) {e.printStackTrace();}

                    // Assign the data to the FilterResults
                    filterResults.values = results;
                    filterResults.count = results.size();

                    Tracking.sendTime("AutoComplete", "Perform Filtering", "Total", t);
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
            }};
        return filter;
    }

    private void prioritizeNearbyStops(Collection<OmniAutoCompleteEntry> results) {
        Log.d(TAG, "Prioritizing nearby stops");
        long t = Tracking.startTime();

        ArrayList<Thread> tasks = new ArrayList<Thread>();
        for (OmniAutoCompleteEntry each : results) {
            final OmniAutoCompleteEntry entry = each;
            Runnable task = new Runnable() {
                @Override
                public void run() {
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
            };
            Thread thread = new Thread(task, "LocationPriority task");
            tasks.add(thread);
            thread.start();
        }

        for (Thread thread: tasks) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Finished prioritizing nearby stops in "+Tracking.timeSpent(t));
        Tracking.sendTime("AutoComplete", "Perform Filtering", "PrioritizeNearbyStops", t);
    }


}
