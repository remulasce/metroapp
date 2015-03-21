package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;

import java.util.Collection;

/**
 * Created by Remulasce on 3/20/2015.
 */
public interface AutoCompleteHistoryFiller {
    Collection<OmniAutoCompleteEntry> autocompleteHistorySuggestions(String input);
    void autocompleteSaveSelection(OmniAutoCompleteEntry selected);
}
