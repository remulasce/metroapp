package com.remulasce.lametroapp.static_data;

import android.content.Context;

import com.remulasce.lametroapp.components.omni_bar.AutocompleteEntry;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.analytics.Log;
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

        FileInputStream fileIn = null;
        Collection<AutocompleteEntry> savedHistory = null;
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
            e1.printStackTrace();
        } catch (OptionalDataException e1) {
            e1.printStackTrace();
        } catch (StreamCorruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (savedHistory != null) {
            historyEntries.clear();
            historyEntries.addAll(savedHistory);
        }


        Collection<OmniAutoCompleteEntry> ret = new ArrayList<OmniAutoCompleteEntry>();

        for (AutocompleteEntry entry : historyEntries) {
            ret.add(entry.getEntry());
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

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE);

            ObjectOutputStream oos;
            oos = new ObjectOutputStream(fos);

            oos.writeObject(historyEntries);

            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
