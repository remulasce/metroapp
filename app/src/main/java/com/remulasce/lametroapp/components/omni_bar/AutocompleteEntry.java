package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.Serializable;

/**
 * Created by Remulasce on 3/22/2015.
 */
public class AutocompleteEntry implements Serializable {
    private static final String TAG = "AutocompleteEntry";

    private String filterText;
    private OmniAutoCompleteEntry entry;
    int timesUsed = 1;

    public AutocompleteEntry(OmniAutoCompleteEntry entry, String filterText) {
        try {
            this.entry = (OmniAutoCompleteEntry) entry.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        this.filterText = filterText;
    }

    public void incrementUse() {
        timesUsed++;
    }

    public boolean passesFilter(String filter) {
        // Screw capitalization
//        return this.filterText.toLowerCase().startsWith(filter.toLowerCase());
        String regularExpression = ".*\\b" + filter.toLowerCase() + ".*";
        boolean matches = this.filterText.toLowerCase().matches(regularExpression);
        Log.d("Autocomplete filter", "Matching "+this.filterText.toLowerCase()+" "+regularExpression+" "+matches);
        return matches;
    }


    public boolean matches(OmniAutoCompleteEntry other) {
        if (other.hasStop() && entry.hasStop()) {
            return entry.getStop().equals(other.getStop());
        } else {
            // We can't handle this.
            Log.w(TAG, "Tried to save an autocomplete entry with no stop- can't handle");
            return false;
        }
    }

    public OmniAutoCompleteEntry getEntry() {
        try {
            OmniAutoCompleteEntry clone = (OmniAutoCompleteEntry) entry.clone();
            float priority = getPriority();
            clone.setPriority(priority);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public float getPriority() {
        return Math.min(.25f, timesUsed / 100.0f);
    }
}
