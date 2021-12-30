package com.remulasce.lametroapp.java_core.basic_types;

import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

import java.io.Serializable;

public class Route implements Serializable {
  // 802, 754, etc. Required.
  private String raw = "";

  // Not required yet, for backwards compatibility.
  private Agency agency;

  // Optional. Check null before using.
  private RouteColor color;

  public Route() {}

  public Route(String route) {
    raw = route;
  }

  public Route(String route, RouteColor color) {
    raw = route;
    this.color = color;
  }

  public String getString() {
    return raw;
  }

  public boolean isValid() {
    return raw != null && !raw.isEmpty();
  }

  public void setColor(RouteColor color) {
    this.color = color;
  }

  public RouteColor getColor() {
    return color;
  }

  // Ugh.
  public int hashCode() {
    return raw.hashCode();
  }

  public boolean equals(Object o) {
    if (o.getClass() != this.getClass()) {
      return false;
    }

    return (o.hashCode() == this.hashCode());
  }

  public Agency getAgency() {
    return agency;
  }

  public void setAgency(Agency agency) {
    this.agency = agency;
  }
}
