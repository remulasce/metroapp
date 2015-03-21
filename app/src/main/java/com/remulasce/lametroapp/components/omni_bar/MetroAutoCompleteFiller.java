package com.remulasce.lametroapp.components.omni_bar;

import android.util.Log;

import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.static_data.AutoCompleteCombinedFiller;
import com.remulasce.lametroapp.static_data.AutoCompleteHistoryFiller;
import com.remulasce.lametroapp.static_data.AutoCompleteStopFiller;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 3/20/2015.
 * Just puts together autocompleteText entries from history and from a search of the stopname table
 */
public class MetroAutoCompleteFiller implements AutoCompleteFiller {
    private static final String TAG = "MetroAutoCompleteFiller";
    private AutoCompleteStopFiller autocompleteText;
    private AutoCompleteHistoryFiller autocompleteHistory;
    private LocationRetriever stopLocations;

    public MetroAutoCompleteFiller(AutoCompleteCombinedFiller autocompleteText, AutoCompleteHistoryFiller autocompleteHistory, LocationRetriever locations) {
        this.stopLocations = locations;
        this.autocompleteText = autocompleteText;
        this.autocompleteHistory = autocompleteHistory;
    }


    @Override
    public Collection<OmniAutoCompleteEntry> getAutoCompleteEntries(String input) {
        long t = Tracking.startTime();

        // Retrieve the autocomplete results.
        Collection<OmniAutoCompleteEntry> results = new ArrayList<OmniAutoCompleteEntry>();

        Collection<OmniAutoCompleteEntry> historySuggestions = autocompleteHistory.autocompleteHistorySuggestions(input);
        Collection<OmniAutoCompleteEntry> autocompleteSuggestions = autocompleteText.autocompleteStopName(input);

        prioritizeNearbyStops(autocompleteSuggestions);

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



        Tracking.sendTime("AutoComplete", "Perform Filtering", "Total", t);
        return results;
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
                            double distance = stopLocations.getCurrentDistanceToStop(entry.getStop());
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
