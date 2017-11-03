package com.remulasce.lametroapp.java_core.basic_types;

import com.remulasce.lametroapp.java_core.analytics.Log;

import java.io.Serializable;

/**
 * Created by Remulasce on 5/4/2015.
 *
 * One Agency per seperate network-referenced transit provider. (this.raw)
 * eg. lametro, lametro-rail, bart, sf-muni
 *
 * We support all Nextrip agencies by nextrip agency, as well as hardcoded BART gtfs data.
 *
 * LaMetroUtils will figure out how to get a list of arrivals for you given your agency. So you shouldn't
 *   have to deal with it here.
 *
 * The agency boundary is now used for automatic region-switching.
 * Display name is supposed to be more publicly-showable. Don't assume it's short.
 *
 */
public class Agency implements Serializable{
    public String raw = "";
    public String displayName = "";
    public BasicLocation minLatLonBound;
    public BasicLocation maxLatLonBound;

    public Agency(String raw, String displayName, BasicLocation minBound, BasicLocation maxBounds ) {
        this.raw = raw;
        this.displayName = displayName;
        this.minLatLonBound = minBound;
        this.maxLatLonBound = maxBounds;
    }


    // Checks if we have a raw string, a public-display name, and boundary locations.
    public boolean isValid() {
        if (isNameBad()) { return false; }
        if (isBoundsBad()) { Log.w("Agency", "Agency is missing its bounds."); }

        return true;
    }

    private boolean isBoundsBad() {
        return minLatLonBound == null || maxLatLonBound == null;
    }

    public boolean hasBounds() {
        return !isBoundsBad();
    }

    private boolean isNameBad() {
        if (raw == null || raw.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether this Agency matches the provided agency string, plus or minus some common
     * transcriptions (capitalization).
     *
     * It's basically a developer-level hack.
     */
    public boolean matches(String agency) {
        return (!isNameBad()) && raw.matches("(?i:"+agency+")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agency agency = (Agency) o;

        return !(raw != null ? !raw.equals(agency.raw) : agency.raw != null);

    }

    @Override
    public int hashCode() {
        return raw != null ? raw.hashCode() : 0;
    }
}
