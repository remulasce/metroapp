package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.util.Log;

import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.static_data.StopLocationTranslator;
import com.remulasce.lametroapp.java_core.static_data.StopNameTranslator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Remulasce on 1/16/2015.
 *
 * Just routes everything through the preloded sql provider.
 */
public class MetroStaticsProvider implements StopLocationTranslator, StopNameTranslator, AutoCompleteCombinedFiller {
    private static final String TAG = "MetroStaticsProvider";


    private final SQLPreloadedStopsReader stopsReader;
    private final AutoCompleteHistoryFiller autoCompleteHistoryFiller;

    private HashMap<String, String> stopNameCache;
    private HashMap<String, BasicLocation> stopLocationCache;
    private HashMap<String, Collection<String>> stopIDCache;


    public MetroStaticsProvider(Context context) {
        stopsReader = new SQLPreloadedStopsReader(context, "StopNames.db");
        autoCompleteHistoryFiller = new AndroidAutocompleteHistory(context);
    }

    private void addToCache(Object k, Object v, Map cache, int maxCacheEntries, int numRemoveFull) {
        Log.d(TAG, "Adding "+k+", "+v+" to cache "+cache);

        if (cache.size() > maxCacheEntries) {
            // Remove 100 random entries
            Log.i(TAG, "Cache "+cache+" full, removing random entries");
            List<Object> keysAsArray = new ArrayList<Object>(cache.keySet());
            for (int i = 0; i < numRemoveFull; i++) {
                Random r = new Random();
                cache.remove(keysAsArray.get(r.nextInt(keysAsArray.size())));
            }
        }

        cache.put(k, v);
        Log.i(TAG, cache.size()+" objects in cache");
    }

    public void initialize() {
        stopsReader.initialize();
    }

    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteStopName(String input) {
        Collection<OmniAutoCompleteEntry> ret = new ArrayList<OmniAutoCompleteEntry>();

        Collection<OmniAutoCompleteEntry> sqlReturns = stopsReader.autocompleteStopName(input);
        ret.addAll(sqlReturns);

        return ret;
    }

    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteHistorySuggestions(String input) {
        Log.d(TAG, "Getting autocomplete history entries for "+input);
        return autoCompleteHistoryFiller.autocompleteHistorySuggestions(input);
//
//        Collection<OmniAutoCompleteEntry> historyReturns = new ArrayList<OmniAutoCompleteEntry>();
//
//        OmniAutoCompleteEntry e1 = new OmniAutoCompleteEntry("Patsaouras Transit Plaza", 1.5f);
//        OmniAutoCompleteEntry e2 = new OmniAutoCompleteEntry("Redondo Beach Station", 1.5f);
//
//        e1.setStop(new Stop("Redondo Beach Station"));
//        e2.setStop(new Stop("Patsaouras Transit Plaza"));
//
//        historyReturns.add(e1);
//        historyReturns.add(e2);
//
//        return historyReturns;
    }

    @Override
    public void autocompleteSaveSelection(OmniAutoCompleteEntry selected) {
        Log.d(TAG, "Saving autocomplete entry: "+selected.toString());
        autoCompleteHistoryFiller.autocompleteSaveSelection(selected);

    }


    @Override
    public BasicLocation getStopLocation(Stop stop) {
        if (stopLocationCache == null) {
            Log.d(TAG, "Initializing stoplocation cache");
            stopLocationCache = new HashMap<String, BasicLocation>();
        }

        if (stopLocationCache.containsKey(stop)) {
            Log.v(TAG, "stoplocation cache hit");
            return stopLocationCache.get(stop);
        }

        BasicLocation ret = stopsReader.getStopLocation(stop);

        addToCache(stop, ret, stopLocationCache, 20, 4);

        return ret;
    }

    @Override
    public String getStopName(String stopID) {

        if (stopNameCache == null) {
            Log.d(TAG, "Initializing stopname cache");
            stopNameCache = new HashMap<String, String>();
        }

        if (stopNameCache.containsKey(stopID)) {
            Log.v(TAG, "stopname cache hit");
            return stopNameCache.get(stopID);
        }

        String ret = stopsReader.getStopName(stopID);

        addToCache(stopID, ret, stopNameCache, 20, 4);

        return ret;
    }

    @Override
    public Collection<String> getStopID(String stopName) {
        if (stopIDCache == null) {
            Log.d(TAG, "Initializing stopID cache");
            stopIDCache = new HashMap<String, Collection<String>>();
        }

        if (stopIDCache.containsKey(stopName)) {
            Log.v(TAG, "stopID cache hit");
            return stopIDCache.get(stopName);
        }

        Collection<String> ret = stopsReader.getStopID(stopName);

        addToCache(stopName, ret, stopIDCache, 20, 4);

        return ret;
    }
}
