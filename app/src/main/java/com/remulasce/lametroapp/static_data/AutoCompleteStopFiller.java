package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;

import java.util.Collection;

/**
 * Created by Remulasce on 1/5/2015.
 */
public interface AutoCompleteStopFiller {
    Collection<OmniAutoCompleteEntry> autocompleteStopName(String input);
}
