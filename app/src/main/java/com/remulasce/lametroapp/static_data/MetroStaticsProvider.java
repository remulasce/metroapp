package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.util.Log;

import com.remulasce.lametroapp.java_core.RegionalizationHelper;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteEntry;
import com.remulasce.lametroapp.java_core.static_data.StopLocationTranslator;
import com.remulasce.lametroapp.java_core.static_data.StopNameTranslator;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Remulasce on 1/16/2015.
 *
 * Just routes everything through the preloaded sql provider.
 */
public class MetroStaticsProvider implements StopLocationTranslator, StopNameTranslator, AutoCompleteCombinedFiller {
    private static final String TAG = "MetroStaticsProvider";

    private Map<Agency, SQLPreloadedStopsReader> regionalStopsReaders = new HashMap<Agency, SQLPreloadedStopsReader>();

    private final AutoCompleteHistoryFiller autoCompleteHistoryFiller;

    private HashMap<String, String> stopNameCache;
    private HashMap<String, BasicLocation> stopLocationCache;
    private HashMap<String, Collection<String>> stopIDCache;



    public MetroStaticsProvider(Context context) {
        setupRegion(context);

        autoCompleteHistoryFiller = new AndroidAutocompleteHistory(context);
    }

    private void setupRegion(Context context) {
        Collection<Agency> installedAgencies = RegionalizationHelper.getInstance().getInstalledAgencies();
        if (installedAgencies != null) {
            for (Agency agency : installedAgencies) {
                if (!regionalStopsReaders.containsKey(agency)) {
                    // add agency
                    SQLPreloadedStopsReader reader = new SQLPreloadedStopsReader(context, getFileName(agency), agency);

                    regionalStopsReaders.put(agency, reader);
                    Log.i(TAG, "Made autocomplete filler for agency "+agency.raw);
                } else {
                    Log.w(TAG, "Tried to add duplicate agency to statics");
                }
            }
        } else {
            Log.w(TAG, "No regions set!");
        }

    }

    private String getFileName(Agency agency) {
        return agency.raw+".db";
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
        for (SQLPreloadedStopsReader reader : regionalStopsReaders.values()) {
            reader.initialize();
        }
    }

    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteStopName(String input) {
        Collection<OmniAutoCompleteEntry> ret = new ArrayList<OmniAutoCompleteEntry>();

        if (input == null || input.length() < 3) {
            Log.d(TAG, "Returning empty autocomplete list because input was too short");
            return ret;
        }

        long t = Tracking.startTime();


        // Duplicate station names across databases should be treated as the same stop.
        // Eg. Vermont / Athens has both lametr and lametro-rail components
        // So we need a quick way to get existing entries to add additional stops to them.
        Map<String, OmniAutoCompleteEntry> usedStops = new HashMap<String, OmniAutoCompleteEntry>();

        Collection<Agency> activeAgencies = RegionalizationHelper.getInstance().getActiveAgencies();

        for (Agency a : regionalStopsReaders.keySet()) {
            if (!activeAgencies.contains(a)) {
                continue;
            }
            SQLPreloadedStopsReader reader = regionalStopsReaders.get(a);

            Collection<OmniAutoCompleteEntry> entries = reader.autocompleteStopName(input);

            for (OmniAutoCompleteEntry newEntry : entries) {
                if (usedStops.containsKey(newEntry.toString())) {
                    OmniAutoCompleteEntry exstEntry = usedStops.get(newEntry.toString());
                    List<Stop> exstStops = exstEntry.getStops();
                    exstStops.addAll(newEntry.getStops());

                    exstEntry.setStops(exstStops);
                } else {
                    usedStops.put(newEntry.toString(), newEntry);
                }
            }
        }

        ret.addAll(usedStops.values());

        long time = Tracking.timeSpent(t);
        Log.v(TAG, "Total regionalized autocomplete for "+input+" took "+time);

        Tracking.sendTime("SQL", "StopNames", "Combined regionalized autocompleteStopName", t);

        return ret;
    }

    @Override
    public Collection<OmniAutoCompleteEntry> autocompleteHistorySuggestions(String input) {
        Log.d(TAG, "Getting autocomplete history entries for "+input);
        return autoCompleteHistoryFiller.autocompleteHistorySuggestions(input);
    }

    @Override
    public void autocompleteSaveSelection(OmniAutoCompleteEntry selected) {
        Log.d(TAG, "Saving autocomplete entry: " + selected.toString());
        autoCompleteHistoryFiller.autocompleteSaveSelection(selected);

    }


    @Override
    public String getStopName(String stopID) {
        Log.w(TAG, "WARNING: REGIONALIZATION NOT IMPLEMENTED IN THIS FUNCTION");

        return null;
    }

    @Override
    public Collection<String> getStopID(String stopName) {
        Log.w(TAG, "WARNING: REGIONALIZATION NOT IMPLEMENTED IN THIS FUNCTION");

        return null;
    }

    @Override
    public Collection<Route> getRoutesToStop(Stop stop) {
        Collection<Route> ret = new ArrayList<Route>();

        if (stop == null || !stop.isValid()) {
            Log.w(TAG, "Returning empty color list because bad input.");
            return ret;
        }

        Agency stopAgency = stop.getAgency();

        if (stopAgency == null || !stopAgency.isValid()) {
            Log.w(TAG, "Returning empty color list because stop didn't have a valid agency");
            return ret;
        }

        long t = Tracking.startTime();


        SQLPreloadedStopsReader reader = regionalStopsReaders.get(stopAgency);

        if (reader == null) {
            Log.w(TAG, "Don't have preloaded region "+ stopAgency +", can't get routes to stop "+stop);
            return ret;
        }

        Collection<String> entries = reader.getRoutesToStop(stop.getStopID());

        Log.d(TAG, "Received route number list "+entries+" for "+stop);

        for (String routeID : entries) {
            Route r = new Route(routeID);
            r.setAgency(stopAgency);

            ret.add(r);
        }

        long time = Tracking.timeSpent(t);

        Log.d(TAG, "Got all routes to "+stop+", "+ret);

        return ret;

    }

    @Override
    public BasicLocation getStopLocation(Stop stop) {
        return null;
    }
}
