package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.util.Collection;

/**
 * Interface gives the inner autocomplete provider access to additional non-text state about the
 * user. This is used to suggest stops near other stops for connections, and preventing suggestion
 * of stops which are already being tracked.
 */
public interface UserStateProvider {
  /** Return locations the user may be interested in navigating to or near */
  Collection<BasicLocation> getInterestingLocations();

  /** Return stops that are currently being tracked. */
  Collection<Stop> getCurrentlyTrackedStops();
}
