package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.types.BasicLocation;
import com.remulasce.lametroapp.types.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.types.Stop;

import java.util.Collection;

/**
 * Created by Remulasce on 1/16/2015.
 */
public class MetroStaticsProvider implements StopLocationTranslator, StopNameTranslator, OmniAutoCompleteProvider {
    @Override
    public Collection<OmniAutoCompleteEntry> autocomplete(String input) {
        return null;
    }

    @Override
    public BasicLocation getStopLocation(Stop stop) {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public String getStopName(String stopID) {
        return null;
    }

    @Override
    public Collection<String> getStopID(String stopName) {
        return null;
    }
}
