package com.remulasce.lametroapp.java_core.static_data;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.util.Collection;

/**
 * Created by Remulasce on 6/8/2015.
 *
 * <p>Use to get all routes to a specific stop
 */
public interface StopRoutesTranslator {
  public Collection<Route> getRoutesToStop(Stop stopID);
}
