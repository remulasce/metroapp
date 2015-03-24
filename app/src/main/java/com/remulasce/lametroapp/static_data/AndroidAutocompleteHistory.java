package com.remulasce.lametroapp.static_data;

import android.content.Context;

import com.remulasce.lametroapp.components.omni_bar.AutocompleteEntry;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.spec.OAEPParameterSpec;

/**
 * Created by Remulasce on 3/20/2015.
 *
 * Handles saving & retrieving prioritized autocomplete history entries.
 * We keep a list of past user-selected autocomplete entries that we know about.
 * We serialize this list to disk after every write operation.
 *
 * When entries are requested from us, we update the list from the disk and have
 *   each of our internal tracking objects produce a relevant OmniAutoCompleteSelection
 *   suitable to be shown to user.
 *
 * The list we produced is not sorted, but has its priority set based on whatever factors
 *   are relevant.
 *
 * Currently we just use number of total past uses.
 *
 *
 */
public class AndroidAutocompleteHistory implements AutoCompleteHistoryFiller {
    private static final String TAG = "AndroidAutocompleteHistory";
    public static final String HISTORY_FILE = "autocompleteHistory.ser";
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
        Collection<OmniAutoCompleteEntry> ret = makeOmniEntriesFromHistory();

        return ret;
    }

    private Collection<OmniAutoCompleteEntry> makeOmniEntriesFromHistory() {
        Collection<OmniAutoCompleteEntry> ret = new ArrayList<OmniAutoCompleteEntry>();

        for (AutocompleteEntry entry : historyEntries) {
            ret.add(entry.getEntry());
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

    @Override
    public void autocompleteSaveSelection(OmniAutoCompleteEntry selected) {
        if (selected == null) {
            Log.w(TAG, "Tried to write a null autocomplete selection");
            return;
        }

        Log.d(TAG, "Writing autocomplete entry to file: " + selected.toString());

        // Check if this selection is already in history
        boolean updated = updateIfAlreadyTracked(selected);

        // Otherwise make a new entry.
        if (!updated) {
            AutocompleteEntry autocompleteEntry = new AutocompleteEntry(selected);
            historyEntries.add(autocompleteEntry);
        }

        // This currently is on UI thread.
        saveEntriesToDisk();

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
        } catch (FileNotFoundException e) {
            Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "FileNotFound on write");
            e.printStackTrace();
        } catch (IOException e) {
            Tracking.sendEvent("Errors", "AndroidAutocompleteHistory", "IOException on write");
            e.printStackTrace();
        }
    }
}
