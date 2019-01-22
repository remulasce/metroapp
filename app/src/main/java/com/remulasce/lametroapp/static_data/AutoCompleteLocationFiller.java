package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;

import java.util.Collection;

/** Given a location, return stops near the location */
public interface AutoCompleteLocationFiller {
  Collection<OmniAutoCompleteEntry> autocompleteLocationSuggestions(
      Collection<BasicLocation> locations);
}
