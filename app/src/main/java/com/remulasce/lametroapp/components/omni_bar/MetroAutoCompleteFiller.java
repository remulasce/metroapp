package com.remulasce.lametroapp.components.omni_bar;

import android.arch.core.util.Function;
import android.location.Location;
import android.util.Log;

import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.static_data.AutoCompleteCombinedFiller;
import com.remulasce.lametroapp.static_data.AutoCompleteHistoryFiller;
import com.remulasce.lametroapp.static_data.AutoCompleteLocationFiller;
import com.remulasce.lametroapp.static_data.AutoCompleteStopFiller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Remulasce on 3/20/2015. Just puts together autoCompleteStops entries from history and
 * from a search of the stopname table
 */
public class MetroAutoCompleteFiller implements AutoCompleteFiller {
  private static final String TAG = "MetroAutoCompleteFiller";
  private AutoCompleteStopFiller autoCompleteStops;
  private AutoCompleteHistoryFiller autocompleteHistory;
  private AutoCompleteLocationFiller autoCompleteLocationFiller;
  private InterestedLocationsProvider interestedLocationsProvider;
  private LocationRetriever stopLocations;

  public MetroAutoCompleteFiller(
      AutoCompleteCombinedFiller autoCompleteStops,
      AutoCompleteHistoryFiller autocompleteHistory,
      AutoCompleteLocationFiller autoCompleteLocation,
      InterestedLocationsProvider interestedLocationsProvider,
      LocationRetriever locations) {
    this.interestedLocationsProvider = interestedLocationsProvider;
    this.stopLocations = locations;
    this.autoCompleteStops = autoCompleteStops;
    this.autocompleteHistory = autocompleteHistory;
    this.autoCompleteLocationFiller = autoCompleteLocation;
  }

  @Override
  public Collection<OmniAutoCompleteEntry> getAutoCompleteEntries(String input) {
    long t = Tracking.startTime();

    // Retrieve the autocomplete results.
    Collection<OmniAutoCompleteEntry> results = new ArrayList<OmniAutoCompleteEntry>();

    Collection<OmniAutoCompleteEntry> historySuggestions =
        autocompleteHistory.autocompleteHistorySuggestions(input);
    Collection<OmniAutoCompleteEntry> textSuggestions =
        autoCompleteStops.autocompleteStopName(input);
    Collection<OmniAutoCompleteEntry> locationSuggestions =
            autoCompleteLocationFiller.autocompleteLocationSuggestions(
                    interestedLocationsProvider.getInterestingLocations());

    prioritizeNearbySuggestions(textSuggestions);
    prioritizeNearbySuggestions(historySuggestions, 0.5f);

    // Tempting to put these before priority work, but don't do it.
    limitNumberSuggestions(historySuggestions, 4);
    limitNumberSuggestions(textSuggestions, 16);
    limitNumberSuggestions(locationSuggestions, 4);

    results.addAll(historySuggestions);
    addInSuggestions(results, textSuggestions);
    addInSuggestions(results, locationSuggestions);


    Tracking.sendTime("AutoComplete", "Perform Filtering", "Total", t);
    return results;
  }

  private void addInSuggestions(Collection<OmniAutoCompleteEntry> original,
                                Collection<OmniAutoCompleteEntry> other) {
    // N^2. Fantastic.
    // If both original and other are the same suggestion, we need to combine the priorities.
    for (OmniAutoCompleteEntry newEntry : other) {
      OmniAutoCompleteEntry matchingEntry = null;

      for (OmniAutoCompleteEntry exstEntry : original) {
        if (exstEntry.toString().equals(newEntry.toString())) {
          matchingEntry = exstEntry;
          break;
        }
      }

      if (matchingEntry != null) {
        matchingEntry.addPriority(newEntry.getPriority());
      } else {
        original.add(newEntry);
      }
    }
  }

  // We don't want to overwhelm users with choices.
  // So here's a function to limit all but the N most important suggestions.
  private void limitNumberSuggestions(Collection<OmniAutoCompleteEntry> historySuggestions, int N) {
    Log.d(TAG, "Limit number of suggestions to " + N + " from " + historySuggestions);

    ArrayList<OmniAutoCompleteEntry> sorted =
        new ArrayList<OmniAutoCompleteEntry>(historySuggestions);
    Collections.sort(sorted);

    historySuggestions.clear();
    for (int i = 0; i < N && i < sorted.size(); i++) {
      historySuggestions.add(sorted.get(i));
    }
  }

