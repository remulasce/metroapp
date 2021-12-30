package com.remulasce.lametroapp.java_core.location;

import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

/**
 * Created by Remulasce on 1/7/2015.
 *
 * <p>Sort of similar to how Google does it where you don't just request GPS, you request the entire
 * info.
 *
 * <p>But, here we can do predictions based on what train you're on or something.
 */
public interface LocationRetriever {
  public double getCurrentDistanceToStop(Stop stop);

  public double getCurrentDistanceToLocation(BasicLocation location);

  public BasicLocation getCurrentLocation();
}
