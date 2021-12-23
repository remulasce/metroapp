package com.remulasce.lametroapp.java_core.static_data;

import java.util.Collection;

public interface StopNameTranslator {
  String getStopName(String stopID);

  Collection<String> getStopID(String stopName);
}
