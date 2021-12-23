package com.remulasce.lametroapp.java_core.basic_types;

import java.io.Serializable;

/** Created by Remulasce on 1/7/2015. Seriously, it's latitude and longitude. */
public class BasicLocation implements Serializable {
  public final double latitude;
  public final double longitude;

  public BasicLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BasicLocation that = (BasicLocation) o;

    if (Double.compare(that.latitude, latitude) != 0) return false;
    return Double.compare(that.longitude, longitude) == 0;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}
