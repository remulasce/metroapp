package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.io.Serializable;
import java.util.List;

public class OmniAutoCompleteEntry implements Cloneable, Serializable, Comparable {

  private String text = "Unassigned";
  private float priority = 1;

  // meh inheritance
  private boolean hasStop = false;
  private List<Stop> stops;

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

  public void setPriority(float p) {
    priority = p;
  }

  @Override
  public String toString() {
    return text;
  }

  public boolean hasLocation() {
    return true;
  }

  public boolean hasStop() {
    return hasStop;
  }

  public void setStops(List<Stop> s) {
    this.stops = s;
    hasStop = true;
  }

  public List<Stop> getStops() {
    return this.stops;
  }

  // This helps with autocomplete history.
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public int compareTo(Object o) {
    OmniAutoCompleteEntry other = (OmniAutoCompleteEntry) o;

    if (other.getPriority() > this.getPriority()) {
      return 1;
    } else if (other.getPriority() < this.getPriority()) {
      return -1;
    } else {
      return 0;
    }
  }
}
