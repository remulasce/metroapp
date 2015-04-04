package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.Serializable;

/**
 * Created by Remulasce on 3/22/2015.
 */
public class AutocompleteEntry implements Serializable {
    private static final String TAG = "AutocompleteEntry";
    // Decay .1 priority in  weeks.
    private static final double RECENCY_DECAY_PER_MILLIS = .1 / (1000 * 60 * 60 * 24 * 7);

    private String filterText;
    private OmniAutoCompleteEntry entry;

    int timesUsed = 1;
    long lastUsed = 0;
    int timesKicked = 0;

    public AutocompleteEntry(OmniAutoCompleteEntry entry, String filterText) {
        try {
            this.entry = (OmniAutoCompleteEntry) entry.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        this.filterText = filterText;
        lastUsed = System.currentTimeMillis();
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

    // We should slowly lose relevance over time in a way that overrules total selections.
    // This does that.
    //
    // Algorithm maintains your rank if you're selected 1% of the time.
    public void decayEntry() {
        timesKicked++;
    }

    // Priority is how many times we've ever used it, minus how long it's been since our last use.
    // Priority [-.3, .25f]
    // Negative priorities suggest the entry should be dropped.
    public float getPriority() {

        float freqPriority = Math.min(.25f, timesUsed / 10.0f);

        // 100 kicks gets you 1 frequency use
        float kickPriority = Math.max(-.2f, -timesKicked / 10.0f / 100.0f);
        float recPriority = Math.max(-.1f, getRecencyPriorityAdjustment());

        return freqPriority + recPriority + kickPriority;
    }

    // Show recently-used predictions more, and less-recent less.
    // Range [-.1, .1]
    public float getRecencyPriorityAdjustment() {
        long millisSinceUse = System.currentTimeMillis() - lastUsed;

        return (float)(0.1f - millisSinceUse * RECENCY_DECAY_PER_MILLIS);
    }
}
