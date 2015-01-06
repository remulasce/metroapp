package com.remulasce.lametroapp.static_data;

/**
 * Created by Remulasce on 1/5/2015.
 */
public class OmniAutoCompleteEntry {

    String text = "Unassigned";
    float priority = 1;

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

}
