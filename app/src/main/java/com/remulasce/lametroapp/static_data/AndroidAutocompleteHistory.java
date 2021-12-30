package com.remulasce.lametroapp.static_data;

import android.content.Context;

import com.remulasce.lametroapp.components.omni_bar.AutocompleteEntry;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.RegionalizationHelper;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Remulasce on 3/20/2015.
 *
 * <p>Handles saving & retrieving prioritized autocomplete history entries. We keep a list of past
 * user-selected autocomplete entries that we know about. We serialize this list to disk after every
 * write operation.
 *
 * <p>When entries are requested from us, we update the list from the disk and have each of our
 * internal tracking objects produce a relevant OmniAutoCompleteSelection suitable to be shown to
 * user.
 *
 * <p>The list we produced is not sorted, but has its priority set based on whatever factors are
 * relevant.
 *
 * <p>Currently we just use number of total past uses.
 */
public class AndroidAutocompleteHistory implements AutoCompleteHistoryFiller {
  private static final String TAG = "AndroidAutocompleteHistory";
  public static final String HISTORY_FILE = "autocompleteHistory.ser";
  private static final int MAX_HISTORY_ENTRIES = 20;
  private Context context;

  private List<AutocompleteEntry> historyEntries = new ArrayList<AutocompleteEntry>();

  // Shared preferences saving
  public AndroidAutocompleteHistory(Context c) {
    this.context = c;
  }

  @Override
  public Collection<OmniAutoCompleteEntry> autocompleteHistorySuggestions(String input) {
    Log.d(TAG, "Getting autocomplete entries from file");

    // Check if there's entries saved on disk
    Collection<AutocompleteEntry> savedHistory = getSavedEntries();

    if (savedHistory != null) {
      historyEntries.clear();
      historyEntries.addAll(savedHistory);
    }

    // Now just make the actual returned entries from whatever we have.
    // The underlying autocomplete manager will deal with culling and sorting.
    Collection<OmniAutoCompleteEntry> ret = makeOmniEntriesFromHistory(input);
    Log.d(TAG, "Returned " + ret.size() + " history autocomplete entries");
    return ret;
  }

  // Make all the actual OmniAutoCompleteEntries, which are suitable to be shown directly to user.
  // But, don't make the ones that don't pass the censor. Filter. Thing.
  // Also, don't show anything that involves a non-active region.
  // That keeps region-switching simple in terms of history. If it's got anything out of region,
  // just ignore it.
  private Collection<OmniAutoCompleteEntry> makeOmniEntriesFromHistory(String filter) {
    Collection<OmniAutoCompleteEntry> ret = new ArrayList<OmniAutoCompleteEntry>();

    for (AutocompleteEntry entry : historyEntries) {
      Collection<Stop> stops = entry.getEntry().getStops();

      boolean stopIsInScope = true;
      for (Stop s : stops) {
        // We later check to make sure all stops are in region, in the adapter deal
        if (!RegionalizationHelper.getInstance().getActiveAgencies().contains(s.getAgency())) {
          stopIsInScope = false;
          break;
        }
      }

      if (!stopIsInScope) {
        continue;
      }

      if (entry.passesFilter(filter)) {
        ret.add(entry.getEntry());
      }
    }
    return ret;
  }

