package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.io.Serializable;

public class OmniAutoCompleteEntry implements Cloneable, Serializable {

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
    public void setPriority(float p) { priority = p; }
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

    // This helps with autocomplete history.
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
