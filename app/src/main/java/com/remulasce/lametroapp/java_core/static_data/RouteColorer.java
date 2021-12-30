package com.remulasce.lametroapp.java_core.static_data;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/** Created by Remulasce on 2/25/2015. */
public interface RouteColorer {
  public RouteColor getColor(Route r);
}
