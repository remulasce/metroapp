package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.basic_types.Stop;

public class OmniAutoCompleteEntry {

    private String text = "Unassigned";
    private float priority = 1;

    // meh inheritance
    private boolean hasStop = false;
    private Stop stop = null;


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

    public boolean hasStop() { return hasStop; }
    public void setStop(Stop s) { this.stop = s; hasStop = true; }
    public Stop getStop() {
        return this.stop;
    }

}
