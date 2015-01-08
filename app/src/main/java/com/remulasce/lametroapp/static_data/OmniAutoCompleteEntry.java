package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.types.Stop;

/**
 * Created by Remulasce on 1/5/2015.
 */
public class OmniAutoCompleteEntry {

    String text = "Unassigned";
    float priority = 1;

    // meh inheritance
    boolean hasStop = false;
    Stop stop = null;


    public OmniAutoCompleteEntry(String text, float priority) {
        this.text = text;
        this.priority = priority;
    }

    public float getPriority() {
        return priority;
    }

    public void addPriority(float diff) {
        priority += diff;
    }
    @Override
    public String toString() {
        return text;
    }

    public boolean hasLocation() { return true; }

    public boolean hasStop() { return true; }
    public void setStop(Stop s) { this.stop = s; hasStop = true; }
    public Stop getStop() {
        return this.stop;
    }

}
