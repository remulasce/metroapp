package com.remulasce.lametroapp.java_core.static_data.types;

import java.io.Serializable;

/**
 * Created by Remulasce on 2/25/2015.
 *
 * <p>Use Color.parseColor to actually use this. We don't use the values internally, so keeping it
 * as a string (which is how it comes from Metro, and how Color.parseColor expects it) makes some
 * sense.
 */
public class RouteColor implements Serializable {
  public RouteColor(String color) {
    this.color = color;
  }

  public String color;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteColor that = (RouteColor) o;

    return !(color != null ? !color.equals(that.color) : that.color != null);
  }

  @Override
  public int hashCode() {
    return color != null ? color.hashCode() : 0;
  }
}