  private void prioritizeNearbySuggestions(Collection<OmniAutoCompleteEntry> results) {
    prioritizeNearbySuggestions(results, 1);
  }

  private void prioritizeNearbySuggestions(
      Collection<OmniAutoCompleteEntry> results, final float scaleFactor) {
    Log.d(TAG, "Prioritizing nearby stops");
    long t = Tracking.startTime();

    ArrayList<Thread> tasks = new ArrayList<Thread>();
    for (OmniAutoCompleteEntry each : results) {
      final OmniAutoCompleteEntry entry = each;
      Runnable task =
          new Runnable() {
            @Override
            public void run() {
              innerPrioritizeNearbySuggestion(entry, scaleFactor);
            }
          };
      Thread thread = new Thread(task, "LocationPriority task");
      tasks.add(thread);
      thread.start();
    }

    for (Thread thread : tasks) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Log.d(TAG, "Finished prioritizing nearby stops in " + Tracking.timeSpent(t));
    Tracking.sendTime("AutoComplete", "Perform Filtering", "PrioritizeNearbyStops", t);
  }

  private void prioritizeSuggestionsCloseToExistingStops(
          Collection<OmniAutoCompleteEntry> results, Collection<Stop> existingStops, final float scaleFactor) {
    Log.d(TAG, "Prioritizing nearby stops");
    long t = Tracking.startTime();

    ArrayList<Thread> tasks = new ArrayList<Thread>();
    for (OmniAutoCompleteEntry each : results) {
      final OmniAutoCompleteEntry entry = each;
      Runnable task =
          new Runnable() {
            @Override
            public void run() {
              innerPrioritizeNearbySuggestion(entry, scaleFactor);
            }
          };
      Thread thread = new Thread(task, "LocationPriority task");
      tasks.add(thread);
      thread.start();
    }

    for (Thread thread : tasks) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Log.d(TAG, "Finished prioritizing nearby stops in " + Tracking.timeSpent(t));
    Tracking.sendTime("AutoComplete", "Perform Filtering", "PrioritizeNearbyStops", t);
  }

  private void innerPrioritizeNearbySuggestion(
      final OmniAutoCompleteEntry entry, float scaleFactor) {
    innerPrioritizeSuggestionDistances(
        entry,
        new Function<Void, Double>() {
          @Override
          public Double apply(Void input) {
            float minDistance = Float.MAX_VALUE;

            for (BasicLocation interestingLoc : interestedLocationsProvider.getInterestingLocations()) {
              float dist = getDistanceBetween(interestingLoc, entry.getStops().get(0).getLocation());

              minDistance = Math.min(dist, minDistance);
            }

            return (double) minDistance;
          }

          private float getDistanceBetween(BasicLocation interestingLoc, BasicLocation stopLocation) {
            float[] results = new float[4];

            Location.distanceBetween(
                    interestingLoc.latitude, interestingLoc.longitude,
                    stopLocation.latitude, stopLocation.longitude, results);

            return results[0];
          }
        },
        scaleFactor);
  }

  private void innerPrioritizeSuggestionDistances(
      OmniAutoCompleteEntry entry, Function<Void, Double> distanceFunction, float scaleFactor) {
    if (entry.hasLocation()) {
      if (entry.hasStop() && entry.getStops() != null && entry.getStops().size() > 0) {
        Log.v(TAG, "Getting distance to " + entry.getStops().get(0).getStopID());
        double distance = distanceFunction.apply(null);

        Log.v(TAG, "Distance to " + entry.getStops().get(0).getStopID() + ", " + distance);

        if (distance > 0) {
          // Two stage priority:
          // One applies up to .2 for distances up to 20 miles away
          // The other prioritizes things in walking distance, 1 mi away
          float priority = 0;
          // ~20 miles
          priority += Math.max(0, .2f * (float) (1 - (distance / 32000)));
          // ~1 mile
          priority += Math.max(0, .8f * (float) (1 - (distance / 1600)));

          priority = Math.max(priority, 0);

          priority *= scaleFactor;

          if (priority > 0) {
            Log.v(TAG, "Adding priority " + priority);
            entry.addPriority(priority);
          }
        }
      }
    }
  }
}