  private Collection<AutocompleteEntry> getSavedEntries() {
    FileInputStream fileIn = null;
    Collection<AutocompleteEntry> savedHistory = null;

    // This would be nice to do OFF THE FRIGGIN UI THREAD
    try {
      fileIn = context.openFileInput(HISTORY_FILE);

      ObjectInputStream in = null;
      in = new ObjectInputStream(fileIn);

      Object o = in.readObject();

      savedHistory = (Collection<AutocompleteEntry>) o;

      in.close();
      fileIn.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e1) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "Class not found on load");
      e1.printStackTrace();
    } catch (OptionalDataException e1) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "OptionalDataException on load");
      e1.printStackTrace();
    } catch (StreamCorruptedException e1) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "Stream Corrupted on load");
      e1.printStackTrace();
    } catch (IOException e1) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "IO Exception on load");
      e1.printStackTrace();
    } catch (ClassCastException e) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "Class cast exception on load");
      e.printStackTrace();
    }
    return savedHistory;
  }

  // Assume that we've previously loaded the entries from file.
  // This currently is on UI thread.
  @Override
  public void autocompleteSaveSelection(OmniAutoCompleteEntry selected) {
    if (selected == null) {
      Log.w(TAG, "Tried to write a null autocomplete selection");
      return;
    }

    Log.d(TAG, "Writing autocomplete entry to file: " + selected.toString());

    // Over time entries should naturally decay.
    // We don't actually want to deal with time
    // So actually, every time an entry is selected, decay everyone.
    // Try not to decay the one that was selected by more than what it gains from being selected.
    decayExistingEntries();

    // Don't let us hold onto too many entries.
    // Drop the lowest priority ones when we get too full.
    // Also drop really low priorities occasionally.
    // We need to do this occasionall. Not necessarily here.
    // We do it ahead of the rest of this fxn to guarantee new entries always get added.
    cullLowestPriorityEntries();

    // Check if this selection is already in history
    boolean updated = updateIfAlreadyTracked(selected);

    // Otherwise make a new entry.
    if (!updated) {
      AutocompleteEntry autocompleteEntry = new AutocompleteEntry(selected, selected.toString());
      historyEntries.add(autocompleteEntry);
    }

    // Every time the dropdown is shown, entries are loaded back from file.
    // So you have to make sure to save to disk after you modify the list ever.
    saveEntriesToDisk();
  }

  private AutocompleteEntry getLowestPriorityEntry() {
    AutocompleteEntry lowest = null;

    // We could keep this list sorted, but the priority of these entries could vary
    // based on time of sorting.
    // This is because we may have a 'recency' priority which may not be linear in time,
    //   or other time-based priorities that make the overall P(t) function non-linear.
    for (AutocompleteEntry entry : historyEntries) {
      if (lowest == null || entry.getPriority() < lowest.getPriority()) {
        lowest = entry;
      }
    }

    return lowest;
  }

  private void decayExistingEntries() {
    for (AutocompleteEntry entry : historyEntries) {
      entry.decayEntry();
    }
  }

  private void cullLowestPriorityEntries() {
    boolean tooManyEntries = historyEntries.size() > MAX_HISTORY_ENTRIES;

    // Remove the worst offender if we have more entries than we should hold
    if (tooManyEntries) {
      Log.d(TAG, "Over max history entries, culling lowest priority entry");
      historyEntries.remove(getLowestPriorityEntry());
    }

    // Also, drop really low entries anyway.
    dropReallyLowPriorityEntries();
  }

  private void dropReallyLowPriorityEntries() {
    List<AutocompleteEntry> rem = new ArrayList<AutocompleteEntry>();
    for (AutocompleteEntry entry : historyEntries) {
      if (entry.getPriority() < 0) {
        Log.d(
            TAG,
            "Kicking low-priority ("
                + entry.getPriority()
                + ") autocomplete entry: "
                + entry.toString());
        rem.add(entry);
      }
    }

    historyEntries.removeAll(rem);
  }

  private boolean updateIfAlreadyTracked(OmniAutoCompleteEntry selected) {
    boolean updated = false;
    for (AutocompleteEntry entry : historyEntries) {
      if (entry.matches(selected)) {
        entry.incrementUse();
        updated = true;
        break;
      }
    }
    return updated;
  }

  private void saveEntriesToDisk() {
    try {
      FileOutputStream fos = context.openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE);

      ObjectOutputStream oos;
      oos = new ObjectOutputStream(fos);

      oos.writeObject(historyEntries);

      oos.close();

      Log.d(TAG, "Saved " + historyEntries.size() + " autocomplete history entries to disk");
    } catch (FileNotFoundException e) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "FileNotFound on write");
      e.printStackTrace();
    } catch (NotSerializableException e) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "Object not serializable");
      e.printStackTrace();
    } catch (IOException e) {
      Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "IOException on write");
      e.printStackTrace();
    }
  }
}
