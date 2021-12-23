package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;

import java.util.Collection;

/** Given a location, return stops near the location */
public interface AutoCompleteLocationFiller {
  /**
   * Returns up to maxResults stops within maxDistanceMeters. No guarantee that those stops will be
   * the closest ones. All returned entries come with priority 1 by default.
   */
  Collection<OmniAutoCompleteEntry> autocompleteLocationSuggestions(
      Collection<BasicLocation> locations, float maxDistanceMeters, int maxResults);
}
