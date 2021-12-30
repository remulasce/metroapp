package com.remulasce.lametroapp.java_core.static_data;

import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

public interface StopLocationTranslator {
  public BasicLocation getStopLocation(Stop stop);
}
