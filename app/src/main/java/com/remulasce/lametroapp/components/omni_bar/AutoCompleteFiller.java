package com.remulasce.lametroapp.components.omni_bar;

import java.util.Collection;

/**
 * Created by Remulasce on 3/20/2015.
 *
 * <p>It makes a list of AutoCompleteEntries for the input bar. Each entry has its priority and
 * action set, but the list doesn't have to be listed yet. The UI element can deal with that.
 */
public interface AutoCompleteFiller {
  public Collection<OmniAutoCompleteEntry> getAutoCompleteEntries(String input);
}
