package com.remulasce.lametroapp.java_core.basic_types;

import java.io.Serializable;

public class Destination implements Serializable {
  private static final long serialVersionUID = 6069634261484767760L;

  private String raw = "";

  public Destination() {}

  public Destination(String dest) {
    raw = dest;
  }

  public String getString() {
    return raw;
  }

  public boolean isValid() {
    return raw != null && !raw.isEmpty();
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
}
