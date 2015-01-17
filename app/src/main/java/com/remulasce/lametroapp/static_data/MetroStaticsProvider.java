package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.util.Log;

import com.remulasce.lametroapp.types.BasicLocation;
import com.remulasce.lametroapp.types.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.types.Stop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Remulasce on 1/16/2015.
 */
public class MetroStaticsProvider implements StopLocationTranslator, StopNameTranslator, AutoCompleteStopFiller {
    private static final String TAG = "MetroStaticsProvider";


    private GTFSStopsReader stopsReader;

    private HashMap<String, String> stopNameCache;
    private HashMap<String, Collection<String>> stopIDCache;


    public MetroStaticsProvider(Context context) {
        stopsReader = new GTFSStopsReader(context);
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

    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteStopName(String input) {
        Collection<OmniAutoCompleteEntry> ret = stopsReader.autocompleteStopName(input);

        return ret;
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
