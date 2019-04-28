package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;

import java.util.Collection;

public interface AutoCompleteStopFiller {
    Collection<OmniAutoCompleteEntry> autocompleteStopName(String input);
}
