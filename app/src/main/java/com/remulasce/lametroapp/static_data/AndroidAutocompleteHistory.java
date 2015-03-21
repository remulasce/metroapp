package com.remulasce.lametroapp.static_data;

import android.content.Context;

import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.spec.OAEPParameterSpec;

/**
 * Created by Remulasce on 3/20/2015.
 *
 */
public class AndroidAutocompleteHistory implements AutoCompleteHistoryFiller {
    private static final String TAG = "AndroidAutocompleteHistory";
    private Context context;

    private File historyFile;
    private FileOutputStream writeFile;
    private BufferedReader readFile;

    private List<AutocompleteEntry> historyEntries = new ArrayList<AutocompleteEntry>();

    // Shared preferences saving
    public AndroidAutocompleteHistory(Context c) {
        this.context = c;

        this.historyFile = new File(context.getFilesDir(), "autocompleteHistory.txt");
    }


    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteHistorySuggestions(String input) {
        Log.d(TAG, "Getting autocomplete entries from file");

        Collection<OmniAutoCompleteEntry> ret = new ArrayList<OmniAutoCompleteEntry>();

        for (AutocompleteEntry entry : historyEntries) {
            ret.add(entry.getEntry());
        }

        if (ret.size() == 0) {
            ret.add(new OmniAutoCompleteEntry("Test", 0));
        }

        return ret;
    }

    @Override
    public void autocompleteSaveSelection(OmniAutoCompleteEntry selected) {
        Log.d(TAG, "Writing autocomplete entry to file: " + selected.toString());

        boolean updated = false;
        for (AutocompleteEntry entry : historyEntries) {
            if (entry.matches(selected)) {
                entry.incrementUse();
                updated = true;
                break;
            }
        }

        if (!updated) {
            AutocompleteEntry autocompleteEntry = new AutocompleteEntry(selected);
            historyEntries.add(autocompleteEntry);
        }
    }

    private class AutocompleteEntry {
        private OmniAutoCompleteEntry entry;
        int timesUsed = 1;

        private AutocompleteEntry(OmniAutoCompleteEntry entry) {
            try {
                this.entry = (OmniAutoCompleteEntry) entry.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        private void incrementUse() {
            timesUsed++;
        }

        private boolean matches(OmniAutoCompleteEntry other) {
            if (other.hasStop() && entry.hasStop()) {
                return entry.getStop().equals(other.getStop());
            } else {
                // We can't handle this.
                Log.w(TAG, "Tried to save an autocomplete entry with no stop- can't handle");
                return false;
            }
        }

        private OmniAutoCompleteEntry getEntry() {
            try {
                OmniAutoCompleteEntry clone = (OmniAutoCompleteEntry) entry.clone();
                float priority = Math.min(.5f, timesUsed / 10.0f);
                clone.setPriority(priority);
                return clone;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
