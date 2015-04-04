package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.Serializable;

/**
 * Created by Remulasce on 3/22/2015.
 */
public class AutocompleteEntry implements Serializable {
    private static final String TAG = "AutocompleteEntry";
    // Decay .25 priority in 2 weeks.
    private static final double RECENCY_DECAY_PER_MILLIS = .25 / (1000 * 60 * 60 * 24 * 14);

    private String filterText;
    private OmniAutoCompleteEntry entry;
    int timesUsed = 1;
    long lastUsed = 0;

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
        lastUsed = System.currentTimeMillis();
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

    // Priority is how many times we've ever used it, minus how long it's been since our last use.
    public float getPriority() {

        float freqPriority = Math.min(.25f, timesUsed / 100.0f);
        float recPriority = Math.max(-.25f, getRecencyPriorityAdjustment());
        return Math.max(0, freqPriority + recPriority);
    }

    // Don't show suggestions that haven't been used in a while.
    // Returns a negative, so add it to total priority.
    public float getRecencyPriorityAdjustment() {
        long millisSinceUse = System.currentTimeMillis() - lastUsed;


        return (float)(-millisSinceUse * RECENCY_DECAY_PER_MILLIS);
    }
}