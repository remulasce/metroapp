package com.remulasce.lametroapp.static_data.hardcoded_hacks;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/**
 * Hardcoded VTA colors
 * Basically just the 2 rapids.
 */
public class HardcodedVTAColors implements RouteColorer {
    @Override
    public RouteColor getColor(Route r) {

        if (r == null || !r.isValid()) { return null; }

        String raw = r.getString();

        if (r.getAgency().matches("VTA")) {
            if (raw.startsWith("522")) { return new RouteColor("#000000"); }
            if (raw.startsWith("523")) { return new RouteColor("#000000"); }
            if (raw.startsWith("900")) { return new RouteColor("#E4691C"); }
            if (raw.startsWith("901")) {
                if (raw.startsWith("901X")) {
                    return new RouteColor("#FdF303");
                } else {
                    return new RouteColor("#01ACD8");
                }
            }
            if (raw.startsWith("902")) { return new RouteColor("#99CC66"); }
        }

        return null;
    }
}
