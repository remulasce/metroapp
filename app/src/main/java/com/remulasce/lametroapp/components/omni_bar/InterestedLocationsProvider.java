package com.remulasce.lametroapp.components.omni_bar;

import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.util.Collection;

/**
 * Interface gives the inner autocomplete provider access to which stops the user is already
 * tracking. This makes it easier for it to provide similar or nearby stops, for transfers or multi-
 * stop stations.
 */
public interface InterestedLocationsProvider {
    Collection<BasicLocation> getInterestingLocations();
}
