package com.remulasce.lametroapp.static_data;

import java.util.Collection;

/**
 * Created by Remulasce on 1/5/2015.
 */
public interface OmniAutoCompleteProvider {
    Collection<OmniAutoCompleteEntry> autocomplete(String input);
}
