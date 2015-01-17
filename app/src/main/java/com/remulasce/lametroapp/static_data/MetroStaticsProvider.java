package com.remulasce.lametroapp.static_data;

import android.content.Context;

import com.remulasce.lametroapp.types.BasicLocation;
import com.remulasce.lametroapp.types.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.types.Stop;

import java.util.Collection;

/**
 * Created by Remulasce on 1/16/2015.
 */
public class MetroStaticsProvider implements StopLocationTranslator, StopNameTranslator, AutoCompleteStopFiller {

    private GTFSStopsReader stopsReader;


    public MetroStaticsProvider(Context context) {
        stopsReader = new GTFSStopsReader(context);
    }

    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteStopName(String input) {
        return stopsReader.autocompleteStopName(input);
    }

    @Override
    public BasicLocation getStopLocation(Stop stop) {
        return stopsReader.getStopLocation(stop);
    }

    @Override
    public void initialize() {
        stopsReader.initialize();
    }

    @Override
    public String getStopName(String stopID) {
        return stopsReader.getStopName(stopID);
    }

    @Override
    public Collection<String> getStopID(String stopName) {
        return stopsReader.getStopID(stopName);
    }
}
